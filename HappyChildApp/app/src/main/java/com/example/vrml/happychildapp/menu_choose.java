package com.example.vrml.happychildapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vrml.happychildapp.Scoll.scrollActivity;

import java.util.ArrayList;

public class menu_choose extends AppCompatActivity {
    private ListView listView;
    private UnitAdapter adapter;
    private ArrayList<String> data;
    DisplayMetrics metrics;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_choose);
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        listView = (ListView) findViewById(R.id.menu_listview);
        adapter = new menu_choose.UnitAdapter(setData());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent();

                if (position == 0){
                    intent.setClass(menu_choose.this, Choose_Chinese_Math.class);
                    startActivity(intent);
                }
                if (position == 1){
                    Toast.makeText(menu_choose.this,"還沒完成",Toast.LENGTH_SHORT).show();
                    //intent.setClass(menu_choose.this, Unit1.class);
                  //  startActivity(intent);
                    }
                if (position == 2){
                    Toast.makeText(menu_choose.this,"還沒完成",Toast.LENGTH_SHORT).show();
                }
                if (position == 3){
                    intent.setClass(menu_choose.this, scrollActivity.class);
                    startActivity(intent);
                }
                //startActivity(intent);
            }
        });
    }

    private ArrayList setData() {
        //GET FROM SharedPreferences User
        String Position = getSharedPreferences("User" , MODE_PRIVATE).getString("Position","ERROR");
        data = new ArrayList<>();
        //CHECK REQUEST CODE
        if(Position.equals("ERROR")) {
            data.add("ERROR");
        }else {
            data.add("我的教學");//0
            data.add("學習紀錄");//1
            if(Position.equals("老師")){
                data.add("教材分享區");//2
                data.add("建立新教材");//3
            }
        }
        return data;
    }

    private class UnitAdapter extends BaseAdapter {
        ArrayList<String> list;

        private UnitAdapter(ArrayList<String> data) {
            this.list = data;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View view1 = LayoutInflater.from(menu_choose.this).inflate(R.layout.menu_item, null);
            TextView textView = (TextView) view1.findViewById(R.id.menu_textView);
            textView.setText(list.get(i));
            textView.setTextSize(metrics.widthPixels/60);
            return view1;
        }
    }
}
