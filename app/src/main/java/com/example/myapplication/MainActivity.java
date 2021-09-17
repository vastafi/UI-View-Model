package com.example.myapplication;

import static androidx.core.app.NotificationCompat.PRIORITY_HIGH;
import static com.example.myapplication.R.drawable.ic_baseline_notifications_24;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    private static final int NOTIFY_ID = 1;
    private final Handler mHandler = new Handler();

    @SuppressLint("SetTextI18n")
    Button openCamera;
    RadioButton frontCamera;
    RadioButton backCamera;
    Button openPhoto;
    Bitmap photo;
    String photoName;
    Button openLastPhoto;
    private static final String CHANNEL_ID = "channel_id01";
    private TextView textView;

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        openCamera = findViewById(R.id.button4);
        openCamera.setOnClickListener(view -> openCamera());

        openLastPhoto = findViewById(R.id.button5);
        openLastPhoto.setOnClickListener(view -> openLastPhoto());
        pushNotification();
        search();
        findViewById(R.id.toggleButton);
        findViewById(R.id.toggleButton);
        findViewById(R.id.textView);

        ToggleButton toggleButton;
        toggleButton = findViewById(R.id.toggleButton);
        textView = findViewById(R.id.textView);

        toggleButton.setOnCheckedChangeListener((compoundButton, checked) -> {
            if (checked) {
                textView.setText("Bluetooth is ON");
                BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                adapter.enable();
            } else {
                textView.setText("Bluetooth is OFF");
                BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                adapter.disable();
            }
        });
        // For initial setting
        if (toggleButton.isChecked()) {
            textView.setText("Bluetooth is ON");
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            adapter.enable();
        } else {
            textView.setText("Bluetooth is OFF");
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            adapter.disable();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void openLastPhoto() {
        Intent intent = new Intent(MainActivity.this, CameraActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT |
                        Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        intent.putExtra("photoName", photoName);
        startActivity(intent);
    }

    private void openCamera() {
        frontCamera = findViewById(R.id.radioButton3);
        backCamera = findViewById(R.id.radioButton2);

        if(frontCamera.isChecked()) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
            capturePhoto.launch(intent);
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra("android.intent.extras.CAMERA_FACING", 0);
                 capturePhoto.launch(intent);
        }
    }

    private final ActivityResultLauncher<Intent> capturePhoto = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                openPhoto = findViewById(R.id.button5);
                if(result.getResultCode() == Activity.RESULT_OK) {
                    assert result.getData() != null;
                    photo = (Bitmap) result.getData().getExtras().get("data");

                    String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
                    OutputStream outStream;
                    photoName = "MyPhoto" + System.currentTimeMillis() + ".jpeg";
                    File file = new File(extStorageDirectory, photoName);
                    try {
                        outStream = new FileOutputStream(file);
                        photo.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                        outStream.flush();
                        outStream.close();
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
    );
    public static void createChannelIfNeeded(NotificationManager manager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(notificationChannel);
        }
    }
    public void displayNotification(View view) {

        mHandler.postDelayed(mNoteRunnable, 10000);
    }

    private final Runnable mNoteRunnable = () -> {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                        .setAutoCancel(false)
                        .setSmallIcon(ic_baseline_notifications_24)
                        .setWhen(System.currentTimeMillis())
                        .setContentIntent(pendingIntent)
                        .setContentTitle("Notification")
                        .setContentText("10 seconds have passed!")
                        .setPriority(PRIORITY_HIGH);
        createChannelIfNeeded(notificationManager);
        notificationManager.notify(NOTIFY_ID, notificationBuilder.build());

    };

    public void pushNotification() {
        final Button button = findViewById(R.id.pushNotification);
        button.setOnClickListener(this::displayNotification);
    }

    public void search() {

        final Button button = findViewById(R.id.searchButton);

        button.setOnClickListener(view -> {
            String searchFor =  ((EditText)findViewById(R.id.searchWord)).getText().toString();
            Intent viewSearch = new Intent(Intent.ACTION_WEB_SEARCH);
            viewSearch.putExtra(SearchManager.QUERY, searchFor);
            startActivity(viewSearch);
        });
    }
}