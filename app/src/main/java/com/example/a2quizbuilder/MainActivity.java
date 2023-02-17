package com.example.a2quizbuilder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    Button startBtn, viewQuizzesBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startBtn = findViewById(R.id.startBtn);
        viewQuizzesBtn = findViewById(R.id.viewQuizzesBtn);

        startBtn.setOnClickListener(onStartClicked);
        viewQuizzesBtn.setOnClickListener(onViewQuizzesClicked);

        try{
            //String destPath = "/data/data/" + getPackageName() +"/database/MyDB";
            String destPath = Environment.getExternalStorageDirectory().getPath() +
                    getPackageName() + "/database/QuizDB";
            File f = new File(destPath);
            if(!f.exists()){
                CopyDB(getBaseContext().getAssets().open("quizdb"),
                        new FileOutputStream(destPath));
            }
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }



    }

    public void CopyDB(InputStream inputStream, OutputStream outputStream)
            throws IOException {
        //copy 1k bytes at a time
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
        inputStream.close();
        outputStream.close();
    }

    public View.OnClickListener onStartClicked = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            Intent i = new Intent(getApplicationContext(), QuestionActivity.class);
            startActivity(i); //Go to the first question of the quiz
        }
    };

    public View.OnClickListener onViewQuizzesClicked = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            Intent i = new Intent(getApplicationContext(), ViewQuizzesActivity.class);
            startActivity(i); //Go to the first question of the quiz
        }
    };

}