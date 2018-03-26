package com.geniteam.mfacedetectionmobilevision;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.widget.ImageView;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

public class MainActivity extends AppCompatActivity {
ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView=(ImageView)findViewById(R.id.imv);

        loadImage();
        createPaintObj();
        createTemBitmapAndCanvas();
        createFaceDetectorDetectFacesAndDrawRectangle();


    }


    Bitmap myBitmap;
    public void loadImage(){
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inMutable=true;

        myBitmap=BitmapFactory.decodeResource(getResources(),R.drawable.f1,options);


    }
    Paint myRectPaint;
    public void createPaintObj(){
        myRectPaint=new Paint();
        myRectPaint.setStrokeWidth(5);
        myRectPaint.setStyle(Paint.Style.STROKE);
        myRectPaint.setColor(Color.RED);
    }

    Bitmap tempBitmap;
    Canvas temCanvas;
    public void createTemBitmapAndCanvas(){
        tempBitmap =Bitmap.createBitmap(myBitmap.getWidth(),myBitmap.getHeight(), Bitmap.Config.RGB_565);
        temCanvas=new Canvas(tempBitmap);
        temCanvas.drawBitmap(tempBitmap,0,0,null);
    }



    public void createFaceDetectorDetectFacesAndDrawRectangle(){
        FaceDetector faceDetector = new
                FaceDetector.Builder(getApplicationContext()).setTrackingEnabled(false)
                .build();
        if(!faceDetector.isOperational()){
            new AlertDialog.Builder(MainActivity.this).setMessage("Could not set up the face detector!").show();
            return;
        }

        //Detect the Faces

        Frame frame = new Frame.Builder().setBitmap(myBitmap).build();
        SparseArray<Face> faces = faceDetector.detect(frame);


        //Draw Rectangles on the Faces


        for(int i=0; i<faces.size(); i++) {
            Face thisFace = faces.valueAt(i);
            float x1 = thisFace.getPosition().x;
            float y1 = thisFace.getPosition().y;
            float x2 = x1 + thisFace.getWidth();
            float y2 = y1 + thisFace.getHeight();
            temCanvas.drawRoundRect(new RectF(x1, y1, x2, y2), 2, 2, myRectPaint);
        }
       // imageView.setImageDrawable(new BitmapDrawable(getResources(),tempBitmap));
    }
}
