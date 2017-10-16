package com.example.vrml.happychildapp.Jennifer_Code;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vrml.happychildapp.R;
import com.example.vrml.happychildapp.menu_choose;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvUserEmail;
    private Button buttonLogout;
    private EditText etUserName, etUserposition;
    private Button buttonSave;

    //USER 職位
    private String UserType = "學生";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //一般登出畫面
        tvUserEmail = (TextView) findViewById(R.id.tvUserEmail);
        buttonLogout = (Button) findViewById(R.id.buttonlogout);
        etUserName = (EditText) findViewById(R.id.etUserName);
        buttonSave = (Button) findViewById(R.id.buttonsave);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (saveUserInformation() == R.integer.OK)
                    startActivity(new Intent(ProfileActivity.this, menu_choose.class));
            }
        });
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ProfileActivity.this, SignInActivity.class));
                ProfileActivity.this.finish();
            }
        });

        //點選老師或學生
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.rgroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i) {
                    case R.id.StuButton:
                        UserType = "學生";
                        break;
                    case R.id.TeaButton:
                        UserType = "老師";
                        break;
                }
            }
        });
    }

    //資料儲存
    private int saveUserInformation() {
        String name = etUserName.getText().toString().trim();
        String position = UserType;
        if (name.equals("") || position.equals("")) {
            Toast.makeText(this, "NAME OR TYPE IS EMPTY", Toast.LENGTH_SHORT).show();
            return R.integer.ERROR;
        }
        UserInformation userInformation = new UserInformation(name, position);
        String Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FireBaseDataBaseTool.SendText(Uid, userInformation);
        Toast.makeText(this, "儲存資料中...", Toast.LENGTH_LONG).show();
        return R.integer.OK;
    }
}
