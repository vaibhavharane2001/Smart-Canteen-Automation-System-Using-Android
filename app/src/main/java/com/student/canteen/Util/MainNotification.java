package com.student.canteen.Util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.student.canteen.R;

public class MainNotification extends AppCompatActivity {

    public static final String CHANNEL_ID = "My Channel";
    public static final int NOTIFICATION_ID = 100;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Drawable drawable = ResourcesCompat.getDrawable(getResources(),R.drawable.ellipse, null);

        BitmapDrawable bitmapDrawable= (BitmapDrawable) drawable;

        Bitmap largeIcon = bitmapDrawable.getBitmap();


        NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        android.app.Notification notification;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = new android.app.Notification.Builder(this)
                    .setLargeIcon(largeIcon)
                    .setSmallIcon(R.drawable.ellipse)
                    .setContentText("New Message")
                    .setSubText("New Message from OrderNosh")
                    .setChannelId(CHANNEL_ID)
                    .build();

            nm.createNotificationChannel(new NotificationChannel(CHANNEL_ID,"New Channel",NotificationManager.IMPORTANCE_HIGH));
        }else{
            notification = new android.app.Notification.Builder(this)
                    .setLargeIcon(largeIcon)
                    .setSmallIcon(R.drawable.ellipse)
                    .setContentText("New Message")
                    .setSubText("New Message from OrderNosh")
                    .build();
        }
        nm.notify(NOTIFICATION_ID,notification);


    }

}
