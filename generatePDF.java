package com.thesis.periodtracker;

import android.graphics.pdf.PdfDocument;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class generatePDF {

    private int age;

    private String name;

    //DatabaseHandler db = new DatabaseHandler();
    public void createPDF(int age, String name){

        this.name = name;
        this.age = age;

        int x = 1;
        int y = 120;

        PdfDocument newPDF = new PdfDocument();
        Paint paint = new Paint();

        PdfDocument.PageInfo myPageInfo1 = new PdfDocument.PageInfo.Builder(1080, 1920, 1).create();
        PdfDocument.Page myPage1 = newPDF.startPage(myPageInfo1);

        Canvas canvas = myPage1.getCanvas();

        paint.setTextSize(24.0f);
        canvas.drawText("Patient Name: " + name, 40, 50, paint);

        paint.setTextSize(24.0f);
        canvas.drawText("Age: " + age, 40, 70, paint);

        paint.setTextSize(24.0f);
        canvas.drawText("List of Found Symptoms: ", 40, 120, paint);

        paint.setTextSize(24.0f);
        while(){
            canvas.drawText("Symptom: " + y, 60, 50 + y, paint);
            y+= 20;
        }

        paint.setTextSize(24.0f);
        canvas.drawText("Chatbot Impression: " +  x  + " " + , 40, 70 + y, paint);

        newPDF.finishPage(myPage1);

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Thing.pdf");

        try {
            newPDF.writeTo(new FileOutputStream(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        newPDF.close();

    }
}
