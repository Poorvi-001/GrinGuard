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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_goals);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
            return;
        }
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        tvMonthYear   = findViewById(R.id.tvMonthYear);
        calendarGrid  = findViewById(R.id.calendarGrid);
        severityChart = findViewById(R.id.severityChart);
        Button btnPrev = findViewById(R.id.btnPrev);
        Button btnNext = findViewById(R.id.btnNext);

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
        updateCalendar();
        loadSeverityHistory();
    }

    private void setupChart() {
        severityChart.getDescription().setEnabled(false);
        severityChart.setNoDataText("No severity data available yet.");

        // X-Axis
        XAxis xAxis = severityChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setAxisMinimum(1f);
        xAxis.setTextColor(Color.parseColor("#E91E63"));

        // Y-Axis
        YAxis leftAxis = severityChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(3f);
        leftAxis.setLabelCount(4, true);
        leftAxis.setTextColor(Color.parseColor("#E91E63"));

        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value == 0f) return "Healthy";
                if (value == 1f) return "Low";
                if (value == 2f) return "Medium";
                if (value == 3f) return "High";
                return "";
            }
        });

        severityChart.getAxisRight().setEnabled(false);
        severityChart.getLegend().setEnabled(false);
    }

    private void loadSeverityHistory() {
        SharedPreferences historyPrefs = getSharedPreferences("SeverityHistory_" + uid, MODE_PRIVATE);
        Map<String, ?> allEntries = historyPrefs.getAll();

        TreeMap<Integer, Float> sortedMap = new TreeMap<>();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            try {
                int day = Integer.parseInt(entry.getKey());
                float val = severityToFloat(entry.getValue().toString());
                if (val != -1f) sortedMap.put(day, val);
            } catch (Exception ignored) {}
        }

        List<Entry> entries = new ArrayList<>();
        for (Map.Entry<Integer, Float> entry : sortedMap.entrySet()) {
            entries.add(new Entry(entry.getKey(), entry.getValue()));
        }

        if (!entries.isEmpty()) {
            renderChart(entries);
        }
    }

    private float severityToFloat(String sev) {
        if (sev == null) return -1f;
        String s = sev.toLowerCase().trim();
        if (s.equals("healthy") || s.equals("healthytooth")) return 0f;
        if (s.equals("low"))     return 1f;
        if (s.equals("medium"))  return 2f;
        if (s.equals("high"))    return 3f;
        return -1f;
    }

    private void renderChart(List<Entry> entries) {
        LineDataSet dataSet = new LineDataSet(entries, "Severity Level");
        dataSet.setColor(Color.parseColor("#E91E63"));
        dataSet.setCircleColor(Color.parseColor("#E91E63"));
        dataSet.setLineWidth(2.5f);
        dataSet.setCircleRadius(5f);
        dataSet.setDrawValues(false);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        severityChart.setData(new LineData(dataSet));
        severityChart.invalidate();
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
                            "CalendarProgress_" + uid,
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
