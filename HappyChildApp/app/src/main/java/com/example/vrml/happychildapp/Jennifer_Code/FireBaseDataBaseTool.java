package com.example.vrml.happychildapp.Jennifer_Code;

import android.content.Intent;

import com.example.vrml.happychildapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * Created by VRML on 2017/10/13.
 */

public class FireBaseDataBaseTool {

    public static void SendText(String Path,Object message){
        FirebaseDatabase mFireBaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = mFireBaseDatabase.getReference();
        databaseReference.child(Path).setValue(message);
    }

}
