package com.example.root.socialapp.repositories;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.example.root.socialapp.models.User;
import com.example.root.socialapp.notification.NotificationHandle;
import com.example.root.socialapp.utils.Chat_Data;
import com.example.root.socialapp.utils.MessageBox;
import com.firebase.client.ChildEventListener;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ChatRepo {

    private Firebase firebase1;
    private Firebase firebase2;
    private FirebaseAuth auth;
    private User user;
    private String id;
    private Activity context;

    public ChatRepo(User user, Activity context){
        this.user = user;
        this.context = context;
        auth = FirebaseAuth.getInstance();
        id = auth.getCurrentUser().getUid();
        Firebase.setAndroidContext(context);
        firebase1 = new Firebase("https://uuuuuuudb.firebaseio.com/messages/" + id + "-" + user.getId());
        firebase2 = new Firebase("https://uuuuuuudb.firebaseio.com/messages/" + user.getId() + "-" + id);
    }
    public void getAllMessages(final LinearLayout layout, final ScrollView scrollView){
        firebase1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(com.firebase.client.DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                String message = map.get("message").toString();
                String user = map.get("user").toString();

                if (user.equals(Chat_Data.id)) {
                    MessageBox.addNewMessage(context,message, 1, layout);
                } else {
                    MessageBox.addNewMessage(context,message, 2, layout);
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

    public void sendMessage(String msg){
        Map<String, String> map = new HashMap<String, String>();
        map.put("message", msg);
        map.put("user", user.getId());
        firebase1.push().setValue(map);
        firebase2.push().setValue(map);
        NotificationHandle.sendNotification(user, msg);
    }

}
