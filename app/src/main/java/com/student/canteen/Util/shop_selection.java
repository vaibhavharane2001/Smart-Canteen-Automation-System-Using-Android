//package com.student.canteen.Util;
//
//
//
//import android.annotation.SuppressLint;
//import android.content.Intent;
//import android.os.Bundle;
//
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//
//import com.student.canteen.BottomNavigation;
//import com.student.canteen.MainActivity;
//import com.student.canteen.R;
//
//public class shop_selection extends AppCompatActivity {
//
//    ImageView imageView;
//    TextView titleTextView;
//    Button selectButton, nimbalkarButton, cafeteriaButton, aanapurnaButton;
//
//    @SuppressLint("WrongViewCast")
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.shop_selection);
//
//        // Initialize views
//        imageView = findViewById(R.id.imageView);
//        titleTextView = findViewById(R.id.textview);
//        selectButton = findViewById(R.id.selectButton);
////        nimbalkarButton = findViewById(R.id.nimbalkarButton);
////        cafeteriaButton = findViewById(R.id.cafeteriaButton);
////        aanapurnaButton = findViewById(R.id.aanapurnaButton);
//
//        // Set click listeners
//        selectButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Handle select button click
//                Toast.makeText(shop_selection.this, "Select Button Clicked", Toast.LENGTH_SHORT).show();
//                // Add your logic for selecting a canteen
//            }
//        });
//
//        nimbalkarButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Handle Nimbalkar canteen button click
//                Toast.makeText(shop_selection.this, "Nimbalkar Canteen Clicked", Toast.LENGTH_SHORT).show();
//                // Add your logic for Nimbalkar canteen
//                Intent intent = new Intent(shop_selection.this, BottomNavigation.class);
//                startActivity(intent);
//            }
//        });
//
//        cafeteriaButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Handle Cafeteria canteen button click
//                Toast.makeText(shop_selection.this, "Cafeteria Canteen Clicked", Toast.LENGTH_SHORT).show();
//                // Add your logic for Cafeteria canteen
//                Intent intent = new Intent(shop_selection.this, BottomNavigation.class);
//                startActivity(intent);
//            }
//        });
//
//        aanapurnaButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Handle Aanapurna canteen button click
//                Toast.makeText(shop_selection.this, "Aanapurna Canteen Clicked", Toast.LENGTH_SHORT).show();
//                // Add your logic for Aanapurna canteen
//
//                Intent intent = new Intent(shop_selection.this, BottomNavigation.class);
//                startActivity(intent);
//            }
//        });
//
package com.student.canteen.Util;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.IntegerRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.student.canteen.BottomNavigation;
import com.student.canteen.MainActivity;
import com.student.canteen.R;
import com.student.canteen.Splashscreen.SplashScreen;
import com.student.canteen.StartingActivity;


public class shop_selection extends AppCompatActivity {
    private LinearLayout buttonContainer;
    private DatabaseReference shopRef;
    private int buttonMarginTop = 30;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_selection);

        buttonContainer = findViewById(R.id.buttonContainer);
        shopRef = FirebaseDatabase.getInstance().getReference().child("Shops");

        loadShopNames();
    }

    private void loadShopNames() {
        shopRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String shopName = snapshot.getValue().toString();
                    addButton(shopName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
                Toast.makeText(shop_selection.this, "Failed to load shop names", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void addButton(String shopName) {
        Button button = new Button(this);
        button.setText(shopName);
        button.setBackgroundResource(R.drawable.confrim_button);
        button.setTextSize(16);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, buttonMarginTop, 0, 0); // Set top margin
        button.setLayoutParams(layoutParams);

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Handle button click
                Intent intent = new Intent(shop_selection.this, BottomNavigation.class);
                startActivity(intent);
                Toast.makeText(shop_selection.this, "Clicked on " + shopName, Toast.LENGTH_SHORT).show();

            }
        });
        buttonContainer.addView(button);
    }
    private void showExitConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to exit?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finishAffinity();
//                Intent intent = new Intent(getApplicationContext(), SplashScreen.class);
//                startActivity(intent);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Do nothing, just dismiss the dialog
            }
        });
        builder.show();
    }



    @Override
    public void onBackPressed() {
        showExitConfirmationDialog();
    }

//    public void onBackPressed() {
//        super.onBackPressed();
//        finishAffinity();
//        Intent intent = new Intent(getApplicationContext(), SplashScreen.class);
//        startActivity(intent);
//    }
}

