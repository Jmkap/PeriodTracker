package com.thesis.periodtracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.text.SimpleDateFormat;


import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "DrFlowDatabase";

    public static final String SYMPTOMS_TABLE = "symptoms_table";
    public static final String DISEASES_TABLE = "previous_diseases_table";
    public static final String SESSIONS_TABLE = "sessions_table";
    public static final String IMPRESSION_TABLE = "impression_table";
    public static final String SESSION_SYMPTOMS_TABLE = "session_symptoms";
    public static final String SESSION_IMPRESSION_TABLE = "session_impression";

    public static final String SESSION_SYMP_SESSION_ID = "session_id";
    public static final String SESSION_SYMP_SYMPTOM_ID = "symptom_id";

    public static final String SESSION_IMP_SESSION_ID = "session_id";
    public static final String SESSION_IMP_DIAGNOSIS_ID = "impression_id";

    public static final String SYMPTOM_ID = "id";
    public static final String SYMPTOM_NAME = "symptom_name";
    public static final String SYMPTOM_DESCRIPTION = "chat_log";
    public static final String SYMPTOM_DURATION_DAYS = "duration_days";
    public static final String SYMPTOM_INTENSITY =  "intensity";

    public static final String DISEASE_ID = "id";
    public static final String DISEASE_SESSION_ID = "session_id";
    public static final String DISEASE_NAME = "disease_name";

    public static final String SESSION_ID = "id";
    public static final String SESSION_DATE = "date";

    public static final String IMPRESSION_ID = "id";
    public static final String IMPRESSION_DISEASE_NAME = "disease_name";
    public static final String IMPRESSION_SCORE = "score";
    public static final String IMPRESSION_RANK = "rank";

    private String currSessonID = "";
    private String currSympSessonID = "";
    private String currImpSessonID = "";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_SESSIONS_TABLE = "CREATE TABLE IF NOT EXISTS " + SESSIONS_TABLE + "(" +
                SESSION_ID + " TEXT PRIMARY KEY," +
                SESSION_DATE + " TEXT)";

        String CREATE_SYMPTOMS_TABLE = "CREATE TABLE IF NOT EXISTS " + SYMPTOMS_TABLE + "(" +
                SYMPTOM_ID + " TEXT PRIMARY KEY," +
                SYMPTOM_NAME + " TEXT," +
                SYMPTOM_DESCRIPTION + " TEXT," +
                SYMPTOM_DURATION_DAYS + " INTEGER, " +
                SYMPTOM_INTENSITY + " INTEGER " + ")";

        String CREATE_DISEASES_TABLE = "CREATE TABLE IF NOT EXISTS " + DISEASES_TABLE + "(" +
                DISEASE_ID + " TEXT PRIMARY KEY," +
                DISEASE_SESSION_ID + " TEXT," +
                DISEASE_NAME + " TEXT, " +
                "FOREIGN KEY(" + DISEASE_SESSION_ID + ") REFERENCES " + SESSIONS_TABLE + "(" + SESSION_ID + "))";

        String CREATE_DIAGNOSIS_TABLE = "CREATE TABLE IF NOT EXISTS " + IMPRESSION_TABLE + "(" +
                IMPRESSION_ID + " TEXT PRIMARY KEY," +
                IMPRESSION_DISEASE_NAME + " TEXT," +
                IMPRESSION_SCORE + " FLOAT," +
                IMPRESSION_RANK + " INTEGER)";

        String CREATE_SESSION_SYMPTOMS_TABLE = "CREATE TABLE IF NOT EXISTS " + SESSION_SYMPTOMS_TABLE + "(" +
                SESSION_SYMP_SESSION_ID + " TEXT," +
                SESSION_SYMP_SYMPTOM_ID + " TEXT," +
                "FOREIGN KEY(" + SESSION_SYMP_SESSION_ID + ") REFERENCES " + SESSIONS_TABLE + "(" + SESSION_ID + ")," +
                "FOREIGN KEY(" + SESSION_SYMP_SYMPTOM_ID + ") REFERENCES " + SYMPTOMS_TABLE + "(" + SYMPTOM_ID + "))";

        String CREATE_SESSION_DIAGNOSIS_TABLE = "CREATE TABLE IF NOT EXISTS " + SESSION_IMPRESSION_TABLE + "(" +
                SESSION_IMP_SESSION_ID + " TEXT," +
                SESSION_IMP_DIAGNOSIS_ID + " TEXT," +
                "FOREIGN KEY(" + SESSION_IMP_SESSION_ID + ") REFERENCES " + SESSIONS_TABLE + "(" + SESSION_ID + ")," +
                "FOREIGN KEY(" + SESSION_IMP_DIAGNOSIS_ID + ") REFERENCES " + IMPRESSION_TABLE + "(" + IMPRESSION_ID + "))";

        db.execSQL(CREATE_SYMPTOMS_TABLE);
        db.execSQL(CREATE_DISEASES_TABLE);
        db.execSQL(CREATE_SESSIONS_TABLE);
        db.execSQL(CREATE_DIAGNOSIS_TABLE);
        db.execSQL(CREATE_SESSION_SYMPTOMS_TABLE);
        db.execSQL(CREATE_SESSION_DIAGNOSIS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SYMPTOMS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DISEASES_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + SESSIONS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + IMPRESSION_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + SESSION_SYMPTOMS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + SESSION_IMPRESSION_TABLE);
        onCreate(db);
    }

    public String createSession (){
        SQLiteDatabase db = this.getWritableDatabase();

        String sessionID = UUID.randomUUID().toString();

        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        ContentValues values = new ContentValues();
        values.put(SESSION_ID, sessionID);
        values.put(SESSION_DATE, currentDate);

        db.insert(SESSIONS_TABLE, null, values);

        this.currSessonID = sessionID;
        return sessionID;
    }

    public void createSymptomSession (String symptomID, String sessionID) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(SESSION_SYMP_SYMPTOM_ID, symptomID);
        values.put(SESSION_SYMP_SESSION_ID, sessionID);

        db.insert(SESSION_SYMPTOMS_TABLE, null, values);
    }

    public void createImpressionSession (String impressionID, String sessionID){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(IMPRESSION_ID, impressionID);
        values.put(SESSION_ID, sessionID);

        db.insert(SESSION_IMPRESSION_TABLE, null, values);
    }

    public String insertSymptoms(String symptomName, String chatLog, int intensity, int duration){
        SQLiteDatabase db = this.getWritableDatabase();

        String symptomID = UUID.randomUUID().toString();

        ContentValues values = new ContentValues();
        values.put(SYMPTOM_ID, symptomID);
        values.put(SYMPTOM_NAME, symptomName);
        values.put(SYMPTOM_DESCRIPTION, chatLog);
        values.put(SYMPTOM_DURATION_DAYS, duration);
        values.put(SYMPTOM_INTENSITY, intensity);

        db.insert(SYMPTOMS_TABLE, null, values);

        return symptomID;
    }

    public String insertImpression(String diseaseName, float score, int rank){
        SQLiteDatabase db = this.getWritableDatabase();

        String impressionID = UUID.randomUUID().toString();

        ContentValues values = new ContentValues();
        values.put(IMPRESSION_ID, impressionID);
        values.put(IMPRESSION_DISEASE_NAME, diseaseName);
        values.put(IMPRESSION_SCORE, score);
        values.put(IMPRESSION_RANK, rank);


        db.insert(IMPRESSION_TABLE, null, values);

        return impressionID;
    }
}
