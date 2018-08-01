package com.fatto.android;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


/**
 * TODO
 *
 * @author fattoliu
 * @version V 1.0
 * @date on 23/5/2018 下午12:18
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_activity_custom_keyboard);

//        final EditText edit = findViewById(R.id.edit);
//        edit.setInputType(InputType.TYPE_NULL);

//        final EditText edit1 = this.findViewById(R.id.edit1);

//        edit.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                new KeyboardUtil(MainActivity.this, MainActivity.this, edit).showKeyboard();
//                return false;
//            }
//        });
//
//        edit1.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                int inputback = edit1.getInputType();
//                edit1.setInputType(InputType.TYPE_NULL);
//                new KeyboardUtil(MainActivity.this, MainActivity.this, edit1).showKeyboard();
//                edit1.setInputType(inputback);
//                return false;
//            }
//        });

        new Thread() {
            @Override
            public void run() {
                super.run();
                HttpsURLConnection urlConnection = null;
                try {
                    URL url = new URL("https://kyfw.12306.cn/otn/");
                    urlConnection = (HttpsURLConnection) url.openConnection();
                    InputStream in = urlConnection.getInputStream();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            }
        }.start();



    }
}
