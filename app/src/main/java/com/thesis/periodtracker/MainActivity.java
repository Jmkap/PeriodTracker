package com.thesis.periodtracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.FrameLayout;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        DatabaseHandler databaseHandler = new DatabaseHandler(this);
        sessionID = databaseHandler.createSession();

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

        inputMessage.setText("Hello");
        this.sendMessage();
    }

    private void sendMessage() {
        String messageContent = inputMessage.getText().toString();
        OkHttpClient okHttpClient = new OkHttpClient();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.254.102:5005/webhooks/rest/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
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

}