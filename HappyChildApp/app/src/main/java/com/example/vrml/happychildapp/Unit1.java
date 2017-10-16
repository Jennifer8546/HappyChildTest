package com.example.vrml.happychildapp;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

import com.example.vrml.happychildapp.Creat_Bitmap.CreatBitmap;

public class Unit1 extends AppCompatActivity {
    ImageView imageView;
    DisplayMetrics metrics = new DisplayMetrics();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unit1);
        imageView = (ImageView) findViewById(R.id.imgv);
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.cat);
        Log.e("Debug",metrics.widthPixels+"");
        CreatBitmap creatBitmap= new CreatBitmap(metrics.widthPixels,metrics.heightPixels,metrics.widthPixels / 16);
        creatBitmap.setBitmap("貓     咪ㄇㄠ ㄇㄧ",bitmap);
        Bitmap temp = creatBitmap.getBitmap();
        imageView.setImageBitmap(temp);
        //ChangImageSize();
    }
//    private void ChangImageSize(){
//
//        float width = metrics.widthPixels*3/5/2;
//        float scale = width/bitmap.getWidth();
//        Matrix matrix = new Matrix();
//        matrix.postScale(scale,scale);
//        Bitmap bmp = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
//        imageView.setImageBitmap(bmp);
//    }

}
