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
import com.example.root.socialapp.models.User;
import com.example.root.socialapp.repositories.ChatRepo;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Chat extends AppCompatActivity {

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
        Intent intent = getIntent();
        final User user = intent.getParcelableExtra("user");
        final ChatRepo chatRepo = new ChatRepo(user, Chat.this);
        // init views
        initViews();
        // chat with user Data
        Picasso.with(this).load(user.getImg()).into(ChatUserImg);
        txtChatUserName.setText(user.getName());

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = txtmessage.getText().toString();
                if (!messageText.equals("")) {
                    chatRepo.sendMessage(messageText);
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
        chatRepo.getAllMessages(layout, scrollView);
    }

    private void initViews() {
        btnSend = (Button) findViewById(R.id.btn_send);
        txtmessage = (EditText) findViewById(R.id.txt_message);
        layout = (LinearLayout) findViewById(R.id.layout_message);
        scrollView = (ScrollView) findViewById(R.id.scroll);
        txtChatUserName = (TextView) findViewById(R.id.chat_user_name);
        ChatUserImg = (CircleImageView) findViewById(R.id.chat_user_img);
        btnSendImg = (ImageButton) findViewById(R.id.send_img);
        btnSendVideo = (ImageButton) findViewById(R.id.send_video);
        btnSendFile = (ImageButton) findViewById(R.id.send_file);
    }

    public void ShowDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.alert_dialog_lay, null);

        Button btnCamera = (Button) view.findViewById(R.id.camera_img);
        Button btnGallery = (Button) view.findViewById(R.id.gallery_img);

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setAction(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                startActivityForResult(i, GALARY_REQUEST);
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
        if (resultCode == Activity.RESULT_OK && requestCode == GALARY_REQUEST) {
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

}