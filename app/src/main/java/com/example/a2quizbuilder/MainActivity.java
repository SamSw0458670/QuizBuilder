package com.example.a2quizbuilder;

import androidx.annotation.ColorRes;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Button startBtn, viewQuizzesBtn;

    Spinner spinner;

    DBAdapter db;

    List<Quiz> quizList = new ArrayList<>();

    String quizId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startBtn = findViewById(R.id.startBtn);
        viewQuizzesBtn = findViewById(R.id.viewQuizzesBtn);
        spinner = findViewById(R.id.mainSpinner);

        startBtn.setOnClickListener(onStartClicked);
        viewQuizzesBtn.setOnClickListener(onViewQuizzesClicked);
        spinner.setOnItemSelectedListener(this);

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

        db = new DBAdapter(this);
        loadSpinner();


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
            if(validateQuiz()) {
                Intent i = new Intent(getApplicationContext(), QuestionActivity.class);
                Bundle quizInfo = new Bundle();
                quizInfo.putString("quizId", quizId);
                i.putExtras(quizInfo);
                startActivity(i); //Go to the first question of the quiz
            }
        }
    };

    public View.OnClickListener onViewQuizzesClicked = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            Intent i = new Intent(getApplicationContext(), ViewQuizzesActivity.class);
            startActivity(i); //Go to the first question of the quiz
        }
    };



    public void loadSpinner(){
        loadQuizzes();
        List<String> spinList =  new ArrayList<>();
        int spinItem = androidx.appcompat.R.layout.support_simple_spinner_dropdown_item;
        for(int i = 0; i < quizList.size(); i++){
            spinList.add(quizList.get(i).getName());
        }

        ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(this, spinItem, spinList);

        spinAdapter.setDropDownViewResource(spinItem);
        spinner.setAdapter(spinAdapter);

    }

    public void loadQuizzes(){
        db.open();
        Cursor c = db.getAllQuizzes();
        if(c.moveToFirst()){
            do{
                Quiz quiz = new Quiz(c.getString(0),c.getString(1),
                        c.getString(2));
                quizList.add(quiz);
            }while(c.moveToNext());
        }
        db.close();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,
                               long id) {

        quizId = quizList.get(position).getID();
        ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private boolean validateQuiz() {
        int minQuestions = 5;
        boolean result = true;
        db.open();
        Cursor c = db.getAllQuestions(Integer.parseInt(quizId));
        if(c.getCount() < minQuestions){
            Toast.makeText(MainActivity.this,
                    "There must be at least " + minQuestions +" questions in the quiz",
                    Toast.LENGTH_LONG).show();
            result = false;
        }
        db.close();
        return result;
    }

}