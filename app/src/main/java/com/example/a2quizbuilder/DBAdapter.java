package com.example.a2quizbuilder;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBAdapter extends SQLiteOpenHelper {

    // creating a constant variables for our database.
    // below variable is for our database name.
    private static final String DB_NAME = "quizDB";

    // below int is our database version
    private static final int DB_VERSION = 1;

    // below variable is for our table name.
    private static final String TABLE_QUIZ_NAME = "quizzes";
    private static final String TABLE_QUESTION_NAME = "questions";

    // id column used for all tables
    private static final String ID_COL = "id";

    // column names used for the quizzes table
    private static final String QUIZ_NAME_COL = "name";
    private static final String QUIZ_SECONDS_PER_QUESTION_COL = "seconds_per_question";

    //column names used for the questions table
    private static final String QUIZ_ID_COL = "quiz_id";
    private static final String QUESTION_COL = "question";
    private static final String ANSWER_COL = "answer";

    // below variable id for our course duration column.
    private static final String DURATION_COL = "duration";

    // below variable for our course description column.
    private static final String DESCRIPTION_COL = "description";

    // below variable is for our course tracks column.
    private static final String TRACKS_COL = "tracks";

    // creating a constructor for our database handler.
    public DBAdapter(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // below method is for creating a database by running a sqlite query
    @Override
    public void onCreate(SQLiteDatabase db) {

        // make the query to create the quizzes table and then execute it
        String quizQuery = "CREATE TABLE " + TABLE_QUIZ_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + QUIZ_NAME_COL + " TEXT,"
                + QUIZ_SECONDS_PER_QUESTION_COL + " INTEGER)";

        db.execSQL(quizQuery);

        // make the query to create the questions table with the FK to the
        // quiz table using the id column and then execute it
        String questionQuery = "CREATE TABLE " + TABLE_QUESTION_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + QUIZ_ID_COL + " INTEGER,"
                + QUESTION_COL + " TEXT,"
                + ANSWER_COL + " TEXT, "
                + "FOREIGN KEY ("+QUIZ_ID_COL+") REFERENCES "+TABLE_QUIZ_NAME+"("+ID_COL+"));";

        db.execSQL(questionQuery);
    }

    // this function is used to add a new quiz to the quizzes table
    public void addNewQuiz(String quizName, String secondsPerQuestion) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        //put each key and value into the ContentValues
        values.put(QUIZ_NAME_COL, quizName);
        values.put(QUIZ_SECONDS_PER_QUESTION_COL, secondsPerQuestion);

        //pass the new content values to the table
        db.insert(TABLE_QUIZ_NAME, null, values);

        db.close();
    }

    public void addNewQuestion(String quizID, String question, String answer) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        //put each key and value into the ContentValues
        values.put(QUIZ_ID_COL, quizID);
        values.put(QUESTION_COL, question);
        values.put(ANSWER_COL, answer);

        //pass the new content values to the table
        db.insert(TABLE_QUESTION_NAME, null, values);

        db.close();
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this method is called to check if the table exists already.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUIZ_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTION_NAME);
        onCreate(db);
    }
}