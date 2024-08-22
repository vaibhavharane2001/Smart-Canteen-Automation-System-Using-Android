package com.student.canteen.Splashscreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.student.canteen.R;
import com.student.canteen.StartingActivity;

public class SplashScreen extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash_screen);

    // Find the "Get Started" button
    Button getStartedButton = findViewById(R.id.getStartedButton);

    // Set click listener for the button
    getStartedButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // Navigate to the StartingActivity when the button is clicked
        Intent intent = new Intent(SplashScreen.this, StartingActivity.class);
        startActivity(intent);
        // Finish the current activity
        finish();
      }
    });
  }
}
