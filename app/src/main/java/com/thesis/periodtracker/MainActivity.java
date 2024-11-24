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
import com.thesis.periodtracker.Rasa.responses.UserInfoResponse;
import com.thesis.periodtracker.RecyclerView.MessageAdapter;
import com.thesis.periodtracker.RecyclerView.MessageModel;
import com.thesis.periodtracker.UserModels.UserInfoItem;
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

    private UserPreferenceHandler userPreference;
    private EditText inputMessage;
    private FrameLayout LayoutSend;
    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private ArrayList<MessageModel> messageList;
    private RasaApiService rasaApiService;
    private boolean firstTimeMessage;
    private String sessionID;
    private DatabaseHandler db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        userPreference = UserPreferenceHandler.getInstance(this);

        this.db = new DatabaseHandler(this);
        this.sessionID = db.createSession();

        firstTimeMessage = true;
        inputMessage = findViewById(R.id.inputMessage);
        LayoutSend = findViewById(R.id.LayoutSend);
        recyclerView = findViewById(R.id.recyclerview);

        messageList = new ArrayList<>();
        adapter = new MessageAdapter(messageList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // retrofit = RetrofitClient.getClient("http://0.0.0.0:5055");
        //rasaApiService = retrofit.create(RasaApiService.class);

        LayoutSend.setOnClickListener(v -> sendMessage());

        // trigger each new app instance starts
        this.sendMessage();
    }

    private void sendMessage() {
        String messageContent = inputMessage.getText().toString();

        if (firstTimeMessage) {
            if (userPreference.isFirstTimeUser()) {
                messageContent = "/start_new_user";
            } else {
                messageContent = "/set_user_info";
            }
        }

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
            if (!firstTimeMessage && !messageContent.startsWith("/")) {
                messageList.add(message);
                adapter.notifyItemInserted(messageList.size() - 1);
                recyclerView.scrollToPosition(messageList.size() - 1);
            }
            inputMessage.setText("");

            // Send message to Rasa
            RasaRequest rasaRequest;

            if (messageContent.equals("/set_user_info")) {
                // Send message with user data
                Log.d("PreferencesChecking", "Current Username: " + userPreference.getUsername());  // Should be Jane
                Log.d("PreferencesChecking", "Current Age: " + userPreference.getAge());  // Should be 30
                Log.d("PreferencesChecking", "Current InMenopause: " + userPreference.isInMenopause());  // Should be true
                Log.d("PreferencesChecking", "Should not be first time: " + userPreference.isFirstTimeUser());  // Should be false
                rasaRequest = new RasaRequest(
                    "android_user",
                    messageContent,
                    userPreference.getUsername(),
                    userPreference.getAge(),
                    userPreference.isInMenopause()
                );
            } else {
                // Send regular message
                rasaRequest = new RasaRequest("android_user", messageContent);
            }

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

                            } else if (rasaResponse instanceof UserInfoResponse) {
                                UserInfoResponse customResponse = (UserInfoResponse) rasaResponse;
                                control = customResponse.getCustom().getControl();

                                // Handle User Info Response
                                UserInfoItem user_info = getUserInfoItem(customResponse);
                                userPreference.saveUserInfo(user_info);
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
        Log.d("SymptomResponse", "\nJson: " + "{control: " + control + ",\n" +
                "symptom: " + symptom + ",\n" +
                "conditionScore: " + duration + ", \n" +
                "intensity: " + intensity + "}");
        userSymptom user_symptom = new userSymptom(symptom, duration, intensity);
        return user_symptom;
    }

    @NonNull
    private static UserInfoItem getUserInfoItem(UserInfoResponse customResponse) {
        String control = customResponse.getCustom().getControl();
        String name = customResponse.getCustom().getData().getName();
        int age = customResponse.getCustom().getData().getAge();
        boolean inMenopause = customResponse.getCustom().getData().isMenopause();
        Log.d("UserInfoResponse", "\nJson: " + "{control: " + control + ",\n" +
                "Username: " + name + ",\n" +
                "Age: " + age + ", \n" +
                "In Menopause?: " + inMenopause + "}");
        UserInfoItem user_info = new UserInfoItem(age, name, inMenopause);
        return user_info;
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

    private void createPDF() {
        // Issue
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String string = "Name";
                String dia = "Some text here";

                int age = 12;
                int x;
                int y = 120;

                //List<userSymptom> sList = db.getSymptoms(); doesn't work when I call this


                PdfDocument newPDF = new PdfDocument();
                Paint paint = new Paint();


                PdfDocument.PageInfo myPageInfo1 = new PdfDocument.PageInfo.Builder(960, 1680, 1).create();
                PdfDocument.Page myPage1 = newPDF.startPage(myPageInfo1);

                Canvas canvas = myPage1.getCanvas();

                paint.setTextSize(24.0f);
                canvas.drawText("Patient Name: " + string, 40, 50, paint);

                paint.setTextSize(24.0f);
                canvas.drawText("Age: " + age, 40, 80, paint);

                paint.setTextSize(24.0f);
                canvas.drawText("List of Found Symptoms: ", 40, 130, paint);

                paint.setTextSize(24.0f);
                /*
                for (int i = 0; i < sList.size(); i++){
                    canvas.drawText("Symptom: " + sList.get(i).getSymptomName(), 60, 50 + y, paint);
                    y+= 35;
                }
                */

                paint.setTextSize(24.0f);
                canvas.drawText("Chatbot Impression: " + dia, 40, 70 + y, paint);


                newPDF.finishPage(myPage1);

                File file = null;

                file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Thing.pdf");

                try {
                    newPDF.writeTo(new FileOutputStream(file));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                newPDF.close();
            }
        });

    }




    private void testUserPreferences() {
        UserPreferenceHandler userPreferences = UserPreferenceHandler.getInstance(this);

        // Test 1: Check initial/empty state
        Log.d("PreferencesTest", "\nTest 1: Checking initial state");
        Log.d("PreferencesTest", "Empty Username: '" + userPreferences.getUsername() + "'");  // Should be empty string
        Log.d("PreferencesTest", "Empty Age: " + userPreferences.getAge());  // Should be 0
        Log.d("PreferencesTest", "Empty InMenopause: " + userPreferences.isInMenopause());  // Should be false
        Log.d("PreferencesTest", "Should be first time: " + userPreferences.isFirstTimeUser());  // Should be true

        // Test 2: Save user info
        Log.d("PreferencesTest", "\nTest 2: Saving user information");
        userPreferences.testSave("Mary", 25, false);

        // Test 3: Verify saved data
        Log.d("PreferencesTest", "\nTest 3: Verifying saved data");
        Log.d("PreferencesTest", "Username: " + userPreferences.getUsername());  // Should be Mary
        Log.d("PreferencesTest", "Age: " + userPreferences.getAge());  // Should be 25
        Log.d("PreferencesTest", "InMenopause: " + userPreferences.isInMenopause());  // Should be false
        Log.d("PreferencesTest", "Should not be first time: " + userPreferences.isFirstTimeUser());  // Should be false

        // Test 4: Simulate app restart (create new instance)
        Log.d("PreferencesTest", "\nTest 4: Simulating app restart");
        UserPreferenceHandler restartedPreferences = UserPreferenceHandler.getInstance(this);
        Log.d("PreferencesTest", "After 'restart' - Username: " + restartedPreferences.getUsername());  // Should still be Mary
        Log.d("PreferencesTest", "After 'restart' - Age: " + restartedPreferences.getAge());  // Should still be 25
        Log.d("PreferencesTest", "After 'restart' - InMenopause: " + restartedPreferences.isInMenopause());  // Should still be false
        Log.d("PreferencesTest", "After 'restart' - Should not be first time: " + restartedPreferences.isFirstTimeUser());  // Should still be false

        // Test 5: Clear data and verify empty state
        Log.d("PreferencesTest", "\nTest 5: Testing clear functionality");
        userPreferences.clearUserData();
        Log.d("PreferencesTest", "After clear - Username: '" + userPreferences.getUsername() + "'");  // Should be empty
        Log.d("PreferencesTest", "After clear - Age: " + userPreferences.getAge());  // Should be 0
        Log.d("PreferencesTest", "After clear - InMenopause: " + userPreferences.isInMenopause());  // Should be false
        Log.d("PreferencesTest", "After clear - Should be first time: " + userPreferences.isFirstTimeUser());  // Should be true

        // Test 6: Save new data after clear
        Log.d("PreferencesTest", "\nTest 6: Saving new data after clear");
        userPreferences.testSave("Jane", 30, true);
        Log.d("PreferencesTest", "New Username: " + userPreferences.getUsername());  // Should be Jane
        Log.d("PreferencesTest", "New Age: " + userPreferences.getAge());  // Should be 30
        Log.d("PreferencesTest", "New InMenopause: " + userPreferences.isInMenopause());  // Should be true
        Log.d("PreferencesTest", "Should not be first time: " + userPreferences.isFirstTimeUser());  // Should be false
    }

}