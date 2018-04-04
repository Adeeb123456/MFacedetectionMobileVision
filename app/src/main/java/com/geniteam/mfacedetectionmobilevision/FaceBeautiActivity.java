package com.geniteam.mfacedetectionmobilevision;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

/**
 * Created by 7CT on 3/26/2018.
 */

public class FaceBeautiActivity extends Activity {
    private static final String TAG = "FaceBeautiActivity";
public  static ImageView imageViewBefore,imageViewAfter,imageViews1,imageViews2,imageViews3;

Button buttonPick;
FrameLayout frameLayout;
FaceView faceView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebeauti);

      /*  InputStream stream = getResources().openRawResource(R.raw.face);
        Bitmap bitmap = BitmapFactory.decodeStream(stream);
        */

        Bitmap bitmap=BitmapFactory.decodeResource(getResources(),R.drawable.face);

        imageViewAfter=(ImageView)findViewById(R.id.imageViewAfter);
        imageViewBefore=(ImageView)findViewById(R.id.imageViewbefore);



        imageViews1=(ImageView)findViewById(R.id.imageViews1);
        imageViews2=(ImageView)findViewById(R.id.imageViews2);
        imageViews3=(ImageView)findViewById(R.id.imageViews3);



        buttonPick=(Button)findViewById(R.id.buttonpick);
        frameLayout=(FrameLayout)findViewById(R.id.frame);
       // faceView=new FaceView(this);
//        frameLayout.addView(faceView);


        buttonPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadImagefromGallery();
            }
        });

       // faceDetectorInit(bitmap);
    }


    public void faceDetectorInit(Bitmap bitmap){
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

      //  FaceView overlay = (FaceView) findViewById(R.id.faceView);
      //  faceView.setContent(bitmap, faces);

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
        paint.setStrokeWidth(5);



        Canvas cropedFaceCanvas;
        Bitmap cropedBitmap;
        Paint cropFacePaint;
        cropedBitmap=Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(),bitmap.getConfig());


        cropFacePaint=new Paint();
        cropFacePaint.setColor(Color.BLUE);
        cropFacePaint.setStrokeWidth(6);

        double viewWidth = imageViewBefore.getWidth();
        double viewHeight = imageViewBefore.getHeight();
        double imageWidth = bitmap.getWidth();
        double imageHeight = bitmap.getHeight();
        double scale = Math.min(viewWidth / imageWidth, viewHeight / imageHeight);

        cropedFaceCanvas=new Canvas(cropedBitmap);
        Rect rectBound=new Rect(0,0,(int)(imageWidth*scale),(int)(imageHeight*scale));
        cropedFaceCanvas.drawBitmap(cropedBitmap,null,rectBound,null);
        float xtopLeft=0;
        float ytopLeft=0;
        float xBottom=0;
        float yBottom=0;

        for (int i = 0; i < faces.size(); ++i) {
            Face face = faces.valueAt(i);


             xtopLeft= (float) (face.getPosition().x);
            ytopLeft= (float) (face.getPosition().y)+(face.getHeight()/6);


             xBottom= (float) (xtopLeft+face.getWidth());

            yBottom= (float) (ytopLeft+face.getHeight());


          Bitmap  bitmapmask1=Bitmap.createScaledBitmap(bitmapmask,(int)face.getWidth(),(int)face.getHeight(),true);

     //     canvasFace.drawRect(new RectF(xtopLeft,ytopLeft,xBottom,yBottom),paint);
            Rect destBounds1 = new Rect((int)xtopLeft, (int)ytopLeft, (int)(xBottom ), (int)(yBottom ));
          //canvasFace.drawBitmap(bitmapmask,null,destBounds1,paint);


        }

       Bitmap bitmap1;
       // bitmap1=doColorFilter(bitmapFace,0,0,.8,(int)xtopLeft,(int)ytopLeft, (int)xBottom,(int)yBottom);
bitmap1=doGamma(bitmapFace,1.8,1.8,1.8,(int)xtopLeft,(int)ytopLeft, (int)xBottom,(int)yBottom);
        imageViewAfter.setImageBitmap(bitmapFace);

    }

    int imagePick=100;

    public void loadImagefromGallery() {


        Intent galleryIntent = new Intent(Intent.ACTION_PICK,

                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

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
imageViewBefore.setImageBitmap(bitmapFromGallery);
faceDetectorInit(bitmapFromGallery);

        } catch (Exception e) {
e.printStackTrace();
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)

                    .show();

        }

    }

    public static Bitmap doColorFilter(Bitmap src, double red, double green, double blue,int xstart,int ystart,
                                       int xend,int yend) {
        // image size
        int width = src.getWidth();
        int height = src.getHeight();
        // create output bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
        // color information
        int A, R, G, B;
        int pixel;

        // scan through all pixels
        for(int x = xstart; x < xend; ++x) {
            for(int y = ystart; y < yend; ++y) {
                // get pixel color
                pixel = src.getPixel(x, y);
                // apply filtering on each channel R, G, B
                A = Color.alpha(pixel);
                R = (int)(Color.red(pixel) * red);
                G = (int)(Color.green(pixel) * green);
                B = (int)(Color.blue(pixel) * blue);
                // set new color pixel to output bitmap
                src.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        // return final image
        return src;
    }



    public static Bitmap doGamma(Bitmap src, double red, double green, double blue,int xstart,int ystart,
                                 int xend,int yend) {
        // create output image
        Bitmap bmOut = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());
        // get image size
        int width = src.getWidth();
        int height = src.getHeight();
        // color information
        int A, R, G, B;
        int pixel;
        // constant value curve
        final int    MAX_SIZE = 256;
        final double MAX_VALUE_DBL = 255.0;
        final int    MAX_VALUE_INT = 255;
        final double REVERSE = 1.0;

        // gamma arrays
        int[] gammaR = new int[MAX_SIZE];
        int[] gammaG = new int[MAX_SIZE];
        int[] gammaB = new int[MAX_SIZE];

        // setting values for every gamma channels
        for(int i = 0; i < MAX_SIZE; ++i) {
            gammaR[i] = (int) Math.min(MAX_VALUE_INT,
                    (int)((MAX_VALUE_DBL * Math.pow(i / MAX_VALUE_DBL, REVERSE / red)) + 0.5));
            gammaG[i] = (int) Math.min(MAX_VALUE_INT,
                    (int)((MAX_VALUE_DBL * Math.pow(i / MAX_VALUE_DBL, REVERSE / green)) + 0.5));
            gammaB[i] = (int) Math.min(MAX_VALUE_INT,
                    (int)((MAX_VALUE_DBL * Math.pow(i / MAX_VALUE_DBL, REVERSE / blue)) + 0.5));
        }

        // apply gamma table
        for(int x = xstart; x < xend; ++x) {
            for(int y = ystart; y < yend; ++y) {
                // get pixel color
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                // look up gamma
                R = gammaR[Color.red(pixel)];
                G = gammaG[Color.green(pixel)];
                B = gammaB[Color.blue(pixel)];
                // set new color to output bitmap
                if(x*x+y*y<=xend*yend){
                    src.setPixel(x, y, Color.argb(A, R, G, B));
                }
              //  Log.d("debug","rec X "+x+" y "+y);






            }
        }

        float y1=100,k;

        float m = 2;




        // return final image
        return src;
    }

}


