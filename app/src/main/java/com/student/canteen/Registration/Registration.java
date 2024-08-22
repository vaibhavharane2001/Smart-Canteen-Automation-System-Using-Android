package com.student.canteen.Registration;

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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.student.canteen.AdminComponent.AdminPanel;
import com.student.canteen.MainActivity;
import com.student.canteen.R;

public class Registration extends AppCompatActivity {
  EditText email, phone, fullname, password, shopname, documentName;
  Button button, uploadDocument;
  ProgressBar progressBar;
  FirebaseAuth firebaseAuth;
  Uri documentUri;
  StorageReference storageReference;



  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_registration);



    email = findViewById(R.id.Email);
    phone = findViewById(R.id.Phone);
    fullname = findViewById(R.id.Fullname);
    password = findViewById(R.id.Password);
    shopname = findViewById(R.id.Shopname);
    button = findViewById(R.id.signup);
    uploadDocument = findViewById(R.id.UploadDocument);
    progressBar = findViewById(R.id.progressBarReg);
    documentName = findViewById(R.id.DocumentName);
    firebaseAuth = FirebaseAuth.getInstance();
    storageReference = FirebaseStorage.getInstance().getReference("documents");
    progressBar.setVisibility(View.GONE);

    documentName = findViewById(R.id.DocumentName);
    uploadDocument = findViewById(R.id.UploadDocument);

    uploadDocument.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        chooseDocument();
      }
    });
    button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        registerUser();
      }
    });
  }

  private void chooseDocument() {
    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
    intent.setType("application/pdf");
    intent.addCategory(Intent.CATEGORY_OPENABLE);
    startActivityForResult(Intent.createChooser(intent, "Select Document"), 123);
  }
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == 123 && resultCode == RESULT_OK && data != null && data.getData() != null) {
      documentUri = data.getData();
      documentName.setText(data.getDataString());
    }
  }
  private void registerUser() {
    final String Email = email.getText().toString().trim();
    final String Phone = phone.getText().toString().trim();
    final String Fullname = fullname.getText().toString().trim();
    final String Password = password.getText().toString().trim();
    final String Shopname = shopname.getText().toString().trim();
    final String DocumentName = documentName.getText().toString().trim();

    if (TextUtils.isEmpty(Email) || TextUtils.isEmpty(Phone) || TextUtils.isEmpty(Fullname)
            || TextUtils.isEmpty(Password) || TextUtils.isEmpty(Shopname) || TextUtils.isEmpty(DocumentName)) {
      Toast.makeText(Registration.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
      return;
    }

    if (TextUtils.isEmpty(Email)) {
      Toast.makeText(Registration.this, "Email is missing", Toast.LENGTH_SHORT).show();
      return;
    }

    if (TextUtils.isEmpty(Phone)) {
      Toast.makeText(Registration.this, "Phone is missing", Toast.LENGTH_SHORT).show();
      return;
    }

    if (phone.length() < 10) {
      Toast.makeText(Registration.this, "Phone number should be greater than 10 digit", Toast.LENGTH_SHORT).show();
      return;
    }

    if (TextUtils.isEmpty(Fullname)) {
      Toast.makeText(Registration.this, "Name is missing", Toast.LENGTH_SHORT).show();
      return;
    }

    if (TextUtils.isEmpty(Password)) {
      Toast.makeText(Registration.this, "Password is missing", Toast.LENGTH_SHORT).show();
      return;
    }

    if (Password.length() < 8) {
      Toast.makeText(Registration.this, "Password should be or greater than 8 digit", Toast.LENGTH_SHORT).show();
      return;
    }

    if (TextUtils.isEmpty(Shopname)) {
      Toast.makeText(Registration.this, "Shop name is missing", Toast.LENGTH_SHORT).show();
      return;
    }

    progressBar.setVisibility(View.VISIBLE);

    if (documentUri != null) {
      StorageReference fileReference = storageReference.child(System.currentTimeMillis() + ".pdf");
      fileReference.putFile(documentUri)
              .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                FirebaseDatabase.getInstance().getReference("Shops").push().setValue(Shopname);

                User user = new User(Email, Fullname, Phone, Password, Shopname, uri.toString(), false);

                firebaseAuth.createUserWithEmailAndPassword(Email, Password)
                        .addOnCompleteListener(Registration.this, task -> {
                          progressBar.setVisibility(View.GONE);
                          if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            if (firebaseUser != null) {
                              FirebaseDatabase.getInstance().getReference("Users")
                                      .child(firebaseUser.getUid())
                                      .setValue(user)
                                      .addOnCompleteListener(task1 -> {
                                        progressBar.setVisibility(View.GONE);
                                        if (task1.isSuccessful()) {
                                          Toast.makeText(Registration.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                                          showNotification();
                                          firebaseAuth.signOut();
                                          startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                          finish();
                                        } else {
                                          Toast.makeText(Registration.this, "Failed to register user", Toast.LENGTH_SHORT).show();
                                        }
                                      });
                            } else {
                              Toast.makeText(Registration.this, "Failed to get user details. Try again.", Toast.LENGTH_SHORT).show();
                            }
                          } else {
                            Toast.makeText(Registration.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                          }
                        });
              }))
              .addOnFailureListener(e -> {
                Toast.makeText(Registration.this, "Failed to upload document: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
              });
    } else {
      Toast.makeText(Registration.this, "Please select a document", Toast.LENGTH_SHORT).show();
      progressBar.setVisibility(View.GONE);
    }
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
    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
    startActivity(intent);
  }
}