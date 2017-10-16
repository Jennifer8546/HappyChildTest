package com.example.vrml.happychildapp.TurnCardGame;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RatingBar;

import com.example.vrml.happychildapp.Choose_Mode;
import com.example.vrml.happychildapp.R;
import com.example.vrml.happychildapp.menu_choose;

public class Turn_Card_Game extends AppCompatActivity {
    final Button[] buttons = new Button[16];
    int[] array = {1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8};
    String[] str_array;
    String text1 = "";
    Button temp = null;
    AlertDialog isExit;
    int count = 0;
    RatingBar mRatingBar;
    private static MediaPlayer music;
    DisplayMetrics metrics = new DisplayMetrics();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trun_card_game_start);
        GetData();
        StartSet();
        DialogSet();

    }

    public void GetData() {
        //從信慈Firebase 導入 TurnCardData
//        Bundle bundle = this.getIntent().getExtras();
//        String Subject = bundle.getString("Subject");
//        int Lesson = bundle.getInt("Lesson");
//        List<String> list = 信慈.GetFromFireBase(Subject,Lesson);
//        Turn_Card_Data turn_card_data = new Turn_Card_Data(list);
        String[] data = new String[]{"魟", "鮮", "聽", "聰", "眼", "睛", "狗", "狂", "物", "牧", "芊", "花", "樹", "柯", "吵", "嘴"};
        Turn_Card_Data turn_card_data = new Turn_Card_Data(data);
        str_array = turn_card_data.getData();
    }

    @Override
    //恢復的時候把音樂開起來
    protected void onResume() {
        super.onResume();
        mHandler = new Handler();
        if (progressThread != null) {
            progressThread = new Thread(ProgressRunnable);
            progressThread.start();
        }
        if (!music.isPlaying()) {
            try {

                music.start();
            } catch (Exception e) {
                Log.e("Debug", "TurnCardGame line 51");
                Log.e("Debug", e.toString());
            }
        }

    }

    //Star Button  music start + onClickListener
    private void StartSet() {
        Button star = (Button) findViewById(R.id.start);
        music = MediaPlayer.create(Turn_Card_Game.this, R.raw.background);
        music.setLooping(true);
        try {
            music.start();
        } catch (Exception e) {
            Log.e("Debug", "TurnCardGame line 51");
            Log.e("Debug", e.toString());
        }
        music.start();
        star.setOnClickListener(staronClickListener);
    }

    //Star的OnClickListener
    private View.OnClickListener staronClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            setContentView(R.layout.turn_card_game);
            Button back = (Button) findViewById(R.id.back);
            mRatingBar = (RatingBar) findViewById(R.id.ratingBar1);
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ShowExit();
                }
            });
            ProgressBarSetting();
            count = 0;
            Rand();
            StarSet();
            ButtonSet();
        }
    };

    //Button 宣告ID+TextSize設定
    private void ButtonSet() {
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Log.e("DEBUG","metrics : "+metrics.toString());
        for (int i = 0; i < 16; i++) {
            String btn = "button" + (i + 1);
            int resID = getResources().getIdentifier(btn, "id", getPackageName());
            buttons[i] = ((Button) findViewById(resID));
            buttons[i].setText(str_array[i]);
            //隨著螢幕寬去縮放文字尺寸
            if(metrics.widthPixels > 2000) {
                buttons[i].setTextSize(metrics.widthPixels / 60);
                if (buttons[i].getText().length() > 9) {
                    buttons[i].setTextSize(metrics.widthPixels / 80);
                }
            }else {
                buttons[i].setTextSize(metrics.widthPixels / 90);
                if (buttons[i].getText().length() > 9) {
                    buttons[i].setTextSize(metrics.widthPixels / 120);
                }
            }
            buttons[i].setTextColor(Color.BLUE);
            buttons[i].setOnClickListener(onClickListener);
            buttons[i].setBackgroundResource(R.drawable.tt);
        }
    }

    //星星初始設定
    private void StarSet() {
        mRatingBar.setMax(100);
        mRatingBar.setNumStars(3);
        mRatingBar.setStepSize(1);
        mRatingBar.setIsIndicator(true);
    }

    //位置交換
    private void Rand() {
        for (int i = 0; i < 1000; i++) {
            int n1 = (int) (Math.random() * 16);
            int n2 = (int) (Math.random() * 16);
            int temp = array[n1];
            array[n1] = array[n2];
            array[n2] = temp;
            String temp1 = str_array[n1];
            str_array[n1] = str_array[n2];
            str_array[n2] = temp1;
        }
    }

    //返回鍵Dialog的Builder
    private void DialogSet() {
        isExit = new AlertDialog.Builder(this)
                .setTitle("離開")
                .setMessage("確定要退出嗎?")
                .setPositiveButton("Yes", listener)
                .setNegativeButton("No", listener)
                .setCancelable(false)
                .create();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ShowExit();
        }
        return true;
    }

    private void ShowExit() {
        mHandler = null;
        isExit.show();
    }

    //成功後的Dialog Messang
    private void ShowMessang(String str) {
        mHandler = null;
        new AlertDialog.Builder(this).setMessage(str)
                .setNegativeButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        setContentView(R.layout.trun_card_game_start);
                        StartSet();
                        startActivity(new Intent(Turn_Card_Game.this, menu_choose.class));
                    }
                }).setCancelable(false).show();

    }

    //返回鍵Dialog的ClickListener
    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case AlertDialog.BUTTON_POSITIVE:
                    music.stop();
                    startActivity(new Intent(Turn_Card_Game.this, Choose_Mode.class));
                    Turn_Card_Game.this.finish();
                    break;
                case AlertDialog.BUTTON_NEGATIVE:
                    mHandler = new Handler();
                    if (progressThread != null) {
                        progressThread = new Thread(ProgressRunnable);
                        progressThread.start();
                    }

                    break;
                default:
                    break;
            }
        }
    };

    int t1, t2;
    //Button 點擊事件(判斷是否配對完)
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button button = (Button) v;
            int i = 0;
            String text = button.getText().toString();


            //第一次按
            if (text1.equals("")) {
                for (i = 0; i < str_array.length; i++) {
                    if (str_array[i].equals(text)) {
                        t1 = array[i];
                        break;
                    }
                }

                text1 = text;
                temp = button;
                temp.setBackgroundColor(Color.parseColor("#00FFFF"));
                return;
            }
            for (i = 0; i < str_array.length; i++) {
                if (str_array[i].equals(text)) {
                    t2 = array[i];
                    break;
                }
            }
            Log.e("DEBUG", "t1: " + t1 + " t2:" + t2);
            //第二次按 判斷是否值相同、點同一個
            // text1.equals(button.getText().toString())
            if (t1 == t2 && temp != button) {

                temp.setVisibility(View.INVISIBLE);
                button.setVisibility(View.INVISIBLE);
                temp.setOnClickListener(null);
                button.setOnClickListener(null);
                count++;
                //完成後顯示成功
                if (count == 8) {
                    ShowMessang("Successful");
                    mRatingBar.setRating(3);
                    music.stop();
                }
                temp = null;
                text1 = "";
                return;
            }
            //不同則恢復

            temp.setBackgroundResource(R.drawable.tt);
            temp = null;
            text1 = "";

        }
    };
    Handler mHandler;
    Thread progressThread;
    ProgressBar progressBar = null;

    //ProgressBar初始值設定
    private void ProgressBarSetting() {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setScaleY(4f);
        progressBar.setMax(600);
        progressBar.setProgress(600);
        mHandler = new Handler();
        progressThread = new Thread(ProgressRunnable);
        progressThread.start();
    }

    //ProgressBar的Runnable
    Runnable ProgressRunnable = new Runnable() {
        @Override
        public void run() {

            while (progressBar.getProgress() > 0 && mHandler != null) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.incrementProgressBy(-1);
                    }
                });
                android.os.SystemClock.sleep(100);
            }
            if (progressBar.getProgress() == 0) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        ShowMessang("Time Out ~~");
                        music.stop();
                    }
                });
            }
            Log.e("DEBUG", "TurnGame LINE207");
        }
    };

    //音樂暫停
    @Override
    protected void onPause() {
        super.onPause();
        mHandler = null;
        try {
            if (music.isPlaying())
                music.pause();
        } catch (Exception e) {
            Log.e("Debug", "TurnCardGame line 51");
            Log.e("Debug", e.toString());
        }

    }

}
