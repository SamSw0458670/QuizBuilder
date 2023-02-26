package com.example.a2quizbuilder;

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
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Button startBtn, viewQuizzesBtn;

    Spinner spinner;

    DBAdapter db;

    List<Quiz> quizList = new ArrayList<>();

    String quizId;
    final String noQuizSelected = "X";

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

        //check for database, if it doesn't exist, make it
        try {
            String destPath = Environment.getExternalStorageDirectory().getPath() +
                    getPackageName() + "/database/QuizDB";
            File f = new File(destPath);
            if (!f.exists()) {
                CopyDB(getBaseContext().getAssets().open("quizdb"),
                        new FileOutputStream(destPath));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
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

    public View.OnClickListener onStartClicked = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            startQuiz();
        }
    };

    public View.OnClickListener onViewQuizzesClicked = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent i = new Intent(getApplicationContext(), ViewQuizzesActivity.class);
            startActivity(i); //Go to the first question of the quiz
        }
    };


    //function to load all the quizzes from the quiz list into the spinner
    public void loadSpinner() {

        loadQuizzes();
        List<String> spinList = new ArrayList<>();
        int spinItem = androidx.appcompat.R.layout.support_simple_spinner_dropdown_item;

        spinList.add("Select Quiz...");
        for (int i = 0; i < quizList.size(); i++) {
            spinList.add(quizList.get(i).getM_name());
        }


        ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(this, spinItem, spinList);

        spinAdapter.setDropDownViewResource(spinItem);
        spinner.setAdapter(spinAdapter);

    }

    //function to fill the quiz array list with quizzes from the database
    public void loadQuizzes() {

        db.open();
        Cursor c = db.getAllQuizzes();
        if (c.moveToFirst()) {
            do {
                Quiz quiz = new Quiz(c.getString(0), c.getString(1),
                        c.getString(2));
                quizList.add(quiz);
            } while (c.moveToNext());
        }
        db.close();
    }

    public void startQuiz(){
        if(validateSpinner()){
            if (validateQuiz()) {
                Intent i = new Intent(getApplicationContext(), QuestionActivity.class);
                Bundle quizInfo = new Bundle();
                quizInfo.putString("quizId", quizId);
                i.putExtras(quizInfo);
                startActivity(i);
            }
        }
    }

    //function to change quiz id to the selected quiz's id in the spinner
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,
                               long id) {
        if(position == 0){
            quizId = noQuizSelected;
        } else {
            quizId = quizList.get(position - 1).getID();
        }
        ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
        ((TextView) parent.getChildAt(0)).setTextSize(20);
    }

    //override function necessary for spinner
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    //function to check if selected quiz has 5 or more questions in it
    private boolean validateQuiz() {

        int minQuestions = 5;
        boolean result = true;
        db.open();
        Cursor c = db.getAllQuestions(Integer.parseInt(quizId));
        if (c.getCount() < minQuestions) {
            Toast.makeText(MainActivity.this,
                    "There must be at least " + minQuestions + " questions in the quiz",
                    Toast.LENGTH_LONG).show();
            result = false;
        }
        db.close();
        return result;
    }

    private boolean validateSpinner(){

        if(Objects.equals(quizId, noQuizSelected)){
            Toast.makeText(MainActivity.this,
                    "Please select a quiz",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

}