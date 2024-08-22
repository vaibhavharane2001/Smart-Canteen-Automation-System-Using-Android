package com.student.canteen.ReserveTable;



import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.student.canteen.R;

import java.util.Locale;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;

import android.widget.DatePicker;
import android.widget.TimePicker;
import java.util.Calendar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.student.canteen.UserComponent.dashboard.DashboardFragment;


public class ReserveTableActivity extends AppCompatActivity {

    private EditText editTextGuests;
    private Button buttonPickDateTime, buttonReserve,buttoncancel;
    private DatabaseReference reservationsRef;
    private String selectedDateTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve_table);

        editTextGuests = findViewById(R.id.editTextGuests);
        buttonPickDateTime = findViewById(R.id.buttonPickDateTime);
        buttonReserve = findViewById(R.id.buttonReserve);
        buttoncancel = findViewById(R.id.buttonCancel);

        reservationsRef = FirebaseDatabase.getInstance().getReference().child("reservations");

        buttonPickDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDateTimePicker();
            }
        });

        buttonReserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the date/time and guests fields are empty
                String guests = editTextGuests.getText().toString().trim();
                if (selectedDateTime.isEmpty() || guests.isEmpty()) {
                    // Display a toast message if fields are empty
                    Toast.makeText(ReserveTableActivity.this, "Please select date and time, and enter number of guests", Toast.LENGTH_SHORT).show();
                } else {
                    // If fields are not empty, proceed with reserving the table
                    reserveTable();
                    Intent intent = new Intent(ReserveTableActivity.this, DashboardFragment.class);
                    startActivity(intent);
                }
            }
        });


        buttoncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show a toast message indicating cancellation
                Toast.makeText(ReserveTableActivity.this, "Reservation cancelled", Toast.LENGTH_SHORT).show();
                // Optionally, you can add more logic here such as clearing the selectedDateTime and guests fields
                selectedDateTime = "";
                editTextGuests.setText("");

                // Start the DashboardFragment activity after cancellation
                Intent intent = new Intent(ReserveTableActivity.this, DashboardFragment.class);
                startActivity(intent);
            }
        });

    }

    private void openDateTimePicker() {
        // Open DatePicker dialog
        Calendar now = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Handle date selection
                        selectedDateTime = String.format(Locale.getDefault(), "%02d-%02d-%d", dayOfMonth, month + 1, year);
                        // Open TimePicker dialog after selecting date
                        openTimePicker(now);
                    }
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void openTimePicker(Calendar now) {
        // Open TimePicker dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // Handle time selection
                        selectedDateTime += String.format(" %02d:%02d", hourOfDay, minute);
                        buttonPickDateTime.setText(selectedDateTime);
                    }
                },
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true // 24-hour format
        );
        timePickerDialog.show();
    }


    private void reserveTable() {
        String guests = editTextGuests.getText().toString().trim();

        if (selectedDateTime.isEmpty() || guests.isEmpty()) {
            Toast.makeText(ReserveTableActivity.this, "Please select date and time, and enter number of guests", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save reservation to Firebase
        Reservation reservation = new Reservation(selectedDateTime, guests);
        reservationsRef.push().setValue(reservation)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ReserveTableActivity.this, "Table reserved successfully", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(ReserveTableActivity.this, "Failed to reserve table: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}

