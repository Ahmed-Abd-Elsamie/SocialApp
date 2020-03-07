package com.example.root.socialapp.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.example.root.socialapp.R;
import com.example.root.socialapp.ui.Chat;
import com.example.root.socialapp.ui.UserProfile;
import com.example.root.socialapp.utils.Chat_Data;
import com.example.root.socialapp.models.User;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by root on 18/08/18.
 */

public class FriendsAdapter extends UsersAdapter {
    private Activity context;

    public FriendsAdapter(List<User> userList, Activity mContext) {
        super(userList, mContext);
        context = mContext;
    }

    @Override
    public void onBindViewHolder(final UsersAdapter.ViewHolder holder, final int position) {
        final User user = list.get(position);
        holder.txtName.setText(user.getName());

        Picasso.with(context).load(user.getImg()).into(holder.userImg);
        holder.imgAdd.setEnabled(true);
        holder.imgAdd.setClickable(true);
        holder.imgAdd.setBackgroundResource(R.drawable.ic_message);
        holder.imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Chat.class);
                intent.putExtra("user", user);
                context.startActivity(intent);
            }
        });

        holder.userCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserProfile.class);
                intent.putExtra("user", user);
                intent.putExtra("friend", true);
                context.startActivity(intent);
            }
        });
    }
}