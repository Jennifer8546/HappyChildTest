package com.example.vrml.happychildapp.Jennifer_Code;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by VRML on 2017/5/24.
 */

public class Uitlity {
    //將圖片轉成字串(上傳到firebase)
    public static String BitmapToString(Bitmap bitmap) {
        //暫存BYTE陣列
        ByteArrayOutputStream baos= new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
        byte[] b =baos.toByteArray();
        return Base64.encodeToString(b,Base64.DEFAULT);
    }
    //將字串轉成圖片(從firebase下載)
    public static Bitmap StringToBitmap(String encodeString){
        try{
            byte[] encodeByte=Base64.decode(encodeString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte,0,encodeByte.length);
            return bitmap;

        }catch (Exception e){
            e.getMessage();
            return null;
        }
    }

}
