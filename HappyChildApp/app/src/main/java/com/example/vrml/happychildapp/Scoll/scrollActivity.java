package com.example.vrml.happychildapp.Scoll;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vrml.happychildapp.HYC_Code.OPEN_CAMERA;
import com.example.vrml.happychildapp.Jennifer_Code.Uitlity;
import com.example.vrml.happychildapp.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by VRML on 2017/5/10.
 */

public class scrollActivity extends AppCompatActivity {
    private RecyclerView myRecyclerView;
    private GalleryAdapter myAdapter;
    private List<Integer> mDatas;
    private TextView recycle_name, recycle_share;
    private CheckBox checkBox;
    private EditText recycle_editText;
    DisplayMetrics metrics = new DisplayMetrics();

    AlertDialog isExit;

    //Firebase相關宣告
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("image");

    //HYC ' s Method
    OPEN_CAMERA openPhoto;
    private static int CAMERA; //CAMERA 的代碼
    private static int PHOTO; //PHOTO 的代碼


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.recycle);
        recycle_name = (TextView) findViewById(R.id.recycle_name);
        recycle_share = (TextView) findViewById(R.id.recycle_share);
        recycle_editText = (EditText) findViewById(R.id.recycle_editText);
        checkBox = (CheckBox) findViewById(R.id.recycle_share_checkBox);
        initData();
        textViewSizeChang();
        DialogSet();
        initCAMERA();

        myRecyclerView = (RecyclerView) findViewById(R.id.recycle);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(linearLayoutManager.HORIZONTAL);
        myRecyclerView.setLayoutManager(linearLayoutManager);
        myAdapter = new GalleryAdapter(this, mDatas);

        //設置GallerAdapter監聽

        myAdapter.setmOnItemClickLitener(new GalleryAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
//                   Toast.makeText(scrollActivity.this, position+"", Toast.LENGTH_SHORT).show();
                isExit.show();
            }
        });

        myRecyclerView.setAdapter(myAdapter);
        super.onCreate(savedInstanceState);

    }

    private void initData() {
        mDatas = new ArrayList<Integer>(Arrays.asList(R.drawable.plus));
    }

    private void textViewSizeChang() {
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        recycle_name.setTextSize(metrics.heightPixels / 32);
        recycle_editText.setTextSize(metrics.heightPixels / 32);
        recycle_share.setTextSize(metrics.heightPixels / 32);
        checkBox.setTextSize(metrics.heightPixels / 32);
    }

    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {

            switch (i) {
                //開啟相簿
                case AlertDialog.BUTTON_NEGATIVE:
                    OPENPHOTO();
                    break;

                //開啟相機
                case AlertDialog.BUTTON_POSITIVE:
                    OPENCAMERA();
                    break;

                default:
                    break;
            }
        }
    };

    private void DialogSet() {
        isExit = new AlertDialog.Builder(this)
                .setTitle("開啟")
                .setMessage("開啟相簿或相機")
                .setPositiveButton("相機", listener)
                .setNegativeButton("相簿", listener)
                .setCancelable(false)
                .create();
    }


    OPEN_CAMERA open_camera;
    Intent openphoto;
    Intent opencamera;
    Intent opencrop;
    private int PHOTOID;
    private int CAMERAID;
    private int CROPID;
    private int MY_REQUEST_CODE = 50;

    private void initCAMERA() {
        open_camera = new OPEN_CAMERA();
        openphoto = open_camera.openphoto();
        opencamera = open_camera.opencamera();
        open_camera.fixMediaDir();
        if (opencamera == null)
            Toast.makeText(this, "NULL", Toast.LENGTH_LONG).show();

        PHOTOID = open_camera.getPhotoID();
        CAMERAID = open_camera.getCameraID();
        CROPID = open_camera.getCropID();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((checkSelfPermission(Manifest.permission.CAMERA) | checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_REQUEST_CODE);
            }

        }
    }

    private void OPENCROP(Uri uri) {
        opencrop = open_camera.crop(uri);
        startActivityForResult(opencrop, CROPID);
    }

    private void OPENPHOTO() {
        startActivityForResult(openphoto, PHOTOID);
    }

    private void OPENCAMERA() {
        if (opencamera.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(opencamera, CAMERAID);
            Log.e("DEBUG", "");
        } else
            Toast.makeText(this, "Your Camera is Wrong", Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Log.e("DEBUG","requestCode"+requestCode);
            if (requestCode == CAMERAID) {
                if (data != null) {
                    Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver()
                            , (Bitmap)data.getExtras().get("data"), null, null));
                    if (data.getExtras().get("data") != null) {
                        Log.e("DEBUG", "Line 191 BIT Height" + ((Bitmap) data.getExtras().get("data")).getHeight());
                        Log.e("DEBUG", "Line 191 BIT Width" + ((Bitmap) data.getExtras().get("data")).getWidth());
                        Log.e("DEBUG", "Line 191 Uri" + (Bitmap) data.getParcelableExtra("data"));
                    }
                    OPENCROP(uri);
                } else
                    Toast.makeText(this, "data == null", Toast.LENGTH_SHORT).show();
            } else if (requestCode == PHOTOID) {
                OPENCROP(data.getData());
                Log.e("DEBUG", "Line 211 " + data.getData());
            } else if (requestCode == CROPID) {
                Bitmap bitmap = data.getParcelableExtra("data");
                Log.e("DEBUG", "Line 214 Bitmap" + bitmap.toString());

                //RETURN TO 信慈Firebase
               // myRef.setValue(Uitlity.BitmapToString(bitmap));
//                Uitlity.BitmapToString(bitmap);
                Log.e("DEBUG", "Line 211 BitmapToString" + Uitlity.BitmapToString(bitmap));

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_REQUEST_CODE) {
            if (grantResults.length > 1) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Now user should be able to use camera
                    OPENCAMERA();
                    Toast.makeText(this, "Thanks ^^", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Fuck U ^^", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "有問題", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
