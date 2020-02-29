package com.example.root.socialapp.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.example.root.socialapp.R;
import com.example.root.socialapp.ui.Chat;
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
        holder.imgAdd.setBackgroundResource(R.drawable.ic_message);

        holder.imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Chat_Data.id = user.getId();
                Chat_Data.name = user.getName();
                Chat_Data.img = user.getImg();
                context.startActivity(new Intent(context,Chat.class));


            }
        });


    }


}
