package com.student.canteen.RegistrationUp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.student.canteen.AdminComponent.AdminPanel;
import com.student.canteen.MainActivity;
import com.student.canteen.MainActivityUp;
import com.student.canteen.R;

public class RegistrationUp extends AppCompatActivity {
  EditText email, phone, fullname, password;
  Button button;
  ProgressBar progressBar;
  FirebaseAuth firebaseAuth;
  Uri documentUri;
  StorageReference storageReference;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.customer_registration);

    email = findViewById(R.id.EmailUp);
    phone = findViewById(R.id.PhoneUp);
    fullname = findViewById(R.id.FullnameUp);
    password = findViewById(R.id.PasswordUp);

    button = findViewById(R.id.signupUp);

    progressBar = findViewById(R.id.progressBarReg);

    firebaseAuth = FirebaseAuth.getInstance();
    storageReference = FirebaseStorage.getInstance().getReference("documents");
    progressBar.setVisibility(View.GONE);

    button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        registerUser();
      }
    });
  }

  private void registerUser() {
    final String Email = email.getText().toString().trim();
    final String Phone = phone.getText().toString().trim();
    final String Fullname = fullname.getText().toString().trim();
    final String Password = password.getText().toString().trim();

    if (TextUtils.isEmpty(Email) || TextUtils.isEmpty(Phone) || TextUtils.isEmpty(Fullname)
            || TextUtils.isEmpty(Password)) {
      Toast.makeText(RegistrationUp.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
      return;
    }

    if (phone.length() < 10) {
      Toast.makeText(RegistrationUp.this, "Phone number should be greater than 10 digits", Toast.LENGTH_SHORT).show();
      return;
    }

    if (Password.length() < 8) {
      Toast.makeText(RegistrationUp.this, "Password should be at least 8 characters", Toast.LENGTH_SHORT).show();
      return;
    }

    progressBar.setVisibility(View.VISIBLE);

    firebaseAuth.createUserWithEmailAndPassword(Email, Password)
            .addOnCompleteListener(RegistrationUp.this, task -> {
              progressBar.setVisibility(View.GONE);
              if (task.isSuccessful()) {
                Toast.makeText(RegistrationUp.this, "User Added Successfully", Toast.LENGTH_SHORT).show();
                // Sign in success, update UI with the signed-in user's information
                UserUp user = new UserUp(
                        Email,
                        Fullname,
                        Phone,
                        Password
                );
                FirebaseDatabase.getInstance().getReference("Users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .setValue(user)
                        .addOnCompleteListener(task1 -> {
                          if (task1.isSuccessful()) {
                            Toast.makeText(RegistrationUp.this, "Success", Toast.LENGTH_SHORT).show();
                            showNotification();
                          }
                        });
                firebaseAuth.signOut();
                Intent intent = new Intent(getApplicationContext(), MainActivityUp.class);
                startActivity(intent);
              } else {
                Toast.makeText(RegistrationUp.this, "Failed to register user", Toast.LENGTH_SHORT).show();
              }
            });

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
            .setContentTitle("User Registration")
            .setContentText("User registered successfully")
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
    Intent intent = new Intent(getApplicationContext(), MainActivityUp.class);
    startActivity(intent);
  }
}
