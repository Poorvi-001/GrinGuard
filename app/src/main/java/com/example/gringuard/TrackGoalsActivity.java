package com.example.gringuard;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
    private Map<String, Integer> firebaseProgressMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_goals);

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
        loadSeverityHistory();
    }

    private void setupChart() {
        severityChart.getDescription().setEnabled(false);

        // X-Axis (Bottom: Days 1, 2, 3...)
        XAxis xAxis = severityChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setAxisMinimum(1f); // Start at Day 1

        // Y-Axis (Left side: The Labels)
        YAxis leftAxis = severityChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f); // Start exactly at 0
        leftAxis.setAxisMaximum(3f); // End exactly at 3
        leftAxis.setLabelCount(4, true); // Forces 4 labels: 0, 1, 2, 3

        // This part turns the numbers into the words you want
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value == 0f) return "Healthy (0)";
                if (value == 1f) return "Low (1)";
                if (value == 2f) return "Medium (2)";
                if (value == 3f) return "High (3)";
                return "";
            }
        });

        severityChart.getAxisRight().setEnabled(false); // Hide the right side numbers
    }
    private void loadSeverityHistory() {
        SharedPreferences historyPrefs = getSharedPreferences("SeverityHistory", MODE_PRIVATE);
        Map<String, ?> allEntries = historyPrefs.getAll();

        TreeMap<Integer, Float> sortedMap = new TreeMap<>();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            try {
                int day = Integer.parseInt(entry.getKey());
                float val = severityToFloat(entry.getValue().toString());
                // We allow 0 (Healthy) now
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

    // FIXED: This maps the word "healthy" directly to 0.0
    private float severityToFloat(String sev) {
        if (sev == null) return -1f;
        String s = sev.toLowerCase().trim();

        if (s.equals("healthy")) return 0f; // Plot on the 0 line
        if (s.equals("low"))     return 1f; // Plot on the 1 line
        if (s.equals("medium"))  return 2f; // Plot on the 2 line
        if (s.equals("high"))    return 3f; // Plot on the 3 line

        return -1f;
    }

    private void renderChart(List<Entry> entries) {
        LineDataSet dataSet = new LineDataSet(entries, "Severity Level");
        dataSet.setColor(Color.parseColor("#E91E63"));
        dataSet.setCircleColor(Color.parseColor("#E91E63"));
        dataSet.setLineWidth(2.5f);
        dataSet.setCircleRadius(5f);
        dataSet.setDrawValues(false); // FIXED: Removes that "1.00" text from the dot
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        severityChart.setData(new LineData(dataSet));
        severityChart.invalidate();
    }

    private void updateCalendar() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        tvMonthYear.setText(sdf.format(currentCalendar.getTime()));
        // (Calendar Adapter logic remains the same...)
    }
}