package com.iblog.root.socialapp.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iblog.root.socialapp.R;
import com.iblog.root.socialapp.models.Conversation;
import com.iblog.root.socialapp.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversationsAdapter extends RecyclerView.Adapter<ConversationsAdapter.ViewHolder> {

    private List<Conversation> list;
    private Activity context;
    private DatabaseReference reference;
    private FirebaseAuth auth;
    private String id;

    public ConversationsAdapter(List<Conversation> list, Activity context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ConversationsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = (View) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_conversation, viewGroup, false);
        ConversationsAdapter.ViewHolder vh = new ConversationsAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationsAdapter.ViewHolder viewHolder, int i) {
        Conversation conversation = list.get(i);
        User user = conversation.getUser();
        viewHolder.txtName.setText(user.getName());
        viewHolder.txtMsg.setText(conversation.getLastMsg());
        viewHolder.txtTime.setText(conversation.getDate());

        Picasso.with(context).load(user.getImg()).into(viewHolder.userImg);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public View view;
        public TextView txtName;
        public TextView txtMsg;
        public CircleImageView userImg;
        public TextView txtTime;
        public CardView conversationCard;

        public ViewHolder(View v) {
            super(v);
            view = v;
            txtName = (TextView) view.findViewById(R.id.item_notify_name);
            userImg = (CircleImageView) view.findViewById(R.id.item_notify_img);
            conversationCard = (CardView) view.findViewById(R.id.notify_item_card);

        }
    }
}
