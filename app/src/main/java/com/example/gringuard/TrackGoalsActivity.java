package com.example.gringuard;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class TrackGoalsActivity extends AppCompatActivity {

    private TextView tvMonthYear;
    private GridView calendarGrid;
    private LineChart severityChart;
    private Calendar currentCalendar;
    private CalendarAdapter adapter;
    private Map<String, Integer> firebaseProgressMap = new HashMap<>();

    private String uid, diseaseKey;
    private DatabaseReference db;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_goals);

        tvMonthYear  = findViewById(R.id.tvMonthYear);
        calendarGrid = findViewById(R.id.calendarGrid);
        severityChart = findViewById(R.id.severityChart);
        Button btnPrev = findViewById(R.id.btnPrev);
        Button btnNext = findViewById(R.id.btnNext);

        uid  = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db   = FirebaseDatabase.getInstance().getReference();
        prefs = getSharedPreferences("GringuardPrefs_" + uid, MODE_PRIVATE);
        diseaseKey = prefs.getString("activeDiseaseKey", "");

        currentCalendar = Calendar.getInstance();

        btnPrev.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, -1);
            updateCalendar();
        });
        btnNext.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, 1);
            updateCalendar();
        });

        setupChart();
        loadProgressFromFirebase();
        loadSeverityHistory();
    }

    private void setupChart() {
        severityChart.getDescription().setEnabled(false);
        severityChart.setTouchEnabled(true);
        severityChart.setDragEnabled(true);
        severityChart.setScaleEnabled(true);
        severityChart.setPinchZoom(true);
        severityChart.setDrawGridBackground(false);

        XAxis xAxis = severityChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(7);
        xAxis.setTextColor(Color.parseColor("#E91E63"));

        YAxis leftAxis = severityChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(4f);
        leftAxis.setGranularity(1f);
        leftAxis.setTextColor(Color.parseColor("#E91E63"));
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value == 1f) return "Low";
                if (value == 2f) return "Med";
                if (value == 3f) return "High";
                return "";
            }
        });

        severityChart.getAxisRight().setEnabled(false);
        severityChart.getLegend().setEnabled(false);
    }

    private void loadSeverityHistory() {
        SharedPreferences sevPrefs = getSharedPreferences("SeverityPrefs", MODE_PRIVATE);
        long startTime = sevPrefs.getLong("startTime", 0);
        if (startTime == 0) return;

        // In a real app, you'd store each check in a list. 
        // For this implementation, we'll fetch the current stored severity 
        // and map it to the current day. 
        // To show a proper graph, the app needs to save history.
        // Let's assume you save history in a specific SharedPref.
        
        SharedPreferences historyPrefs = getSharedPreferences("SeverityHistory", MODE_PRIVATE);
        Map<String, ?> allEntries = historyPrefs.getAll();
        
        List<Entry> entries = new ArrayList<>();
        
        // Convert to TreeMap to keep days sorted
        TreeMap<Integer, Float> sortedMap = new TreeMap<>();
        
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            try {
                int day = Integer.parseInt(entry.getKey());
                String sev = entry.getValue().toString();
                float val = 0;
                if (sev.equalsIgnoreCase("low")) val = 1f;
                else if (sev.equalsIgnoreCase("medium")) val = 2f;
                else if (sev.equalsIgnoreCase("high")) val = 3f;
                
                if (val > 0) sortedMap.put(day, val);
            } catch (Exception e) {}
        }

        for (Map.Entry<Integer, Float> entry : sortedMap.entrySet()) {
            entries.add(new Entry(entry.getKey(), entry.getValue()));
        }

        if (entries.isEmpty()) {
            severityChart.setNoDataText("No severity data available yet.");
            severityChart.invalidate();
            return;
        }

        LineDataSet dataSet = new LineDataSet(entries, "Severity Level");
        dataSet.setColor(Color.parseColor("#E91E63"));
        dataSet.setCircleColor(Color.parseColor("#E91E63"));
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawCircleHole(false);
        dataSet.setValueTextSize(9f);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.parseColor("#F8BBD0"));
        dataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);

        LineData lineData = new LineData(dataSet);
        severityChart.setData(lineData);
        severityChart.invalidate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProgressFromFirebase();
        loadSeverityHistory();
    }

    private void loadProgressFromFirebase() {
        if (diseaseKey.isEmpty()) {
            updateCalendar();
            return;
        }

        db.child("Users").child(uid)
                .child("FollowPlan").child(diseaseKey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        firebaseProgressMap.clear();

                        for (DataSnapshot day : snapshot.getChildren()) {
                            String date = day.child("date").getValue(String.class);
                            Integer pct  = day.child("percentage").getValue(Integer.class);
                            if (date != null && pct != null) {
                                firebaseProgressMap.put(date, pct);

                                getSharedPreferences("CalendarProgress_" + uid, MODE_PRIVATE)
                                        .edit().putInt(date, pct).apply();
                            }
                        }
                        updateCalendar();
                    }
                    @Override
                    public void onCancelled(DatabaseError error) {
                        updateCalendar();
                    }
                });
    }

    private void updateCalendar() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        tvMonthYear.setText(sdf.format(currentCalendar.getTime()));

        List<Date> dayList = new ArrayList<>();
        Calendar calendar = (Calendar) currentCalendar.clone();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int monthBeginningCell = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell);

        while (dayList.size() < 42) {
            dayList.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        adapter = new CalendarAdapter(this, dayList, currentCalendar, firebaseProgressMap);
        calendarGrid.setAdapter(adapter);
    }

    private class CalendarAdapter extends BaseAdapter {
        private Context context;
        private List<Date> days;
        private Calendar displayMonth;
        private Map<String, Integer> progressMap;

        public CalendarAdapter(Context context, List<Date> days,
                               Calendar displayMonth, Map<String, Integer> progressMap) {
            this.context      = context;
            this.days         = days;
            this.displayMonth = displayMonth;
            this.progressMap  = progressMap;
        }

        @Override public int getCount() { return days.size(); }
        @Override public Object getItem(int position) { return days.get(position); }
        @Override public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Date date = days.get(position);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            if (convertView == null) {
                convertView = LayoutInflater.from(context)
                        .inflate(R.layout.item_calendar_day, parent, false);
            }

            TextView tvDay = convertView.findViewById(R.id.tvDay);
            tvDay.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));

            if (calendar.get(Calendar.MONTH) != displayMonth.get(Calendar.MONTH)) {
                tvDay.setTextColor(Color.LTGRAY);
                tvDay.setBackgroundColor(Color.TRANSPARENT);
            } else {
                tvDay.setTextColor(Color.BLACK);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String dateKey = sdf.format(date);

                Integer progress = progressMap.get(dateKey);
                if (progress == null) {
                    SharedPreferences calPrefs = context.getSharedPreferences(
                            "CalendarProgress_" + FirebaseAuth.getInstance().getCurrentUser().getUid(),
                            MODE_PRIVATE);
                    int local = calPrefs.getInt(dateKey, -1);
                    progress = (local == -1) ? null : local;
                }

                if (progress == null) {
                    tvDay.setBackgroundColor(Color.parseColor("#E0E0E0"));
                } else if (progress == 0) {
                    tvDay.setBackgroundColor(Color.parseColor("#FF5252"));
                } else if (progress == 100) {
                    tvDay.setBackgroundColor(Color.parseColor("#4CAF50"));
                } else {
                    tvDay.setBackgroundColor(Color.parseColor("#FFEB3B"));
                }
            }
            return convertView;
        }
    }
}
