package com.example.a2quizbuilder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBAdapter {

    // creating a constant variables for our database.
    // below variable is for our database name.
    private static final String DB_NAME = "QuizDB";

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

    private final DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    // creating a constructor for the database handler.
    public DBAdapter(Context context) {
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
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
                    + "FOREIGN KEY (" + QUIZ_ID_COL + ") REFERENCES " + TABLE_QUIZ_NAME + "(" + ID_COL + "));";

            db.execSQL(questionQuery);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // this method is called to check if the table exists already.
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUIZ_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTION_NAME);
            onCreate(db);
        }
    }

    //open the database
    public DBAdapter open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //close the database
    public void close() {
        DBHelper.close();
    }

    // this function is used to add a new quiz to the quizzes table
    public long addNewQuiz(String quizName, String secondsPerQuestion) {

        ContentValues values = new ContentValues();

        //put each key and value into the ContentValues
        values.put(QUIZ_NAME_COL, quizName);
        values.put(QUIZ_SECONDS_PER_QUESTION_COL, secondsPerQuestion);

        //pass the new content values to the table
        return db.insert(TABLE_QUIZ_NAME, null, values);

    }

    // function that is used to add a new question to the question table
    public long addNewQuestion(String quizID, String question, String answer) {

        ContentValues values = new ContentValues();

        //put each key and value into the ContentValues
        values.put(QUIZ_ID_COL, quizID);
        values.put(QUESTION_COL, question);
        values.put(ANSWER_COL, answer);

        //pass the new content values to the table
        return db.insert(TABLE_QUESTION_NAME, null, values);

    }

    //function that returns a Cursor that has all the quizzes
    public Cursor getAllQuizzes(){
        return db.query(TABLE_QUIZ_NAME, new String[]
                {ID_COL, QUIZ_NAME_COL, QUIZ_SECONDS_PER_QUESTION_COL},
                null, null, null, null, QUIZ_NAME_COL);
    }

    //function that returns a cursor with a specific quiz based on the quizId
    public Cursor getSingleQuiz(long quizId) throws SQLException {

        Cursor quiz = db.query(true, TABLE_QUIZ_NAME, new String[]
                {QUIZ_NAME_COL, QUIZ_SECONDS_PER_QUESTION_COL}, ID_COL + "=" + quizId,
                null, null, null, null, null);
        if(quiz != null){
            quiz.moveToFirst();
        }
        return quiz;
    }

    //function that deletes a quiz and all its associated questions
    public boolean deleteQuiz(long quizId) {
        boolean successQuestions = db.delete(TABLE_QUESTION_NAME,
                QUIZ_ID_COL + "=" + quizId, null) >0;

        boolean successQuiz = db.delete(TABLE_QUIZ_NAME,
                ID_COL + "=" + quizId, null) >0;


        return successQuiz;
    }

    public boolean updateQuiz(long quizId, String quizName, String seconds){

        ContentValues cval = new ContentValues();
        cval.put(QUIZ_NAME_COL, quizName);
        cval.put(QUIZ_SECONDS_PER_QUESTION_COL, seconds);
        return db.update(TABLE_QUIZ_NAME, cval, ID_COL + "=" + quizId, null) > 0;
    }

    //function that returns a Cursor with all the questions from a specific quiz using the quizId
    public Cursor getAllQuestions(long quizId){
        String whereClause = QUIZ_ID_COL + " = ?";

        return db.query(TABLE_QUESTION_NAME, new String[]
                        {ID_COL, QUESTION_COL, ANSWER_COL},
                whereClause, new String[] {String.valueOf(quizId)},
                null, null, null);
    }

    //function that returns a Cursor with a specific question based on the question id
    public Cursor getSingleQuestion(long questionId) throws SQLException {

        Cursor question = db.query(true, TABLE_QUIZ_NAME, new String[]
                        {ID_COL, QUESTION_COL, ANSWER_COL}, ID_COL + "=" + questionId,
                null, null, null, null, null);
        if(question != null){
            question.moveToFirst();
        }
        return question;
    }

    //function to delete a specific question
    public boolean deleteQuestion(long questionId) {

        return db.delete(TABLE_QUESTION_NAME,
                ID_COL + "=" + questionId, null) >0;
    }

    public boolean updateQuestion(long questionId, String question, String answer){

        ContentValues cval = new ContentValues();
        cval.put(QUESTION_COL, question);
        cval.put(ANSWER_COL, answer);
        return db.update(TABLE_QUESTION_NAME, cval,
                ID_COL + "=" + questionId, null) > 0;

    }



}