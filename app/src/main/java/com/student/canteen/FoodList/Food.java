package com.student.canteen.FoodList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.student.canteen.AdminComponent.AdminPanel;
import com.student.canteen.R;
import com.student.canteen.constant.FireBaseConstant;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class Food extends AppCompatActivity {
  FirebaseDatabase firebaseDatabase;
  DatabaseReference databaseReference;
  RecyclerView recyclerView;
  Button button, fooddone;
  RelativeLayout hiddenrelative;
  TextView fodname, foodpz, Foodpic;
  long maxid = 0;
  String foodname, foodprize, foodpic;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_food);

    firebaseDatabase = FirebaseDatabase.getInstance();
    databaseReference = firebaseDatabase.getReference(FireBaseConstant.FOOD_CARD);

    button = findViewById(R.id.addnewfood);
    fooddone = findViewById(R.id.DONefOOD);
    fodname = findViewById(R.id.setFoodname);
    foodpz = findViewById(R.id.setFoodprize);
    Foodpic = findViewById(R.id.setFoodpicture);
    recyclerView = findViewById(R.id.recyclerfood);
    recyclerView.setHasFixedSize(true);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    hiddenrelative = findViewById(R.id.hiddenrelative);
    hiddenrelative.setVisibility(View.GONE);

    createNotificationChannel(); // Create notification channel

    databaseReference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        if (snapshot.exists()) {
          int counteroflineno = 0;
          for (DataSnapshot postSnapshot : snapshot.getChildren()) {
            counteroflineno = Integer.parseInt(postSnapshot.getKey());
          }
          maxid = counteroflineno;
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {

      }
    });

    fooddone.setOnClickListener(view -> {
      hiddenrelative.setVisibility(View.GONE);
      foodname = fodname.getText().toString();
      foodprize = foodpz.getText().toString();
      foodpic = Foodpic.getText().toString();

      if (foodname.isEmpty() || foodprize.isEmpty() || foodpic.isEmpty()) {
        Toast.makeText(getApplicationContext(), "All fields are required!", Toast.LENGTH_LONG).show();
      } else {
        databaseReference.child(String.valueOf(maxid + 1)).child("foodName").setValue(foodname);
        databaseReference.child(String.valueOf(maxid + 1)).child("foodPrize").setValue(foodprize);
        databaseReference.child(String.valueOf(maxid + 1)).child("foodPicture").setValue(foodpic);
        databaseReference.child(String.valueOf(maxid + 1)).child("foodId").setValue(String.valueOf(maxid + 1));

        Toast.makeText(getApplicationContext(), "FOOD ADDED", Toast.LENGTH_LONG).show();

        sendNotification(); // Send notification
      }

      fodname.setText("");
      foodpz.setText("");
      Foodpic.setText("");
    });

    button.setOnClickListener(view -> hiddenrelative.setVisibility(View.VISIBLE));

  }

  private void createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      CharSequence name = "FoodChannel";
      String description = "Channel for food notifications";
      int importance = NotificationManager.IMPORTANCE_DEFAULT;
      NotificationChannel channel = new NotificationChannel("channel_id", name, importance);
      channel.setDescription(description);

      NotificationManager notificationManager = getSystemService(NotificationManager.class);
      notificationManager.createNotificationChannel(channel);
    }
  }

  private void sendNotification() {
    NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_id")
            .setSmallIcon(R.drawable.logo)
            .setContentTitle("New Food Added")
            .setContentText("A new food item has been added!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
    notificationManager.notify(1, builder.build());
  }

  @Override
  protected void onStart() {
    super.onStart();

    FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<FoodModel>().setQuery(databaseReference, FoodModel.class).build();
    FirebaseRecyclerAdapter<FoodModel, FoodVIewHOlder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<FoodModel, FoodVIewHOlder>(options) {
      @Override
      protected void onBindViewHolder(@NonNull FoodVIewHOlder foodVIewHOlder, final int i, @NonNull final FoodModel foodModel) {
        foodVIewHOlder.foodNametemp.setText(foodModel.getFoodName());
        foodVIewHOlder.foodPrizetemp.setText(foodModel.getFoodPrize());
        Picasso.get().load(foodModel.getFoodPicture()).into(foodVIewHOlder.foodPicturetemp);
        foodVIewHOlder.remove.setOnClickListener(view -> databaseReference.child(foodModel.getFoodId()).removeValue());
      }

      @NonNull
      @Override
      public FoodVIewHOlder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.foodtemp, parent, false);
        return new FoodVIewHOlder(view);
      }
    };

    firebaseRecyclerAdapter.startListening();
    firebaseRecyclerAdapter.notifyDataSetChanged();
    recyclerView.setAdapter(firebaseRecyclerAdapter);
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    finishAffinity();
    Intent intent = new Intent(getApplicationContext(), AdminPanel.class);
    startActivity(intent);
  }
}