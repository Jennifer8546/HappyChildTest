package com.example.vrml.happychildapp.Creat_Bitmap;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by VRML on 2017/3/27.
 */

public class CreatBitmap {
    private int width;
    private int height;
    private int vWidth;
    private Bitmap ResultBitmap;

    public CreatBitmap(int width, int height, int vWidth) {
        this.width = width;
        this.height = height;
        this.vWidth = vWidth;
    }

    public Bitmap getBitmap() {
        return ResultBitmap;
    }

    public void setBitmap(String string, Bitmap bitmap) {
        makeBitmap(string, bitmap);
    }

    public void makeBitmap(String string, Bitmap bitmap) {
        Bitmap newbm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        String Text = string;

        Canvas canvas = new Canvas(newbm);
        Paint paint = new Paint();
        Rect bounds = new Rect();

        paint.setTextSize(vWidth);
        paint.getTextBounds(Text, 0, Text.length(), bounds);
        int tvx = width / 16;
        int tvy = height * 4 / 5 / 2;
        int imX = width / 2;
        int imy = (height / 2)-(bitmap.getHeight()/2);

        if (Text.length() > 7) {
            Text = string.substring(0, 7);
            canvas.drawText(Text, tvx, tvy, paint);
            Text = string.substring(7, string.length());
            canvas.drawText(Text, tvx, tvy + vWidth, paint);
        }else{
            canvas.drawText(Text, tvx+ vWidth, tvy+ vWidth, paint);
        }

        canvas.drawBitmap(bitmap, imX, imy, paint);
        ResultBitmap = newbm;
    }
}
