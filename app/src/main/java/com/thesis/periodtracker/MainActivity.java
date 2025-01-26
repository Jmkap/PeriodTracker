package com.thesis.periodtracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thesis.periodtracker.Rasa.RasaApiService;
import com.thesis.periodtracker.Rasa.RasaRequest;
import com.thesis.periodtracker.Rasa.RasaResponseDeserializer;
import com.thesis.periodtracker.Rasa.responses.ImageResponse;
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

    private String debugging;
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
    private AppCompatImageView imageDownload;
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
        imageDownload = findViewById(R.id.imageDownload);

        messageList = new ArrayList<>();
        adapter = new MessageAdapter(messageList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // retrofit = RetrofitClient.getClient("http://0.0.0.0:5055");
        //rasaApiService = retrofit.create(RasaApiService.class);

        LayoutSend.setOnClickListener(v -> sendMessage());
        imageDownload.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                createPDF();
            }
            else if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Request permission if not granted
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        100);
            } else {
                // Permission already granted, create PDF directly
                createPDF();
            }
        });

        // trigger each new app instance starts
        this.sendMessage();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                createPDF(); // Retry PDF creation if permission granted
            } else {
                Snackbar.make(findViewById(android.R.id.content),
                        "Cannot create PDF without storage permission",
                        Snackbar.LENGTH_LONG).show();
            }
        }
    }

    private void sendMessage() {
        String messageContent = inputMessage.getText().toString()   ;

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
            MessageModel message = new MessageModel(messageContent, null, "", timestamp, MessageModel.SENT);
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
                    debugging = response.raw().toString();
                    Log.d("OnResponse", "Entered On Response: debugging");
                    if (response.isSuccessful() && response.body() != null) {
                        for (RasaResponse rasaResponse : response.body()) {
                            String user_id = rasaResponse.getRecipientId();
                            String rasaMessage = null;
                            String imageURL = null;
                            String control = null;
                            if (rasaResponse instanceof SymptomResponse) {
                                SymptomResponse customResponse = (SymptomResponse) rasaResponse;
                                control = customResponse.getCustom().getControl();

                                // Handle symptom response
                                userSymptom userSymptom = getUserSymptom(customResponse);
                                String symptomID = db.insertSymptoms(userSymptom);
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
                            } else if (rasaResponse instanceof ImageResponse){
                                ImageResponse imageResponse = (ImageResponse) rasaResponse;
                                rasaMessage = imageResponse.getText();
                                imageURL = imageResponse.getImageUrl();
                                Log.d("ImageResponse", "User ID: " + user_id + "\nImage URL: " + imageURL);
                            } else {
                                rasaMessage = ((TextResponse) rasaResponse).getText();
                                Log.d("TextResponse", "User ID: " + user_id + "\nText: " + rasaMessage);
                                // Handle text response
                            }
                            String rasaTimestamp = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
                            if (rasaMessage != null) {
                                MessageModel responseMessage = new MessageModel(rasaMessage, imageURL, "", rasaTimestamp, MessageModel.RECEIVED);
                                messageList.add(responseMessage);
                                adapter.notifyItemInserted(messageList.size() - 1);
                                recyclerView.scrollToPosition(messageList.size() - 1);
                            }

                            //debugging
                            Log.d("RasaResponse", "User ID: " + user_id + "\nText: " + rasaMessage + "\nJson: " + control);
                        }
                    } else {
                        Log.e("RasaResponse", "Error: " + response.errorBody() );
                    }
                    if (firstTimeMessage) {
                        firstTimeMessage = false;
                    }
                }



                @Override
                public void onFailure(Call<List<RasaResponse>> call, Throwable t) {
                    Log.e("RasaResponse", "Failure: " + t + "\n" + debugging);
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
        final float MARGIN_LEFT = 40;
        final float MARGIN_RIGHT = 920;  // Page width - margin
        final float TEXT_SIZE = 16.0f;
        final float LINE_HEIGHT = 25;
        float currentY = 50;  // Starting Y position
        boolean no_symp, no_imp = true;

        String currDate = db.getSessionDate(this.sessionID);
        String userName = userPreference.getUsername();
        int age = userPreference.getAge();

        List<userSymptom> symptomsList = db.getSymptomsBySessionId(this.sessionID);
        List<userImpression> impressionsList = db.getImpressionsBySessionId(this.sessionID);

        PdfDocument newPDF = new PdfDocument();
        Paint paint = new Paint();
        paint.setTextSize(TEXT_SIZE);
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);

        PdfDocument.PageInfo myPageInfo1 = new PdfDocument.PageInfo.Builder(960, 1680, 1).create();
        PdfDocument.Page myPage1 = newPDF.startPage(myPageInfo1);
        Canvas canvas = myPage1.getCanvas();

        // Calculate column widths and positions
        float dateWidth = paint.measureText("Date: " + currDate);
        float symptomColWidth = 300;  // Width for symptom names
        float durationColWidth = 200;  // Width for duration
        float intensityColWidth = 200;  // Width for intensity

        // Column start positions
        float durationColX = MARGIN_LEFT + symptomColWidth;
        float intensityColX = durationColX + durationColWidth;

        // Header section with right-aligned date
        canvas.drawText("Patient Name: " + userName, MARGIN_LEFT, currentY, paint);
        canvas.drawText("Date: " + currDate, MARGIN_RIGHT - dateWidth, currentY, paint);
        currentY += LINE_HEIGHT;

        canvas.drawText("Age: " + age, MARGIN_LEFT, currentY, paint);
        currentY += LINE_HEIGHT * 2;

        // Symptoms section
        if (symptomsList != null && !symptomsList.isEmpty()) {
            canvas.drawText("Number of Found Symptoms: " + symptomsList.size(), MARGIN_LEFT, currentY, paint);
            currentY += LINE_HEIGHT * 1.5f;

            // Draw column headers
            paint.setFakeBoldText(true);  // Make headers bold
            canvas.drawText("Symptom", MARGIN_LEFT, currentY, paint);
            canvas.drawText("Duration (days)", durationColX, currentY, paint);
            canvas.drawText("Intensity (1-10)", intensityColX, currentY, paint);
            paint.setFakeBoldText(false);
            currentY += LINE_HEIGHT * 1.5f;

            // Draw symptoms in columns
            for (userSymptom symptom : symptomsList) {
                // Truncate symptom name if too long
                String symptomName = symptom.getSymptomName();
                if (paint.measureText(symptomName) > symptomColWidth - 20) {
                    while (paint.measureText(symptomName + "...") > symptomColWidth - 20) {
                        symptomName = symptomName.substring(0, symptomName.length() - 1);
                    }
                    symptomName += "...";
                }

                canvas.drawText(symptomName, MARGIN_LEFT, currentY, paint);
                canvas.drawText(String.valueOf(symptom.getDurationDays()), durationColX, currentY, paint);
                canvas.drawText(String.valueOf(symptom.getIntensity()), intensityColX, currentY, paint);

                currentY += LINE_HEIGHT;
            }
            no_symp = false;
        } else {
            canvas.drawText("No symptoms recorded", MARGIN_LEFT, currentY, paint);
            currentY += LINE_HEIGHT;
            no_symp = true;
        }

        currentY += LINE_HEIGHT * 1.5f;

        // Impressions section
        if (impressionsList != null && !impressionsList.isEmpty()) {
            paint.setFakeBoldText(true);
            canvas.drawText("Chatbot Impression: " + impressionsList.get(0).getDiseaseName(),
                    MARGIN_LEFT, currentY, paint);
            currentY += LINE_HEIGHT * 1.5f;

            // Draw impression column headers
            canvas.drawText("Condition", MARGIN_LEFT, currentY, paint);
            canvas.drawText("Confidence (%)", durationColX, currentY, paint);
            paint.setFakeBoldText(false);
            currentY += LINE_HEIGHT;

            // Draw impressions in columns
            for (int i = 0; i < impressionsList.size(); i++) {
                userImpression impression = impressionsList.get(i);
                String rankText = String.format(Locale.US, "Rank %d: %s", (i + 1),
                        impression.getDiseaseName());

                canvas.drawText(rankText, MARGIN_LEFT, currentY, paint);
                canvas.drawText(String.format(Locale.US, "%.1f", impression.getScore()),
                        durationColX, currentY, paint);

                currentY += LINE_HEIGHT;
            }
            no_imp = false;
        } else {
            canvas.drawText("No impressions available", MARGIN_LEFT, currentY, paint);
            no_imp = true;
        }

        newPDF.finishPage(myPage1);

        String fileDate = new SimpleDateFormat("yyyy-MM-dd_HHmmss", Locale.getDefault()).format(new Date());


        File pdfFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "ImpressionReport-" + fileDate + ".pdf");

        try {
            newPDF.writeTo(new FileOutputStream(pdfFile));
            Snackbar.make(findViewById(android.R.id.content),
                    "PDF generated successfully! Check your downloads folder",
                    Snackbar.LENGTH_LONG).show();
        } catch (IOException e) {
            Log.e("PDF_CREATION", "Error writing PDF file", e);
            Snackbar.make(findViewById(android.R.id.content),
                    "Error generating PDF",
                    Snackbar.LENGTH_LONG).show();
        } finally {
            newPDF.close();
        }
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