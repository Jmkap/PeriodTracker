package com.thesis.periodtracker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "UserDatabase";
    public static final String USER_TABLE = "user_table";
    public static final String SYMPTOMS_TABLE = "symptoms_table";
    public static final String DISEASES_TABLE = "diseases_table";

    public static final String USER_ID = "id";
    public static final String USER_AGE = "age";
    public static final String USER_NAME = "name";
    public static final String USER_NICKNAME = "nickname";

    public static final String SYMPTOM_ID = "id";
    public static final String SYMPTOM_USER_ID = "user_id";
    public static final String SYMPTOM_NAME = "symptom_name";
    public static final String SYMPTOM_DESCRIPTION = "description";
    public static final String SYMPTOM_DURATION_DAYS = "duration_days";

    public static final String DISEASE_ID = "id";
    public static final String DISEASE_USER_ID = "user_id";
    public static final String DISEASE_NAME = "disease_name";
    public static final String DISEASE_DESCRIPTION = "description";
    public static final String DISEASE_LIFE_THREATENING = "life_threatening";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS " + USER_TABLE + "(" +
                USER_ID + " INTEGER PRIMARY KEY," +
                USER_AGE + " INTEGER," +
                USER_NAME + " TEXT," +
                USER_NICKNAME + " TEXT)";

        String CREATE_SYMPTOMS_TABLE = "CREATE TABLE IF NOT EXISTS " + SYMPTOMS_TABLE + "(" +
                SYMPTOM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                SYMPTOM_USER_ID + " INTEGER," +
                SYMPTOM_NAME + " TEXT," +
                SYMPTOM_DESCRIPTION + " TEXT," +
                SYMPTOM_DURATION_DAYS + " INTEGER," +
                "FOREIGN KEY(" + SYMPTOM_USER_ID + ") REFERENCES " + USER_TABLE + "(" + USER_ID + "))";

        String CREATE_DISEASES_TABLE = "CREATE TABLE IF NOT EXISTS " + DISEASES_TABLE + "(" +
                DISEASE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DISEASE_USER_ID + " INTEGER," +
                DISEASE_NAME + " TEXT," +
                DISEASE_DESCRIPTION + " TEXT," +
                DISEASE_LIFE_THREATENING + " INTEGER," +
                "FOREIGN KEY(" + DISEASE_USER_ID + ") REFERENCES " + USER_TABLE + "(" + USER_ID + "))";


        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_SYMPTOMS_TABLE);
        db.execSQL(CREATE_DISEASES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + SYMPTOMS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DISEASES_TABLE);
        onCreate(db);
    }
}