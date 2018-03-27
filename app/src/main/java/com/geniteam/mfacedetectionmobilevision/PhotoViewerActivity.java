package com.geniteam.mfacedetectionmobilevision;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;

import java.io.InputStream;

/**
 * Created by 7CT on 3/26/2018.
 */

public class PhotoViewerActivity extends Activity {
    private static final String TAG = "PhotoViewerActivity";
public  static ImageView imageView,imageViews1,imageViews2,imageViews3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_viewer);

      /*  InputStream stream = getResources().openRawResource(R.raw.face);
        Bitmap bitmap = BitmapFactory.decodeStream(stream);
        */

        Bitmap bitmap=BitmapFactory.decodeResource(getResources(),R.drawable.f1);

        imageView=(ImageView)findViewById(R.id.imageView);

        imageViews1=(ImageView)findViewById(R.id.imageViews1);
        imageViews2=(ImageView)findViewById(R.id.imageViews2);
        imageViews3=(ImageView)findViewById(R.id.imageViews3);

        // A new face detector is created for detecting the face and its landmarks.
        //
        // Setting "tracking enabled" to false is recommended for detection with unrelated
        // individual images (as opposed to video or a series of consecutively captured still
        // images).  For detection on unrelated individual images, this will give a more accurate
        // result.  For detection on consecutive images (e.g., live video), tracking gives a more
        // accurate (and faster) result.
        //
        // By default, landmark detection is not enabled since it increases detection time.  We
        // enable it here in order to visualize detected landmarks.
        FaceDetector detector = new FaceDetector.Builder(getApplicationContext())
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .build();

        // This is a temporary workaround for a bug in the face detector with respect to operating
        // on very small images.  This will be fixed in a future release.  But in the near term, use
        // of the SafeFaceDetector class will patch the issue.
        Detector<Face> safeDetector = new SafeFaceDetector(detector);

        // Create a frame from the bitmap and run face detection on the frame.
        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<Face> faces = safeDetector.detect(frame);

        if (!safeDetector.isOperational()) {
            // Note: The first time that an app using face API is installed on a device, GMS will
            // download a native library to the device in order to do detection.  Usually this
            // completes before the app is run for the first time.  But if that download has not yet
            // completed, then the above call will not detect any faces.
            //
            // isOperational() can be used to check if the required native library is currently
            // available.  The detector will automatically become operational once the library
            // download completes on device.
            Log.w(TAG, "Face detector dependencies are not yet available.");

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(this, "low storage", Toast.LENGTH_LONG).show();
                Log.w(TAG, "low storage");
            }
        }

        FaceView overlay = (FaceView) findViewById(R.id.faceView);
      // overlay.setContent(bitmap, faces);

    processFace(bitmap,faces);

        // Although detector may be used multiple times for different images, it should be released
        // when it is no longer needed in order to free native resources.
        safeDetector.release();
    }
    private Bitmap getScaledBitMapBaseOnScreenSize(Bitmap bitmapOriginal){

        Bitmap scaledBitmap=null;
        try {
            DisplayMetrics metrics = new DisplayMetrics();
            this.getWindowManager().getDefaultDisplay().getMetrics(metrics);


            int width = bitmapOriginal.getWidth();
            int height = bitmapOriginal.getHeight();

            float scaleWidth = metrics.scaledDensity;
            float scaleHeight = metrics.scaledDensity;

            // create a matrix for the manipulation
            Matrix matrix = new Matrix();
            // resize the bit map
            matrix.postScale(scaleWidth, scaleHeight);

            // recreate the new Bitmap
            scaledBitmap = Bitmap.createBitmap(bitmapOriginal, 0, 0, width, height, matrix, true);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return scaledBitmap;
    }

    public void processFace(Bitmap bitmap,SparseArray<Face> faces){
        Canvas canvasFace;
        Bitmap bitmapFace;
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inMutable=true;

        Bitmap bitmapmask=BitmapFactory.decodeResource(getResources(),R.drawable.mas,options);
    //   bitmapmask=Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(),bitmapmask.getConfig());
        bitmapFace=bitmap.copy(Bitmap.Config.ARGB_8888,true);

        canvasFace=new Canvas(bitmapFace);

        Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);


        for (int i = 0; i < faces.size(); ++i) {
            Face face = faces.valueAt(i);


            float xtopLeft= (float) (face.getPosition().x);
            float ytopLeft= (float) (face.getPosition().y);


            float xBottom= (float) (xtopLeft+face.getWidth());

            float yBottom= (float) (ytopLeft+face.getHeight());



          canvasFace.drawRect(new RectF(xtopLeft,ytopLeft,xBottom,yBottom),paint);
            Rect destBounds1 = new Rect((int)xtopLeft, (int)ytopLeft, (int)(xBottom ), (int)(yBottom ));
          canvasFace.drawBitmap(bitmapmask,null,destBounds1,paint);


        }

       imageView.setImageBitmap(bitmapFace);

    }






}
