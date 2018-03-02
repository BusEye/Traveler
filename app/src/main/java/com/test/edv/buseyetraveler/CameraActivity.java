package com.test.edv.buseyetraveler;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class CameraActivity extends AppCompatActivity {

    SurfaceView cameraview;
    BarcodeDetector barcodeDetector;
    CameraSource cameraSource;
    SurfaceHolder surfaceHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        cameraview = (SurfaceView) findViewById(R.id.cameraView);
        cameraview.setZOrderMediaOverlay(true);

        surfaceHolder =cameraview.getHolder();
        barcodeDetector = new  BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();

        if (!barcodeDetector.isOperational())
        {
            Toast.makeText(getApplicationContext(),"Sorry Setup",Toast.LENGTH_LONG).show();
            this.finish();
        }

        cameraSource = new CameraSource.Builder(this,barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(24)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(1920,1024)
                .build();

        cameraview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

                try{
                     if (ContextCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED)
                     {
                         cameraSource.start(cameraview.getHolder());
                     }
                   }
                catch (IOException e)
                   {
                      e.printStackTrace();
                   }


            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                    if (cameraSource != null)
                    {
                        cameraSource.release();
                        cameraSource = null;
                    }
            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
              final SparseArray<Barcode> barcode = detections.getDetectedItems();
              if (barcode.size()>0)
              {
                  Intent intent = new Intent();
                  intent.putExtra("barcordDeta",barcode.valueAt(0));
                  setResult(RESULT_OK,intent);
                  finish();
              }
            }
        });
    }
}
