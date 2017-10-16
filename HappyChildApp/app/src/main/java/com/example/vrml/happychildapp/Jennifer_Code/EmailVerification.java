package com.example.vrml.happychildapp.Jennifer_Code;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by VRML on 2017/10/14.
 */

public class EmailVerification {
    private FirebaseAuth firebaseAuth;
    private Context context;
    EmailVerification(FirebaseAuth firebaseAuth, Context context){
        this.firebaseAuth = firebaseAuth;
        this.context = context;
    }
    public void SendMail(){
        final ProgressDialog EmailDialog = ProgressDialog.show(context, "", "驗證信寄出中...", false, false);
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                public void onComplete(@NonNull Task<Void> task) {
                    EmailDialog.dismiss();
                    Log.e("TAG",task.toString());
                    if (task.isSuccessful()) {
                        Toast.makeText(context, "驗證信寄出成功!!", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("TAG", task.toString());
                        Toast.makeText(context, "驗證信寄出失敗!!", Toast.LENGTH_SHORT).show();
                    }
                }

            });
        }
    }
}
