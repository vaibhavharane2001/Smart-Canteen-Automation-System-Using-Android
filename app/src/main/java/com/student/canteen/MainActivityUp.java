package com.student.canteen;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.res.ResourcesCompat;

import com.student.canteen.AdminComponent.AdminPanel;
import com.student.canteen.RegistrationUp.RegistrationUp;
import com.student.canteen.Util.NoInternet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.student.canteen.Util.shop_selection;

public class MainActivityUp extends AppCompatActivity {
    TextView email, password, forgotPassword;
    Button button,signupbutton;
    FirebaseAuth firebaseAuth;
    ProgressBar progressBar;
    FrameLayout frameLayout;
    boolean isResetPassword = false;

//    private static final String CHANNEL_ID = "My Channel";
//    private static final int NOTIFICATION_ID = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_main);

//
//        Drawable drawable = ResourcesCompat.getDrawable(getResources(),R.drawable.logo, null);
//
//        BitmapDrawable bitmapDrawable= (BitmapDrawable) drawable;
//
//        Bitmap largeIcon = bitmapDrawable.getBitmap();
//
//
//        NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
//        Notification notification;
//
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            notification = new Notification.Builder(this)
//                    .setLargeIcon(largeIcon)
//                    .setSmallIcon(R.drawable.logo)
//                    .setContentText("Order on OrderNosh")
//                    .setSubText("Updates")
//                    .setChannelId(CHANNEL_ID)
//                    .build();
//
//            nm.createNotificationChannel(new NotificationChannel(CHANNEL_ID,"New Channel",NotificationManager.IMPORTANCE_HIGH));
//        }else{
//            notification = new Notification.Builder(this)
//                    .setLargeIcon(largeIcon)
//                    .setSmallIcon(R.drawable.logo)
//                    .setContentText("Order on OrderNosh")
//                    .setSubText("Updates")
//                    .build();
//
//        }
//
//        nm.notify(NOTIFICATION_ID,notification);





        email = findViewById(R.id.Emailofloginup);
        password = findViewById(R.id.Passwordloginup);
        button = findViewById(R.id.loginbuttonup);
        signupbutton=findViewById(R.id.signupbuttonup);
        progressBar = findViewById(R.id.progressBarLoginPageup);
        progressBar.setVisibility(View.GONE);
        forgotPassword = findViewById(R.id.forgotPasswordup);
        frameLayout = findViewById(R.id.mainActivityFrameLayoutup);
        firebaseAuth = FirebaseAuth.getInstance();
        final TextView passwordToggler = findViewById(R.id.passwordToogleup);
        passwordToggler.setText("SHOW");


        forgotPassword.setOnClickListener(v -> {
            frameLayout.setVisibility(View.GONE);
            button.setText("RESET");
            isResetPassword = true;
        });

        signupbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), RegistrationUp.class);
                startActivity(intent);

            }
        });

        //THIS IS FOR PASSWORD SHOW METHOD
        passwordToggler.setOnClickListener(view -> {
            String state = passwordToggler.getText().toString();
            if (state.equals("SHOW")) {
                passwordToggler.setText("HIDE");
                password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                passwordToggler.setText("SHOW");
                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
        });

        button.setOnClickListener(view -> {
            if (isResetPassword) {
                progressBar.setVisibility(View.VISIBLE);
                String Email = email.getText().toString();
                if (TextUtils.isEmpty(Email)) {
                    Toast.makeText(MainActivityUp.this, "Email is missing", Toast.LENGTH_SHORT).show();
                    return;
                }
                firebaseAuth.sendPasswordResetEmail(Email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(MainActivityUp.this, "Reset link send successfully", Toast.LENGTH_SHORT).show();
                                frameLayout.setVisibility(View.VISIBLE);
                                button.setText("LOGIN");
                                progressBar.setVisibility(View.GONE);

                            }
                            if (!task.isSuccessful()) {
                                Toast.makeText(MainActivityUp.this, "Reset link sending failed", Toast.LENGTH_SHORT).show();
                                frameLayout.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                                button.setText("LOGIN");
                            }
                        });

            }
            if (!isResetPassword) {
                String Email = email.getText().toString().trim();
                String Password = password.getText().toString().trim();

                if (TextUtils.isEmpty(Email)) {
                    Toast.makeText(MainActivityUp.this, "Email is missing", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(Password)) {
                    Toast.makeText(MainActivityUp.this, "Password is missing", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Password.length() < 6) {
                    Toast.makeText(MainActivityUp.this, "Password too short", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    firebaseAuth.signInWithEmailAndPassword(Email, Password)
                            .addOnCompleteListener(MainActivityUp.this, task -> {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    showNotification();
                                    Intent intent = new Intent(getApplicationContext(), shop_selection.class);
                                    startActivity(intent);

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(MainActivityUp.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
            isResetPassword = false;
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            startActivity(new Intent(MainActivityUp.this, shop_selection.class));
        }
        if (!isInternetAvailable()) {
            startActivity(new Intent(MainActivityUp.this, NoInternet.class));
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
                .setContentTitle("Login ")
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