package com.example.root.socialapp.repositories;

import com.example.root.socialapp.models.Comment;
import com.example.root.socialapp.models.Conversation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class ConversationsRepo {

    private static ConversationsRepo instance;
    private List<Conversation> conversations = new ArrayList<>();
    private DatabaseReference reference;
    private FirebaseAuth auth;


    public static ConversationsRepo getInstance(){
        if (instance == null) {
            instance = new ConversationsRepo();
        }
        return instance;
    }


    public void getLastConversations(){
        
    }
}
