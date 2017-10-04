package com.example.kishorebaktha.text_recognization;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    SurfaceView camraView;
    TextView textView;
    Random r=new Random();
    StringBuilder stringBuilder=new StringBuilder();
    CameraSource cameraSource;
    final int REQUEST_CAMERA_PERMISSION = 101;
    final Handler mHandler = new Handler();
    // Create runnable for posting
//    final Runnable mUpdateResults = new Runnable() {
//        public void run() {
//            AlertDialog.Builder a_builder = new AlertDialog.Builder(MainActivity.this);
//            a_builder.setMessage("do you want to exit the game?")
//                    .setCancelable(false)
//                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            finish();
//                        }
//                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.cancel();
//        }
//        });
//            AlertDialog ab = a_builder.create();
//            ab.setTitle("Alert");
//            ab.show();
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);
        camraView = (SurfaceView) findViewById(R.id.surfaceView);
        final TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if (!textRecognizer.isOperational()) {
            Log.w("MainActivity", "dependencies not yet available");
        } else {
            cameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setRequestedFps(2.0f)
                    .setAutoFocusEnabled(true)
                    .build();
            camraView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    try {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.CAMERA}
                            ,REQUEST_CAMERA_PERMISSION);
                            return;
                        }
                        cameraSource.start(camraView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    cameraSource.stop();
                }
            });
            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {

                }

                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {
                      final SparseArray<TextBlock> items=detections.getDetectedItems();
                    if(items.size()!=0)
                    {
                        stringBuilder=new StringBuilder();
                        textView.post(new Runnable() {
                            @Override
                            public void run() {
                                for(int i=0;i<items.size();i++)
                                {
                                    TextBlock item=items.valueAt(i);
                                    stringBuilder.append(item.getValue());
                                    stringBuilder.append("\n");
                                }
                                textView.setText(stringBuilder.toString());
                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION)
        {
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                    return;
            }
            try {
                cameraSource.start(camraView.getHolder());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    public void Save(View v) {
                        int m = r.nextInt(20000000);
                        String s = String.valueOf(m);
                        File myfile = new File("/sdcard/" + s + ".txt");
                        try {
                            myfile.createNewFile();
                            FileOutputStream fout = new FileOutputStream(myfile);
                            OutputStreamWriter myoutwriter = new OutputStreamWriter(fout);
                            myoutwriter.append(stringBuilder);
                            myoutwriter.close();
                            fout.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
}
