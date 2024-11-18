package com.thesis.periodtracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thesis.periodtracker.Rasa.RasaApiService;
import com.thesis.periodtracker.Rasa.RasaRequest;
import com.thesis.periodtracker.Rasa.RasaResponseDeserializer;
import com.thesis.periodtracker.Rasa.responses.ImpressionResponse;
import com.thesis.periodtracker.Rasa.responses.RasaResponse;
import com.thesis.periodtracker.Rasa.responses.SymptomResponse;
import com.thesis.periodtracker.Rasa.responses.TextResponse;
import com.thesis.periodtracker.RecyclerView.MessageAdapter;
import com.thesis.periodtracker.RecyclerView.MessageModel;
import com.thesis.periodtracker.UserModels.userImpression;
import com.thesis.periodtracker.UserModels.userSymptom;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {

    private EditText inputMessage;
    private FrameLayout LayoutSend;
    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private ArrayList<MessageModel> messageList;
    private RasaApiService rasaApiService;
    private boolean firstTimeMessage;
    private String sessionID;
    private DatabaseHandler db;

    private userSymptom s = new userSymptom("Name", 3, 5);

    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.db = new DatabaseHandler(this);
        this.sessionID = db.createSession();

        ArrayList<String> sList = new ArrayList<>();

        db.insertSymptoms(s);
        //sList = db.getSymptoms();


        button = findViewById(R.id.button);
        ActivityCompat.requestPermissions(this,new String[]
                {Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

        createPDF();

        /*
        firstTimeMessage = true;
        inputMessage = findViewById(R.id.inputMessage);
        LayoutSend = findViewById(R.id.LayoutSend);
        recyclerView = findViewById(R.id.recyclerview);

        messageList = new ArrayList<>();
        adapter = new MessageAdapter(messageList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
         */

        // retrofit = RetrofitClient.getClient("http://0.0.0.0:5055");
        //rasaApiService = retrofit.create(RasaApiService.class);

        //LayoutSend.setOnClickListener(v -> sendMessage());

        //inputMessage.setText("Hello");
        //this.sendMessage();
    }

    private void sendMessage() {
        String messageContent = inputMessage.getText().toString();
        OkHttpClient okHttpClient = new OkHttpClient();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(RasaResponse.class, new RasaResponseDeserializer())
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.254.102:5005/webhooks/rest/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        String timestamp = null;
        if (!messageContent.isEmpty()) {
            timestamp = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
            MessageModel message = new MessageModel(messageContent, "", timestamp, MessageModel.SENT);
            if (!firstTimeMessage) {
                messageList.add(message);
                adapter.notifyItemInserted(messageList.size() - 1);
                recyclerView.scrollToPosition(messageList.size() - 1);
            }
            inputMessage.setText("");

            // Send message to Rasa
            RasaRequest rasaRequest = new RasaRequest("android_user", messageContent);
            rasaApiService = retrofit.create(RasaApiService.class);
            rasaApiService.sendMessage(rasaRequest).enqueue(new Callback<List<RasaResponse>>() {
                @Override
                public void onResponse(Call<List<RasaResponse>> call, Response<List<RasaResponse>> response) {
                    if (response.isSuccessful() && response.body() != null) {

                        for (RasaResponse rasaResponse : response.body()) {
                            String user_id = rasaResponse.getRecipientId();
                            String rasaMessage = null;
                            String control = null;
                            if (rasaResponse instanceof SymptomResponse) {
                                SymptomResponse customResponse = (SymptomResponse) rasaResponse;
                                control = customResponse.getCustom().getControl();

                                // Handle symptom response
                                userSymptom user_symptom = getUserSymptom(customResponse);
                                String symptomID = db.insertSymptoms(user_symptom);
                                db.createSymptomSession (symptomID, sessionID);

                            } else if (rasaResponse instanceof ImpressionResponse) {
                                ImpressionResponse customResponse = (ImpressionResponse) rasaResponse;
                                control = customResponse.getCustom().getControl();

                                // Handle impression response
                                userImpression impression = getUserImpression(customResponse);
                                String impressionID = db.insertImpression(impression);
                                db.createImpressionSession (impressionID, sessionID);

                            } else {
                                rasaMessage = ((TextResponse) rasaResponse).getText();
                                Log.d("TextResponse", "User ID: " + user_id + "\nText: " + rasaMessage);
                                // Handle text response
                            }
                            String rasaTimestamp = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
                            if (rasaMessage != null) {
                                MessageModel responseMessage = new MessageModel(rasaMessage, "", rasaTimestamp, MessageModel.RECEIVED);
                                messageList.add(responseMessage);
                                adapter.notifyItemInserted(messageList.size() - 1);
                                recyclerView.scrollToPosition(messageList.size() - 1);
                            }

                            //debugging
                            Log.d("RasaResponse", "User ID: " + user_id + "\nText: " + rasaMessage + "\nJson: " + control);
                        }
                    } else {
                        Log.e("RasaResponse", "Error: " + response.errorBody());
                    }
                    if (firstTimeMessage) {
                        firstTimeMessage = false;
                    }
                }



                @Override
                public void onFailure(Call<List<RasaResponse>> call, Throwable t) {
                    Log.e("RasaResponse", "Failure: " + t.getMessage());
                    if (firstTimeMessage) {
                        firstTimeMessage = false;
                    }
                }
            });
        }
        Log.d("MainActivity", "Timestamp: " + timestamp);
    }

    @NonNull
    private static userImpression getUserImpression(ImpressionResponse customResponse) {
        String control = customResponse.getCustom().getControl();
        String conditionName = customResponse.getCustom().getData().getConditionName();
        int conditionScore = customResponse.getCustom().getData().getConditionScore();
        boolean lifeThreat = customResponse.getCustom().getData().isLifeThreat();
        int rank = customResponse.getCustom().getData().getRank();
        Log.d("ConditionResponse", "\nJson: " + "{control: " + control + ",\n" +
                                                  "conditionName: " + conditionName + ",\n" +
                                                  "conditionScore: " + conditionScore + ", \n" +
                                                  "lifeThreat: " + lifeThreat + "}");
        userImpression impression = new userImpression(conditionName, rank, conditionScore);
        return impression;
    }

    @NonNull
    private static userSymptom getUserSymptom(SymptomResponse customResponse) {
        String control = customResponse.getCustom().getControl();
        String symptom = customResponse.getCustom().getData().getSymptomName();
        int duration = customResponse.getCustom().getData().getDuration();
        int intensity = customResponse.getCustom().getData().getIntensity();
        Log.d("ConditionResponse", "\nJson: " + "{control: " + control + ",\n" +
                                                  "symptom: " + symptom + ",\n" +
                                                  "conditionScore: " + duration + ", \n" +
                                                  "intensity: " + intensity + "}");
        userSymptom user_symptom = new userSymptom(symptom, duration, intensity);
        return user_symptom;
    }

    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("MainActivity", ex.toString());
        }
        return null;
    }

    private void createPDF(UserInfoItem item, String sessionID) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //LocalDate date = LocalDate.now();
                String date = db.getDate(sessionID);

                ArrayList<userSymptom> sList = new ArrayList<>();
                sList = db.getSymptoms(sessionID);

                ArrayList<userImpression> impList = new ArrayList<>();
                impList = db.getImpressions(sessionID);

                String string = item.getName();
                //String dia = "Some text here";

                int age = item.getAge();
                int x;
                int y = 120;

                int check;
                int set;
                userImpression temp;

                //censors Ovarian Cancer
                for (int i = 0; i < impList.size(); i++){
                    if (Objects.equals(impList.get(i).getDiseaseName(), "Ovarian Cancer")){
                        impList.get(i).setDiseaseName("WARNING!");
                    }
                }

                //sorts the impression list using Bubble Sort
                for (check = 0; check < impList.size(); check++){
                    for (set = impList.size() - 1; set > check; set--){
                        if (impList.get(check).getRank() > impList.get(set).getRank()){
                            temp = impList.get(check);
                            impList.set(check, impList.get(set));
                            impList.set(set, temp);
                        }
                    }
                }

                PdfDocument newPDF = new PdfDocument();
                Paint paint = new Paint();


                PdfDocument.PageInfo myPageInfo1 = new PdfDocument.PageInfo.Builder(960, 1680, 1).create();
                PdfDocument.Page myPage1 = newPDF.startPage(myPageInfo1);

                Canvas canvas = myPage1.getCanvas();

                paint.setTextSize(12.0f);
                canvas.drawText("Patient Name: " + string, 40, 50, paint);


                paint.setTextSize(12.0f);
                canvas.drawText("Date: " + date, 800, 50, paint);

                paint.setTextSize(12.0f);
                canvas.drawText("Age: " + age, 40, 80, paint);

                paint.setTextSize(12.0f);
                canvas.drawText("Number of Found Symptoms: " + sList.size(), 40, 130, paint);


                for (int i = 0; i < sList.size(); i++){
                    canvas.drawText("Symptom: " + sList.get(i).getSymptomName(), 60, 50 + y, paint);
                    canvas.drawText("Duration (days): " + sList.get(i).getDurationDays(), 350, 50 + y, paint);
                    canvas.drawText("Intensity (1 - 10): " + sList.get(i).getIntensity(), 500, 50 + y, paint);
                    y+= 20;
                }



                paint.setTextSize(12.0f);
                /*
                for (int i = 0; i < sList.size(); i++){
                    canvas.drawText("Symptom: " + sList.get(i), 60, 50 + y, paint);
                    y+= 35;
                }
                */

                paint.setTextSize(12.0f);

                canvas.drawText("Chatbot Impression: " + impList.get(0).getDiseaseName(), 40, 70 + y, paint);

                for (int i = 0; i < 3; i++){
                    int place = i + 1;

                    if(impList.isEmpty()){
                        canvas.drawText("Rank " + place + ": ", 60, 100 + y, paint);
                    }
                    else{
                        canvas.drawText("Rank " + place + ": " + impList.get(i).getDiseaseName() + "      " + "Confidence: " + impList.get(i).getScore(), 60, 100 + y, paint);
                    }

                    y += 20;
                }


                newPDF.finishPage(myPage1);

                //File file = null;

                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Generated Report.pdf");

                try {
                    newPDF.writeTo(new FileOutputStream(file));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                newPDF.close();
            }
        });

            }


}