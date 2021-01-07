package com.yaochi.niceprogressbar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;

import com.yaochi.nice_progress_library.NiceProgressBar;

public class MainActivity extends AppCompatActivity {

    private Handler handler=new Handler();
    int progress=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view= LayoutInflater.from(this).inflate(R.layout.activity_main,null);
        final NiceProgressBar progressBar=view.findViewById(R.id.progress);
        progressBar.setMax(1000);
        setContentView(view);
         handler.postDelayed(new Runnable() {
             @Override
             public void run() {

                 progressBar.setProgress(progress);

                 if(progress<1000){
                     progress=progress+1;
                     handler.postDelayed(this,15);
                 }

             }
         },200);
    }
}