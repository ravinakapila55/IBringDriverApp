package com.ibring_driver.provider.chat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ibring_driver.provider.Constant.URLHelper;
import com.ibring_driver.provider.Helper.SharedHelper;
import com.ibring_driver.provider.R;
import com.ibring_driver.provider.XuberServicesApplication;
import com.ibring_driver.provider.retrofit.RetrofitResponse;
import com.ibring_driver.provider.retrofit.RetrofitService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Ack;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener, RetrofitResponse {


    private static final String TAG = ChatActivity.class.getSimpleName();
    Socket mSocket;
    String user_name;
    String conversationId;

    String senderId;
    String receiverId;

    EditText edt_message;

    TextView toolbar_title;
    ImageView ivBack;

    RecyclerView recycler;

    List<String> stringList = new ArrayList<>();

    ArrayList<ChatModel> messageDataList = new ArrayList<>();


    ChatAdapter chatAdapter;


    String userId;
    String username;

    ImageView send;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        findIds();


    }

    public void findIds()
    {
        edt_message=(EditText)findViewById(R.id.edt_message);
        recycler=(RecyclerView)findViewById(R.id.recycler);
        toolbar_title=(TextView) findViewById(R.id.toolbar_title);
        send=(ImageView) findViewById(R.id.send);
        ivBack=(ImageView) findViewById(R.id.ivBack);
        ivBack.setVisibility(View.VISIBLE);
        send.setOnClickListener(this);
        ivBack.setOnClickListener(this);

        userId= String.valueOf(SharedHelper.getKey(this,"id"));

        username= String.valueOf(SharedHelper.getKey(this,"first_name"));
//        userId= "59";
        Log.e("user","ddd "+userId);
        Log.e("username","username "+username);

        recycler.setLayoutManager(new LinearLayoutManager(ChatActivity.this, LinearLayoutManager.VERTICAL, false));


        chatAdapter = new ChatAdapter( ChatActivity.this,messageDataList);
        recycler.setAdapter(chatAdapter);

        Intent intent = getIntent();
        if (intent != null)
        {
//            doctor_id = intent.getStringExtra("doctor_id");
            receiverId = intent.getStringExtra("receiver_id");
            user_name = intent.getStringExtra("receiver_name");
            senderId = userId;
//            conversationId = intent.getStringExtra("conversationId");
//            receiverId = doctor_id;
        }

        Log.e("Chat "," senderId "+senderId +
                " receiverId "+receiverId );
        toolbar_title.setText(user_name);

        callGetConversationID();

        setConnectionListner();
    }

    public void setConnectionListner() {
        try {
            Log.e(TAG, "connectSocketChattt: " + "inside");
            Log.e(TAG, "Url: " + senderId);


            XuberServicesApplication app = (XuberServicesApplication) getApplication();

            mSocket = app.getSocket();

            mSocket.on(Socket.EVENT_CONNECT, onConnect);
            mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onTimeConnectError);
            mSocket.on("message_received", onNewMessage);

            mSocket.connect();
//emitRoomJoin();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Emitter.Listener onConnect = new Emitter.Listener()
    {
        @Override
        public void call(final Object... args)
        {
            Log.e(TAG, "call: onConnectCChHHat" + args.length);
            Log.e(TAG, "call: onConnect" + args.toString());

            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    mSocket.emit("init", userId);
                    Log.e(TAG, "onConnect");
                    for (int jj = 0; jj < args.length; jj++)
                    {
                        Log.e(TAG, "OnConnectResponseDriverHome " + args[jj]);
                    }
                }
            });
        }
    };


    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.e(TAG, "call: onDisconnect" + args.length);
            Log.e(TAG, "call: onDisconnect" + args.toString());

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "onDisconnect");
                    for (int jj = 0; jj < args.length; jj++) {
                        Log.e(TAG, "onDisconnectResponseDriverHome " + args[jj]);
                    }
                }
            });
        }
    };


    private Emitter.Listener onConnectError = new Emitter.Listener()
    {
        @Override
        public void call(final Object... args) {
            Log.e(TAG, "call: onConnectError" + args.length);
            Log.e(TAG, "call: onConnectError" + args.toString());

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "onConnectError");
                    for (int jj = 0; jj < args.length; jj++) {
                        Log.e(TAG, "onConnectErrorResponseDriverHome " + args[jj]);
                    }
                }
            });
        }
    };


    private Emitter.Listener onTimeConnectError = new Emitter.Listener()
    {
        @Override
        public void call(final Object... args)
        {
            Log.e(TAG, "call: onTimeConnectError" + args.length);
            Log.e(TAG, "call: onTimeConnectError" + args.toString());

            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    Log.e(TAG, "onTimeConnectError");
                    for (int jj = 0; jj < args.length; jj++)
                    {
                        Log.e(TAG, "onTimeConnectErrorResponseDriverHome " + args[jj]);
                    }
                }
            });
        }
    };

    private Emitter.Listener onNewMessage = new Emitter.Listener()
    {
        @Override
        public void call(final Object... args)
        {

            Log.e(TAG, "call: onNewMessage " + args.length);
            Log.e(TAG, "call: onNewMessage " + args.toString());

            String test=args[0].toString();
            Log.e(TAG, "test " + test);

            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    JSONObject data = (JSONObject) args[0];
                    Log.e(TAG, String.valueOf(data));

                    String username;
                    String message;
                    String sender;
                    String createdAt;
                    String receiver;
                    try
                    {
                        username = data.getString("name");
                        message = data.getString("message");
                        sender = data.getString("sender_id");
//                        receiver = data.getString("receiver");
                        createdAt = data.getString("time");
                        addMessage(username, message, sender, createdAt);
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                        return;
                    }

                    // add the message to view
                    // addMessage(username, message);
                }
            });
        }
    };

    private void attemptSend()
    {
        final String message = edt_message.getText().toString().trim();
        if (TextUtils.isEmpty(message))
        {
            return;
        }

        JSONObject obj = new JSONObject();
        try
        {
            obj.put("sender", userId);
            obj.put("receiver", receiverId);
            obj.put("message", message);
            obj.put("username", username);

            Log.e("SendParams ",obj.toString());
        }
        catch (JSONException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        edt_message.setText("");
        // mSocket.emit("message_sent", senderId, String.valueOf(obj));

        mSocket.emit("message_sent", String.valueOf(obj), new Ack()
        {
            @Override
            public void call(Object... args)
            {
                Log.e("socketio", "message back: " + args);
            }
        });
        //addMessage(user_name, message);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();
        mSocket.off(Socket.EVENT_CONNECT, onConnect);
        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.off("message_received", onNewMessage);
        XuberServicesApplication.setActivity(null);
    }


    public void addMessage(String userName, String message, String senderId, String createdAt)
    {

        String title;
        int viewType;

        ChatModel messageData = new ChatModel();

        if (senderId.equals(userId)) {
            title = "You";
            viewType = ListType.SENDER_VIEW_TYPE;
        } else {
            title = userName;
            viewType = ListType.RECEIVER_VIEW_TYPE;
        }

        messageData.setId("");
        messageData.setConversationId(conversationId);
        messageData.setMessage(message);
        messageData.setSender(senderId);
        messageData.setCreatedAt("");
        messageData.setMessage_time(createdAt);
        messageData.setType(viewType);
        messageData.setNameTitle(title);
        messageDataList.add(messageData);
        chatAdapter.notifyDataSetChanged();
        scrollToBottom();
    }

    private void scrollToBottom() {
        recycler.scrollToPosition(chatAdapter.getItemCount() - 1);
    }

    @Override
    public void onResume() {
        super.onResume();
        XuberServicesApplication.setActivity(this);
    }

    public void callGetConversationID()
    {
        new RetrofitService(this, this, URLHelper.GET_CONERSATION_ID+
                "?sender="+userId+"&receiver="+receiverId ,
                1000, 1,"5").callService(true);
    }
    public void callAllConversation(String idd)
    {
        new RetrofitService(this, this, URLHelper.GET_CONVERSATION+
                "?conversationId="+idd ,
                1200, 1,"5").callService(true);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.send:

                if(edt_message.getText().toString().isEmpty())
                {
                    Toast.makeText(this, "Please enter message", Toast.LENGTH_SHORT).show();
                }
                else {
                    attemptSend();
                }

                break;

                case R.id.ivBack:

               finish();

                break;
        }
    }


    @Override
    public void onResponse(int RequestCode, String response)
    {

      switch (RequestCode)
      {
          case 1000:

              Log.e("ResponseIDConversation ",response);
              //{"status":true,"conversationId":"ee4c1751-31e9-4741-afbc-15043be350af"}

              try {
                  JSONObject jsonObject=new JSONObject(response);
                  if (jsonObject.getString("status").equalsIgnoreCase("true"))
                  {
                      conversationId=jsonObject.getString("conversationId");
                      Log.e("conversationId ",conversationId);
                      callAllConversation(conversationId);
                  }
              }catch (Exception ex)
              {
                  ex.printStackTrace();
              }
              break;

          case 1200:

              Log.e("ResponseCHatAll ",response);

              try {
                  JSONObject jsonObject=new JSONObject(response);


                  if (jsonObject.getString("status").equalsIgnoreCase("true"))
                  {
                      JSONArray messages=jsonObject.getJSONArray("messages");


                      if (messages.length()>0)
                      {
                          for (int i = 0; i < messages.length(); i++) {

                              JSONObject jsonObject1=messages.getJSONObject(i);

                              ChatModel chatModel=new ChatModel();

                              String title="";
                              int viewType;

                              chatModel.setId(jsonObject1.getString("id"));
                              chatModel.setConversationId(jsonObject1.getString("conversationId"));
                              chatModel.setMessage(jsonObject1.getString("message"));
                              chatModel.setMessage_time(jsonObject1.getString("createdAt"));

                              if (jsonObject1.getString("sender").equals(userId))
                              {
                                  title="You";
                                  viewType = ListType.SENDER_VIEW_TYPE;
                                  chatModel.setSender(jsonObject1.getString("sender"));

                              }
                              else {
                                  title = username;
                                  viewType = ListType.RECEIVER_VIEW_TYPE;
                                  chatModel.setSender(receiverId);
                              }

                              chatModel.setType(viewType);
                              chatModel.setNameTitle(title);

                              messageDataList.add(chatModel);

                          }

                          if (messageDataList.size()>0)
                          {
                              ChatAdapter chatAdapter=new ChatAdapter(this,messageDataList);
                              recycler.setAdapter(chatAdapter);
                              scrollToBottom();
                          }
                      }
                  }



              }catch (Exception ex)
              {
                  ex.printStackTrace();
              }

              break;
      }

    }
}

