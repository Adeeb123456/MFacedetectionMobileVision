package com.geniteam.mfacedetectionmobilevision;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;

/**
 * Created by 7CT on 3/26/2018.
 */

public class MaskStaticActivityold1 extends Activity implements View.OnClickListener{
    private static final String TAG = "FaceBeautiActivity";
public  static ImageView imageView,imageViews1,imageViews2,imageViews3,imageViews4;

Button buttonPick;
int filterToApply=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

      /*  InputStream stream = getResources().openRawResource(R.raw.face);
        Bitmap bitmap = BitmapFactory.decodeStream(stream);
        */

        Bitmap bitmap=BitmapFactory.decodeResource(getResources(),R.drawable.face);

        imageView=(ImageView)findViewById(R.id.imageViewAfter);
        buttonPick=(Button)findViewById(R.id.buttonpick);

        buttonPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isStoragePermissionGranted()){
                    loadImagefromGallery();
                }

            }
        });

        imageViews1=(ImageView)findViewById(R.id.mas1);
        imageViews2=(ImageView)findViewById(R.id.mas2);
        imageViews3=(ImageView)findViewById(R.id.mas3);
        imageViews4=(ImageView)findViewById(R.id.mas4);

        imageViews1.setOnClickListener(this);
        imageViews2.setOnClickListener(this);
        imageViews3.setOnClickListener(this);
        imageViews4.setOnClickListener(this);


    }


    public void faceDetectorInit(Bitmap bitmap,Bitmap bitmapmask){
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

     //   FaceView overlay = (FaceView) findViewById(R.id.faceView);
        //  overlay.setContent(bitmap, faces);

       if(filterToApply==1){
           processFaceLips(bitmap,faces,bitmapmask);
        }else

        if(filterToApply==2){
            processFaceLips(bitmap,faces,bitmapmask);
        }else if(filterToApply==3){
            processFaceLips(bitmap,faces,bitmapmask);
        }else if(filterToApply==4){
            processFaceLips(bitmap,faces,bitmapmask);
        }

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

    public void processFace(Bitmap bitmap,SparseArray<Face> faces,Bitmap bitmapmask){
        Canvas canvasFace;
        Bitmap bitmapFace;
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inMutable=true;


    //   bitmapmask=Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(),bitmapmask.getConfig());

        bitmapFace=bitmap.copy(Bitmap.Config.ARGB_8888,true);

        canvasFace=new Canvas(bitmapFace);

        Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);


        for (int i = 0; i < faces.size(); ++i) {
            Face face = faces.valueAt(i);


            float xtopLeft= (float) (face.getPosition().x);
            float ytopLeft= (float) (face.getPosition().y)+(face.getHeight()/6);


            float xBottom= (float) (xtopLeft+face.getWidth());

            float yBottom= (float) (ytopLeft+face.getHeight());


            float righteyeX = 0;
            float righteyeY=0;
            float leftEyeX = 0;
            float leftEyeY = 0;


            float rightmouthX = 0;
            float rightmouthY=0;
            float leftmouthX = 0;
            float leftmouthY = 0;

            int noseBaseX=0;
            int noseBaseY=0;


          Bitmap  bitmapmask1=Bitmap.createScaledBitmap(bitmapmask,(int)face.getWidth(),(int)face.getHeight(),true);

            for (Landmark landmark :  face.getLandmarks()) {
                int cx = (int) (landmark.getPosition().x );
                int cy = (int) (landmark.getPosition().y );

              if(landmark.getType()==Landmark.LEFT_EYE){
                  leftEyeX=landmark.getPosition().x;
                  leftEyeY=landmark.getPosition().y;
                  canvasFace.drawCircle(cx, cy, 20, paint);
                  canvasFace.drawCircle(cx, cy, 10, paint);

              }

              if(landmark.getType()==Landmark.RIGHT_EYE){
                  righteyeX=landmark.getPosition().x;
                  righteyeY=landmark.getPosition().y;
                  canvasFace.drawCircle(cx, cy, 20, paint);
                  canvasFace.drawCircle(cx, cy, 10, paint);
              }

                if(landmark.getType()==Landmark.NOSE_BASE){
                  noseBaseX=(int)landmark.getPosition().x;
                    noseBaseY=(int)landmark.getPosition().y;
                    canvasFace.drawCircle(cx, cy, 10, paint);
                }




                    //  canvasFace.drawBitmap(bitmapmask,leftEyeX+righteyeX,leftEyeY+righteyeY,paint);

            }

            canvasFace.rotate(face.getEulerY()-face.getEulerZ(),canvasFace.getWidth()/2,canvasFace.getHeight()/2);

          canvasFace.drawRect(new RectF(righteyeX-face.getWidth()/7,(int)(righteyeY+leftEyeY)/2-face.getHeight()/7,leftEyeX+face.getWidth()/7,noseBaseY),paint);
           // Rect destBounds1 = new Rect((int)righteyeX,(int)(righteyeY+leftEyeY)/2,(int)leftEyeX,(int)noseBaseY);
            Rect destBounds1 = new Rect((int)(righteyeX-face.getWidth()/7),(int)((righteyeY+leftEyeY)/2-face.getHeight()/7),(int)(leftEyeX+face.getWidth()/7),(int)noseBaseY);
         canvasFace.drawBitmap(bitmapmask,null,destBounds1,paint);


        }

       imageView.setImageBitmap(bitmapFace);

    }
    public void processFaceGlasses(Bitmap bitmap,SparseArray<Face> faces,Bitmap bitmapmask){
        Canvas canvasFace;
        Bitmap bitmapFace;
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inMutable=true;


        //   bitmapmask=Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(),bitmapmask.getConfig());

        bitmapFace=bitmap.copy(Bitmap.Config.ARGB_8888,true);

        canvasFace=new Canvas(bitmapFace);

        Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.OVERLAY
        ));


        for (int i = 0; i < faces.size(); ++i) {
            Face face = faces.valueAt(i);


            float xtopLeft= (float) (face.getPosition().x);
            float ytopLeft= (float) (face.getPosition().y)+(face.getHeight()/6);


            float xBottom= (float) (xtopLeft+face.getWidth());

            float yBottom= (float) (ytopLeft+face.getHeight());


            float righteyeX = 0;
            float righteyeY=0;
            float leftEyeX = 0;
            float leftEyeY = 0;


            float rightmouthX = 0;
            float rightmouthY=0;
            float leftmouthX = 0;
            float leftmouthY = 0;

            int noseBaseX=0;
            int noseBaseY=0;


            Bitmap  bitmapmask1=Bitmap.createScaledBitmap(bitmapmask,(int)face.getWidth(),(int)face.getHeight(),true);

            for (Landmark landmark :  face.getLandmarks()) {
                int cx = (int) (landmark.getPosition().x );
                int cy = (int) (landmark.getPosition().y );

                if(landmark.getType()==Landmark.LEFT_EYE){
                    leftEyeX=landmark.getPosition().x;
                    leftEyeY=landmark.getPosition().y;
                    canvasFace.drawCircle(cx, cy, 20, paint);
                    canvasFace.drawCircle(cx, cy, 10, paint);

                }

                if(landmark.getType()==Landmark.RIGHT_EYE){
                    righteyeX=landmark.getPosition().x;
                    righteyeY=landmark.getPosition().y;
                    canvasFace.drawCircle(cx, cy, 20, paint);
                    canvasFace.drawCircle(cx, cy, 10, paint);
                }

                if(landmark.getType()==Landmark.NOSE_BASE){
                    noseBaseX=(int)landmark.getPosition().x;
                    noseBaseY=(int)landmark.getPosition().y;
                    canvasFace.drawCircle(cx, cy, 10, paint);
                }




                //  canvasFace.drawBitmap(bitmapmask,leftEyeX+righteyeX,leftEyeY+righteyeY,paint);

            }

            canvasFace.rotate(face.getEulerY()-face.getEulerZ(),canvasFace.getWidth()/2,canvasFace.getHeight()/2);

            canvasFace.drawRect(new RectF(righteyeX-face.getWidth()/7,(int)(righteyeY+leftEyeY)/2-face.getHeight()/7,leftEyeX+face.getWidth()/7,noseBaseY),paint);
            // Rect destBounds1 = new Rect((int)righteyeX,(int)(righteyeY+leftEyeY)/2,(int)leftEyeX,(int)noseBaseY);
            Rect destBounds1 = new Rect((int)(righteyeX-face.getWidth()/7),(int)((righteyeY+leftEyeY)/2-face.getHeight()/7),(int)(leftEyeX+face.getWidth()/7),(int)noseBaseY);
            canvasFace.drawBitmap(bitmapmask,null,destBounds1,paint);


        }

        imageView.setImageBitmap(bitmapFace);

    }
    Bitmap bitmapFace=null;
    Canvas canvasFace;
    public void processFaceLips(Bitmap bitmap,SparseArray<Face> faces,Bitmap bitmapmask){


        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inMutable=true;


        //   bitmapmask=Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(),bitmapmask.getConfig());
if(bitmapFace==null){
    bitmapFace=bitmap.copy(Bitmap.Config.ARGB_8888,true);
    canvasFace=new Canvas(bitmapFace);
}




        Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
       // paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.OVERLAY));


        for (int i = 0; i < faces.size(); ++i) {
            Face face = faces.valueAt(i);


            float xtopLeft= (float) (face.getPosition().x);
            float ytopLeft= (float) (face.getPosition().y)+(face.getHeight()/6);


            float xBottom= (float) (xtopLeft+face.getWidth());

            float yBottom= (float) (ytopLeft+face.getHeight());


            float righteyeX = 0;
            float righteyeY=0;
            float leftEyeX = 0;
            float leftEyeY = 0;


            float rightmouthX = 0;
            float rightmouthY=0;
            float leftmouthX = 0;
            float leftmouthY = 0;

            int noseBaseX=0;
            int noseBaseY=0;


            Bitmap  bitmapmask1=Bitmap.createScaledBitmap(bitmapmask,(int)face.getWidth(),(int)face.getHeight(),true);

            for (Landmark landmark :  face.getLandmarks()) {
                int cx = (int) (landmark.getPosition().x );
                int cy = (int) (landmark.getPosition().y );

                if(landmark.getType()==Landmark.LEFT_EYE){
                    leftEyeX=landmark.getPosition().x;
                    leftEyeY=landmark.getPosition().y;
                    canvasFace.drawCircle(cx, cy, 20, paint);
                    canvasFace.drawCircle(cx, cy, 10, paint);

                }

                if(landmark.getType()==Landmark.RIGHT_EYE){
                    righteyeX=landmark.getPosition().x;
                    righteyeY=landmark.getPosition().y;
                    canvasFace.drawCircle(cx, cy, 20, paint);
                    canvasFace.drawCircle(cx, cy, 10, paint);
                }

                if(landmark.getType()==Landmark.NOSE_BASE){
                    noseBaseX=(int)landmark.getPosition().x;
                    noseBaseY=(int)landmark.getPosition().y;
                    canvasFace.drawCircle(cx, cy, 10, paint);
                }


                if(landmark.getType()==Landmark.LEFT_MOUTH){
                    leftmouthX=(int)landmark.getPosition().x;
                    leftmouthY=(int)landmark.getPosition().y;
                    canvasFace.drawCircle(cx, cy, 10, paint);
                }

                if(landmark.getType()==Landmark.RIGHT_MOUTH){
                    rightmouthX=(int)landmark.getPosition().x;
                    rightmouthY=(int)landmark.getPosition().y;
                    canvasFace.drawCircle(cx, cy, 10, paint);
                }


                //  canvasFace.drawBitmap(bitmapmask,leftEyeX+righteyeX,leftEyeY+righteyeY,paint);

            }

           // canvasFace.rotate(face.getEulerY()-face.getEulerZ(),canvasFace.getWidth()/2,canvasFace.getHeight()/2);


            if(filterToApply==2){
                canvasFace.drawRect(new RectF(righteyeX-face.getWidth()/7,(int)(rightmouthY+leftmouthY)/2-face.getHeight()/7,leftEyeX+face.getWidth()/7,noseBaseY),paint);
                //  Rect destBounds1 = new Rect((int)righteyeX,(int)(righteyeY+leftEyeY)/2,(int)leftEyeX,(int)noseBaseY);
                Rect destBounds1 = new Rect((int)(righteyeX-face.getWidth()/7),(int)((righteyeY+leftEyeY)/2-face.getHeight()/7),(int)(leftEyeX+face.getWidth()/7),(int)noseBaseY);
                canvasFace.drawBitmap(bitmapmask,null,destBounds1,paint);
            }



            if(filterToApply==3){
                canvasFace.drawRect(new RectF(rightmouthX-face.getWidth()/7,noseBaseY,leftmouthX+face.getWidth()/7,(int)(rightmouthY+leftmouthY)/2+face.getHeight()/7),paint);
                //  Rect destBounds1 = new Rect((int)righteyeX,(int)(righteyeY+leftEyeY)/2,(int)leftEyeX,(int)noseBaseY);
                Rect destBounds1 = new Rect((int)(rightmouthX-100),(int)(((rightmouthY+leftmouthY)/2)),(int)(leftmouthX+100),(int)((rightmouthY+leftmouthY)/2+face.getHeight()/9));
                canvasFace.drawBitmap(bitmapmask,null,destBounds1,paint);
            }


            if(filterToApply==4){
                canvasFace.drawRect(new RectF(rightmouthX-face.getWidth()/7,noseBaseY,leftmouthX+face.getWidth()/7,(int)(rightmouthY+leftmouthY)/2+face.getHeight()/7),paint);
                //  Rect destBounds1 = new Rect((int)righteyeX,(int)(righteyeY+leftEyeY)/2,(int)leftEyeX,(int)noseBaseY);
                Rect destBounds1 = new Rect((int)(rightmouthX-face.getWidth()/7),(int)((noseBaseY)),(int)(leftmouthX+face.getWidth()/7),(int)((rightmouthY+leftmouthY)/2+(face.getHeight()/3)));
                canvasFace.drawBitmap(bitmapmask,null,destBounds1,paint);
            }

            if(filterToApply==1){
               /* bitmapFace=bitmap.copy(Bitmap.Config.ARGB_8888,true);
                canvasFace=new Canvas(bitmapFace);*/
                canvasFace.drawRect(new RectF(righteyeX-face.getWidth()/7,(int)(rightmouthY+leftmouthY)/2-face.getHeight()/7,leftEyeX+face.getWidth()/7,noseBaseY),paint);
                //  Rect destBounds1 = new Rect((int)righteyeX,(int)(righteyeY+leftEyeY)/2,(int)leftEyeX,(int)noseBaseY);
                Rect destBounds1 = new Rect((int)(righteyeX-face.getWidth()/7),(int)((righteyeY+leftEyeY)/2-face.getHeight()/7),(int)(leftEyeX+face.getWidth()/7),(int)noseBaseY);

                Bitmap bitmapGlasses=BitmapFactory.decodeResource(getResources(),R.drawable.glas3,options);


                canvasFace.drawBitmap(bitmapGlasses,null,destBounds1,paint);



              //

                canvasFace.drawRect(new RectF(rightmouthX-face.getWidth()/7,noseBaseY,leftmouthX+face.getWidth()/7,(int)(rightmouthY+leftmouthY)/2+face.getHeight()/7),paint);
                //  Rect destBounds1 = new Rect((int)righteyeX,(int)(righteyeY+leftEyeY)/2,(int)leftEyeX,(int)noseBaseY);
                Rect destBounds2 = new Rect((int)(rightmouthX-100),(int)(((rightmouthY+leftmouthY)/2)),(int)(leftmouthX+100),(int)((rightmouthY+leftmouthY)/2+face.getHeight()/9));

                Bitmap bitmapLips=BitmapFactory.decodeResource(getResources(),R.drawable.lips1fit,options);
                canvasFace.drawBitmap(bitmapLips,null,destBounds2,paint);


                //

                canvasFace.drawRect(new RectF(rightmouthX-face.getWidth()/7,noseBaseY,leftmouthX+face.getWidth()/7,(int)(rightmouthY+leftmouthY)/2+face.getHeight()/7),paint);
                //  Rect destBounds1 = new Rect((int)righteyeX,(int)(righteyeY+leftEyeY)/2,(int)leftEyeX,(int)noseBaseY);
                Rect destBounds3 = new Rect((int)(rightmouthX-face.getWidth()/7),(int)((noseBaseY)),(int)(leftmouthX+face.getWidth()/7),(int)((rightmouthY+leftmouthY)/2+(face.getHeight()/3)));


                Bitmap bitmapBread=BitmapFactory.decodeResource(getResources(),R.drawable.b2fit,options);
                canvasFace.drawBitmap(bitmapBread,null,destBounds3,paint);

            }


        }

        imageView.setImageBitmap(bitmapFace);

    }


    int imagePick=100;

    public void loadImagefromGallery() {


        Intent galleryIntent = new Intent(Intent.ACTION_PICK,

                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, imagePick);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK){
            if(requestCode==imagePick){
            getBitmap(data);
            }


        }
    }

    String imgDecodableString;
    Bitmap bitmapFromGallery;
    public  void getBitmap(Intent data){
        try {

            // When an Image is picked



                // Get the Image from data



                Uri selectedImage = data.getData();

                String[] filePathColumn = { MediaStore.Images.Media.DATA };



                // Get the cursor

                Cursor cursor = getContentResolver().query(selectedImage,

                        filePathColumn, null, null, null);

                // Move to first row

                cursor.moveToFirst();



                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

                imgDecodableString = cursor.getString(columnIndex);

                cursor.close();

               // ImageView imgView = (ImageView) findViewById(R.id.imgView);

                // Set the Image in ImageView after decoding the String

              //  imgView.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));

bitmapFromGallery=BitmapFactory.decodeFile(imgDecodableString);
imageView.setImageBitmap(bitmapFromGallery);


        } catch (Exception e) {
e.printStackTrace();
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)

                    .show();

        }

    }


    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v(TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission

            loadImagefromGallery();
        }
    }

    @Override
    public void onClick(View view) {
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inMutable=true;
        if(view.getId()==R.id.mas1){
            Bitmap bitmapmask=BitmapFactory.decodeResource(getResources(),R.drawable.mas1,options);
filterToApply=1;
            faceDetectorInit(bitmapFromGallery,bitmapmask);
        }

        if(view.getId()==R.id.mas2){
            Bitmap bitmapmask=BitmapFactory.decodeResource(getResources(),R.drawable.glas3,options);
filterToApply=2;
            faceDetectorInit(bitmapFromGallery,bitmapmask);
        }
        if(view.getId()==R.id.mas3){
            filterToApply=3;
            Bitmap bitmapmask=BitmapFactory.decodeResource(getResources(),R.drawable.lips1fit,options);

            faceDetectorInit(bitmapFromGallery,bitmapmask);
        }

        if(view.getId()==R.id.mas4){
            filterToApply=4;
            Bitmap bitmapmask=BitmapFactory.decodeResource(getResources(),R.drawable.b2fit,options);

            faceDetectorInit(bitmapFromGallery,bitmapmask);
        }
    }
}


