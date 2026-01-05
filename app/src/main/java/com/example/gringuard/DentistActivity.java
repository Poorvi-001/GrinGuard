package com.example.gringuard;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class DentistActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DentistAdapter adapter;
    private List<Dentist> mainList; // The master list
    private EditText searchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dentist_list);

        searchBar = findViewById(R.id.searchState);
        recyclerView = findViewById(R.id.dentistRecyclerView);

        // 1. Fill the Master List
        mainList = new ArrayList<>();
        mainList.add(new Dentist("Dr. Neeraj Verma", "MDS", "A-101", "9811022334", "neeraj@dent.in", "Delhi"));
        mainList.add(new Dentist("Dr. Rajesh Koppikar", "BDS", "B-504", "9869011223", "raj@dent.in", "Maharashtra"));
        mainList.add(new Dentist("Dr. Uday Mukherjee", "MDS", "C-303", "9433011445", "uday@dent.in", "West Bengal"));

        // 2. Initialize Adapter with a COPY of the master list
        adapter = new DentistAdapter(new ArrayList<>(mainList));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // 3. Search Logic inside the Activity
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterData(s.toString());
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void filterData(String query) {
        List<Dentist> filteredList = new ArrayList<>();
        for (Dentist d : mainList) {
            if (d.state.toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(d);
            }
        }
        // Send the new filtered list to the adapter
        adapter.updateList(filteredList);
    }
}