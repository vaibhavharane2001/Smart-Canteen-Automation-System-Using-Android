package com.student.canteen;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.student.canteen.Splashscreen.SplashScreen;

public class StartingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting);

        // Get reference to the buttons
        Button btnAdmin = findViewById(R.id.btnAdmin);
        Button btnUser = findViewById(R.id.btnUser);

        // Set OnClickListener for Admin button
        btnAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to MainActivity
                startActivity(new Intent(StartingActivity.this, MainActivity.class));
                // Finish the StartingActivity
                finish();
            }
        });

        // Set OnClickListener for User button
        btnUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to MainActivity
                startActivity(new Intent(StartingActivity.this, MainActivityUp.class));
                // Finish the StartingActivity
                finish();
            }
        });
    }
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
        Intent intent = new Intent(getApplicationContext(), SplashScreen.class);
        startActivity(intent);
    }
}
