package com.thesis.periodtracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.FrameLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

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
    }

    private void sendMessage() {
        String messageContent = inputMessage.getText().toString();
        OkHttpClient okHttpClient = new OkHttpClient();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.14:5005/webhooks/rest/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        String timestamp = null;
        if (!messageContent.isEmpty()) {
            timestamp = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
            MessageModel message = new MessageModel(messageContent, "", timestamp, MessageModel.SENT);
            messageList.add(message);
            adapter.notifyItemInserted(messageList.size() - 1);
            recyclerView.scrollToPosition(messageList.size() - 1);
            inputMessage.setText("");

            // Send message to Rasa
            RasaRequest rasaRequest = new RasaRequest("android_user", messageContent);
            rasaApiService = retrofit.create(RasaApiService.class);
            rasaApiService.sendMessage(rasaRequest).enqueue(new Callback<List<RasaResponse>>() {
                @Override
                public void onResponse(Call<List<RasaResponse>> call, Response<List<RasaResponse>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        for (RasaResponse rasaResponse : response.body()) {
                            String rasaMessage = rasaResponse.getText();
                            String rasaTimestamp = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
                            MessageModel responseMessage = new MessageModel(rasaMessage, "", rasaTimestamp, MessageModel.RECEIVED);
                            messageList.add(responseMessage);
                            adapter.notifyItemInserted(messageList.size() - 1);
                            recyclerView.scrollToPosition(messageList.size() - 1);
                        }
                    } else {
                        Log.e("RasaResponse", "Error: " + response.errorBody());
                    }
                }

                @Override
                public void onFailure(Call<List<RasaResponse>> call, Throwable t) {
                    Log.e("RasaResponse", "Failure: " + t.getMessage());
                }
            });
        }
        Log.d("MainActivity", "Timestamp: " + timestamp);
    }
}