package com.example.vrml.happychildapp.Jennifer_Code;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vrml.happychildapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {

    private Button button;
    private EditText etemail;
    private EditText etpassword;
    private TextView login;

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        button = (Button) findViewById(R.id.signup);
        etemail = (EditText) findViewById(R.id.etemail);
        etpassword = (EditText) findViewById(R.id.etpassword);
        login = (TextView) findViewById(R.id.login);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
            }
        });
    }

    private void registerUser() {
        String email = etemail.getText().toString().trim();
        String password = etpassword.getText().toString().trim();
        if (!isEmailValid(email)) {
            //email是空的
            Toast.makeText(this, "email format error", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            //密碼是空的
            Toast.makeText(this, "password can't be empty and less than six words", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog = ProgressDialog.show(SignUpActivity.this, "", "註冊用戶中...", false, false);
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<com.google.firebase.auth.AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<com.google.firebase.auth.AuthResult> task) {
                        if (task.isSuccessful()) {
                            SendMail();
                        } else {
                            Log.e("DEBUG", "Sign-in Failed: " + task.getException().getMessage());
                            Toast.makeText(SignUpActivity.this, "註冊失敗!請再試一次!!", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });
    }

    private void SendMail() {
        final ProgressDialog EmailDialog = ProgressDialog.show(SignUpActivity.this, "", "驗證信寄出中...", false, false);

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                public void onComplete(@NonNull Task<Void> task) {
                    EmailDialog.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(SignUpActivity.this, "驗證信寄出成功!!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                        SignUpActivity.this.finish();
                    } else {
                        Log.e("TAG", task.toString());
                        Toast.makeText(SignUpActivity.this, "驗證信寄出失敗!!", Toast.LENGTH_SHORT).show();
                    }
                }

            });
        }
    }


    //Check Email Foramt
    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
