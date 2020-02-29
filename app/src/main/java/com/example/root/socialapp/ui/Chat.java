package com.example.root.socialapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.root.socialapp.R;
import com.example.root.socialapp.utils.Chat_Data;
import com.firebase.client.ChildEventListener;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import de.hdodenhof.circleimageview.CircleImageView;

public class Chat extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Firebase databaseReference1;
    private Firebase databaseReference2;
    private String uid;
    private Button btnSend;
    private EditText txtmessage;
    private LinearLayout layout;
    private ScrollView scrollView;
    private CircleImageView ChatUserImg;
    private TextView txtChatUserName;
    private ImageButton btnSendImg;
    private ImageButton btnSendVideo;
    private ImageButton btnSendFile;
    private int GALARY_REQUEST = 100;
    private Uri imgurl;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);



        Firebase.setAndroidContext(this);

        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid().toString();

        databaseReference1 = new Firebase("https://uuuuuuudb.firebaseio.com/messages/" + uid + "-" + Chat_Data.id);
        databaseReference2 = new Firebase("https://uuuuuuudb.firebaseio.com/messages/" + Chat_Data.id + "-" + uid);


        // Assign Views
        btnSend = (Button)findViewById(R.id.btn_send);
        txtmessage = (EditText)findViewById(R.id.txt_message);
        layout = (LinearLayout)findViewById(R.id.layout_message);
        scrollView = (ScrollView)findViewById(R.id.scroll);
        txtChatUserName = (TextView)findViewById(R.id.chat_user_name);
        ChatUserImg = (CircleImageView)findViewById(R.id.chat_user_img);
        btnSendImg = (ImageButton)findViewById(R.id.send_img);
        btnSendVideo = (ImageButton)findViewById(R.id.send_video);
        btnSendFile = (ImageButton)findViewById(R.id.send_file);



        // chat with user Data
        Picasso.with(this).load(Chat_Data.img).into(ChatUserImg);
        txtChatUserName.setText(Chat_Data.name);



        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = txtmessage.getText().toString();
                if(!messageText.equals("")){
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("message", messageText);
                    map.put("user", Chat_Data.id);
                    databaseReference1.push().setValue(map);
                    databaseReference2.push().setValue(map);

                    sendNotification(messageText);

                }
                txtmessage.setText("");
            }


        });


        btnSendImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ShowDialog();

            }
        });

        // getting messages

        databaseReference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(com.firebase.client.DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                String message = map.get("message").toString();
                String user = map.get("user").toString();

                if(user.equals(Chat_Data.id)){
                    addNewMessage(message, 1);
                }
                else{
                    addNewMessage(message, 2);
                }
                scrollView.fullScroll(View.FOCUS_DOWN);

            }

            @Override
            public void onChildChanged(com.firebase.client.DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(com.firebase.client.DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(com.firebase.client.DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void addNewMessage(String message, int type){
        TextView textView = new TextView(Chat.this);
        textView.setText(message);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 10);
        textView.setTextSize(18);
        textView.setLayoutParams(lp);
        textView.setPadding(8,8,8,8);


        if(type == 1) {

            textView.setBackgroundResource(R.drawable.message_reciever);
            lp.gravity = Gravity.RIGHT;
            textView.setTextColor(Color.parseColor("#000000"));

        }
        else{

            textView.setBackgroundResource(R.drawable.message_sender);
            lp.gravity = Gravity.LEFT;
            textView.setTextColor(Color.parseColor("#FFFFFF"));

        }

        layout.addView(textView);

    }



    public void ShowDialog(){
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.alert_dialog_lay,null);

        Button btnCamera = (Button) view.findViewById(R.id.camera_img);
        Button btnGallery = (Button) view.findViewById(R.id.gallery_img);

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent();
                i.setAction(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                startActivityForResult(i , GALARY_REQUEST);

            }
        });


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("")
                .setView(view)
                .show();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == GALARY_REQUEST){
            imgurl = data.getData();

            LinearLayout l = (LinearLayout) findViewById(R.id.attachments_layout);

            ImageView imageView = new ImageView(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(10, 10, 10, 10);
            imageView.setImageURI(imgurl);
            lp.gravity = Gravity.CENTER;
            imageView.setLayoutParams(lp);

            l.addView(imageView);


            //UploadToDatabase();

        }
    }








    private void sendNotification(final String message) {


        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                int SDK_INT = android.os.Build.VERSION.SDK_INT;
                if (SDK_INT > 8) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);


                    try {
                        String jsonResponse;

                        URL url = new URL("https://onesignal.com/api/v1/notifications");
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setUseCaches(false);
                        con.setDoOutput(true);
                        con.setDoInput(true);

                        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        con.setRequestProperty("Authorization", "Basic YzYzMWYyOWQtMjBiMy00MWRmLThhOGYtYTk3YThmMWVjYjdi");
                        con.setRequestMethod("POST");

                        String strJsonBody = "{"
                                + "\"app_id\": \"f963e69c-86c7-4581-9405-c771569969e5\","

                                + "\"filters\": [{\"field\": \"tag\", \"key\": \"User_ID\", \"relation\": \"=\", \"value\": \"" + Chat_Data.id /*id of other */+ "\"}],"

                                + "\"data\": {\"foo\": \"bar\"},"
                                + "\"contents\": {\"en\": " + "\" " + message + "\"}"

                                + "}";


                        System.out.println("strJsonBody:\n" + strJsonBody);

                        byte[] sendBytes = strJsonBody.getBytes("UTF-8");
                        con.setFixedLengthStreamingMode(sendBytes.length);

                        OutputStream outputStream = con.getOutputStream();
                        outputStream.write(sendBytes);

                        int httpResponse = con.getResponseCode();
                        System.out.println("httpResponse: " + httpResponse);

                        if (httpResponse >= HttpURLConnection.HTTP_OK
                                && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                            Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        } else {
                            Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        }
                        System.out.println("jsonResponse:\n" + jsonResponse);

                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }
        });
    }


}
