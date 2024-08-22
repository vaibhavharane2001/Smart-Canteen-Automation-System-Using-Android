package com.student.canteen;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.student.canteen.AdminComponent.AdminPanel;
import com.student.canteen.Registration.Registration;
import com.student.canteen.Util.NoInternet;

public class MainActivity extends AppCompatActivity {
  EditText email, password;
  Button button, signupbutton;
  FirebaseAuth firebaseAuth;
  ProgressBar progressBar;
  FrameLayout frameLayout;
  boolean isResetPassword = false;

  DatabaseReference usersRef;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    email = findViewById(R.id.Emailoflogin);
    password = findViewById(R.id.Passwordlogin);
    button = findViewById(R.id.loginbutton);
    signupbutton = findViewById(R.id.signupbutton);
    progressBar = findViewById(R.id.progressBarLoginPage);
    progressBar.setVisibility(View.GONE);
    frameLayout = findViewById(R.id.mainActivityFrameLayout);
    firebaseAuth = FirebaseAuth.getInstance();
    usersRef = FirebaseDatabase.getInstance().getReference("Users");

    button.setOnClickListener(view -> {
      String Email = email.getText().toString().trim();
      String Password = password.getText().toString().trim();

      if (TextUtils.isEmpty(Email)) {
        Toast.makeText(MainActivity.this, "Email is missing", Toast.LENGTH_SHORT).show();
        return;
      }
      if (TextUtils.isEmpty(Password)) {
        Toast.makeText(MainActivity.this, "Password is missing", Toast.LENGTH_SHORT).show();
        return;
      }

      progressBar.setVisibility(View.VISIBLE);
      firebaseAuth.signInWithEmailAndPassword(Email, Password)
              .addOnCompleteListener(MainActivity.this, task -> {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                  // Sign in success, check for approval status
                  checkApprovalStatus();
                } else {
                  // If sign in fails, display a message to the user.
                  Toast.makeText(MainActivity.this, "Check Email and Password",
                          Toast.LENGTH_SHORT).show();
                }
              });
    });

    signupbutton.setOnClickListener(view -> {
      Intent intent = new Intent(getApplicationContext(), Registration.class);
      startActivity(intent);
    });
  }

  private void checkApprovalStatus() {
    FirebaseUser currentUser = firebaseAuth.getCurrentUser();
    if (currentUser != null) {
      String userId = currentUser.getUid();
      usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
          if (dataSnapshot.exists()) {
            Boolean approved = dataSnapshot.child("approved").getValue(Boolean.class);
            if (approved == true) {
              // User is approved, navigate to the appropriate activity
              Intent intent = new Intent(getApplicationContext(), AdminPanel.class);
              startActivity(intent);
            } else {
              // User is not approved, show a message or take appropriate action
              Toast.makeText(MainActivity.this, "Your account is not yet approved.", Toast.LENGTH_SHORT).show();
            }
          }
          else {
            // User is not approved, show a message or take appropriate action
            Toast.makeText(MainActivity.this, "Vendor not exists", Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();
          }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
          // Handle possible database errors
        }
      });
    }
  }


  @Override
  protected void onStart() {
    super.onStart();
    FirebaseUser user = firebaseAuth.getCurrentUser();
    if (user != null) {
      startActivity(new Intent(MainActivity.this,StartingActivity.class));
    }
    if (!isInternetAvailable()) {
      startActivity(new Intent(MainActivity.this, NoInternet.class));
    }
  }

  public boolean isInternetAvailable() {
    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
  }
  private void showNotification() {
    // Create a notification channel (required for Android Oreo and above)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationChannel channel = new NotificationChannel("registration_channel", "Registration Channel", NotificationManager.IMPORTANCE_DEFAULT);
      NotificationManager notificationManager = getSystemService(NotificationManager.class);
      if (notificationManager != null) {
        notificationManager.createNotificationChannel(channel);
      }
    }

    // Build the notification
    NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "registration_channel")
            .setSmallIcon(R.drawable.logo)
            .setContentTitle("Login")
            .setContentText("Login successfully")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

    // Show the notification
    NotificationManager notificationManager = getSystemService(NotificationManager.class);
    if (notificationManager != null) {
      notificationManager.notify(1, builder.build());
    }
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    finishAffinity();
    Intent intent = new Intent(getApplicationContext(), StartingActivity.class);
    startActivity(intent);
  }
}