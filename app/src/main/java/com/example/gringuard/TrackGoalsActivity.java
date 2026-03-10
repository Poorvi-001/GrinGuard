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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TrackGoalsActivity extends AppCompatActivity {

    private TextView tvMonthYear;
    private GridView calendarGrid;
    private Calendar currentCalendar;
    private CalendarAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_goals);

        tvMonthYear = findViewById(R.id.tvMonthYear);
        calendarGrid = findViewById(R.id.calendarGrid);
        Button btnPrev = findViewById(R.id.btnPrev);
        Button btnNext = findViewById(R.id.btnNext);

        currentCalendar = Calendar.getInstance();
        updateCalendar();

        btnPrev.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, -1);
            updateCalendar();
        });

        btnNext.setOnClickListener(v -> {
            currentCalendar.add(Calendar.MONTH, 1);
            updateCalendar();
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

        adapter = new CalendarAdapter(this, dayList, currentCalendar);
        calendarGrid.setAdapter(adapter);
    }

    private class CalendarAdapter extends BaseAdapter {
        private Context context;
        private List<Date> days;
        private Calendar displayMonth;
        private SharedPreferences prefs;

        public CalendarAdapter(Context context, List<Date> days, Calendar displayMonth) {
            this.context = context;
            this.days = days;
            this.displayMonth = displayMonth;
            this.prefs = context.getSharedPreferences("CalendarProgress", MODE_PRIVATE);
        }

        @Override
        public int getCount() { return days.size(); }

        @Override
        public Object getItem(int position) { return days.get(position); }

        @Override
        public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Date date = days.get(position);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_calendar_day, parent, false);
            }

            TextView tvDay = convertView.findViewById(R.id.tvDay);
            tvDay.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));

            // Gray out days not in the current month
            if (calendar.get(Calendar.MONTH) != displayMonth.get(Calendar.MONTH)) {
                tvDay.setTextColor(Color.LTGRAY);
                tvDay.setBackgroundColor(Color.TRANSPARENT);
            } else {
                tvDay.setTextColor(Color.BLACK);
                
                // HIGHLIGHT LOGIC
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String dateKey = sdf.format(date);
                int progress = prefs.getInt(dateKey, -1);

                if (progress == -1) {
                    tvDay.setBackgroundColor(Color.parseColor("#E0E0E0")); // Default Gray
                } else if (progress == 0) {
                    tvDay.setBackgroundColor(Color.parseColor("#FF5252")); // Red (0%)
                } else if (progress == 100) {
                    tvDay.setBackgroundColor(Color.parseColor("#4CAF50")); // Green (100%)
                } else {
                    tvDay.setBackgroundColor(Color.parseColor("#FFEB3B")); // Yellow (Partial)
                }
            }

            return convertView;
        }
    }
}
