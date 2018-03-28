package com.geniteam.mfacedetectionmobilevision;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;

import com.google.android.gms.vision.face.Face;

import static com.geniteam.mfacedetectionmobilevision.FaceBeautiActivity.imageView;
import static com.geniteam.mfacedetectionmobilevision.FaceBeautiActivity.imageViews1;
import static com.geniteam.mfacedetectionmobilevision.FaceBeautiActivity.imageViews2;
import static com.geniteam.mfacedetectionmobilevision.FaceBeautiActivity.imageViews3;

/**
 * Created by 7CT on 3/26/2018.
 */

public class FaceView extends View{
    private Bitmap mBitmap;
    private SparseArray<Face> mFaces;

    public FaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Sets the bitmap background and the associated face detections.
     */
    void setContent(Bitmap bitmap, SparseArray<Face> faces) {
        mBitmap = bitmap;
        mFaces = faces;
        invalidate();
    }

    /**
     * Draws the bitmap background and the associated face landmarks.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if ((mBitmap != null) && (mFaces != null)) {
            double scale = drawBitmap(canvas);
            drawFaceAnnotations(canvas, scale);
        }
    }

    /**
     * Draws the bitmap background, scaled to the device size.  Returns the scale for future use in
     * positioning the facial landmark graphics.
     */
    private double drawBitmap(Canvas canvas) {
        double viewWidth = canvas.getWidth();
        double viewHeight = canvas.getHeight();
        double imageWidth = mBitmap.getWidth();
        double imageHeight = mBitmap.getHeight();
        double scale = Math.min(viewWidth / imageWidth, viewHeight / imageHeight);

        Rect destBounds = new Rect(0, 0, (int)(imageWidth * scale), (int)(imageHeight * scale));
        canvas.drawBitmap(mBitmap, null, destBounds, null);
        return scale;
    }

    /**
     * Draws a small circle for each detected landmark, centered at the detected landmark position.
     * <p>
     *
     * Note that eye landmarks are defined to be the midpoint between the detected eye corner
     * positions, which tends to place the eye landmarks at the lower eyelid rather than at the
     * pupil position.
     */

    private void drawFaceAnnotations(Canvas canvas, double scale) {
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);


        Paint paintFace=new Paint();
        paint.setColor(Color.RED);


        Bitmap bitmapFac=Bitmap.createBitmap(mBitmap.getWidth(),mBitmap.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas1=new Canvas(bitmapFac);
        Rect destBounds = new Rect(0, 0, (int)(mBitmap.getWidth() * scale), (int)(mBitmap.getHeight() * scale));

        //canvas1.drawBitmap(bitmapFac,0,0,paint);

        for (int i = 0; i < mFaces.size(); ++i) {
            Face face = mFaces.valueAt(i);


               float xtopLeft= (float) (face.getPosition().x);
               float ytopLeft= (float) (face.getPosition().y);


               float xBottom= (float) (xtopLeft+face.getWidth());

               float yBottom= (float) (ytopLeft+face.getHeight());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                canvas.drawRect(xtopLeft,ytopLeft,xBottom,yBottom,paint);
            }

           /* RectF rectF=new RectF(xtopLeft,ytopLeft,xBottom,yBottom);
          //  canvas1.drawBitmap(bitmapFac,new Matrix(),paint);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                canvas1.drawRect(xtopLeft,ytopLeft,xBottom,yBottom,paintFace);
            }

            paintFace.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            Rect destBounds1 = new Rect((int) xtopLeft, (int) ytopLeft, (int)(mBitmap.getWidth() * scale), (int)(mBitmap.getHeight() * scale));
            canvas1.drawBitmap(mBitmap, null, destBounds1, paintFace);*/


           // canvas1.drawBitmap(bitmapFac,new Rect(),paint);






         /*   for (Landmark landmark :  face.getLandmarks()) {
                int cx = (int) (landmark.getPosition().x * scale);
                int cy = (int) (landmark.getPosition().y * scale);
                canvas.drawCircle(cx, cy, 10, paint);
            }*/
        }
        Path path=new Path();


     // canvas.drawBitmap(mBitmap,0,0,paint);


      //  imageViews1.setImageBitmap(BitmapClassics.doGamma(bitmapFac,1.8,1.8,1.8));
        //imageViews2.setImageBitmap(BitmapClassics.doColorFilter(bitmapFac,0,1.8,1.8));
       //imageViews3.setImageBitmap(BitmapClassics.shrpBitmap(bitmapFac,BitmapClassics.kernalBlur));
      //  imageView.setImageBitmap(BitmapClassics.doGamma(bitmapFac,1.8,1.8,1.8));
    }



    public Bitmap combineTwoBitmaps(Bitmap originalBitmap,Bitmap faceCropBitmap){
        Bitmap bitmapCombined;
        bitmapCombined= Bitmap.createBitmap(originalBitmap.getWidth(),originalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvasCombined=new Canvas(bitmapCombined);

        canvasCombined.drawBitmap(faceCropBitmap,0,0,null);

        return bitmapCombined;



    }
}

