package com.academicvault;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.academicvault.adapter.Subject_Adapter;
import com.academicvault.database.DatabaseHelper;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

/**
 * Main Activity of the application, serving as the dashboard for subjects.
 */
public class MainActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private RecyclerView recyclerView;
    private Subject_Adapter subjectAdapter;
    private TextView totalSubCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new DatabaseHelper(this);

        setupUI();
        load();
    }

    /**
     * Initializes the UI components and click listeners.
     */
    private void setupUI() {
        recyclerView = findViewById(R.id.subject_recycleview);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }

        totalSubCount = findViewById(R.id.totalsubjects);

        ExtendedFloatingActionButton addSubjectFab = findViewById(R.id.addSubjectFab);
        if (addSubjectFab != null) {
            addSubjectFab.setOnClickListener(v -> showAddSubjectDialog());
        }
    }

    /**
     * Displays a dialog to add a new subject.
     */
    private void showAddSubjectDialog() {
        View dialogBox = getLayoutInflater().inflate(R.layout.add_subject_dialog_box, null);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogBox)
                .create();
        
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        Button addBtn = dialogBox.findViewById(R.id.add_btn);
        EditText subName = dialogBox.findViewById(R.id.subject_name_input);
        TextView cancelBtn = dialogBox.findViewById(R.id.cancel_btn);

        addBtn.setOnClickListener(y -> {
            String name = subName.getText().toString().trim();
            if (insert(name)) {
                dialog.dismiss();
            }
        });

        cancelBtn.setOnClickListener(x -> dialog.dismiss());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the list whenever we return to the main activity
        load();
    }

    /**
     * Loads subjects from the database and populates the RecyclerView.
     */
    public void load() {
        try {
            if (dbHelper == null) {
                dbHelper = new DatabaseHelper(this);
            }
            
            if (recyclerView != null) {
                subjectAdapter = new Subject_Adapter(this, dbHelper.getAllSubjects(), this::load);
                recyclerView.setAdapter(subjectAdapter);
            }

            if (totalSubCount != null && subjectAdapter != null) {
                totalSubCount.setText(String.valueOf(subjectAdapter.getItemCount()));
            }
        } catch (Exception e) {
            Log.e("DATABASE_ERROR", "Error occurred in load(): ", e);
        }
    }

    /**
     * Inserts a new subject into the database.
     * @param subName Name of the subject to add.
     * @return true if insertion was successful, false otherwise.
     */
    private boolean insert(String subName) {
        if (subName.isEmpty()) {
            Toast.makeText(this, "Subject Name cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            long id = dbHelper.insertSubject(subName);
            if (id != -1) {
                Toast.makeText(this, "Subject Added", Toast.LENGTH_SHORT).show();
                load();
                return true;
            } else {
                Toast.makeText(this, "Subject Not Added", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
    }
}
