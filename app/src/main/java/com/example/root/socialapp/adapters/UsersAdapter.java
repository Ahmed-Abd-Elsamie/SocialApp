package com.example.root.socialapp.adapters;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.root.socialapp.MyData;
import com.example.root.socialapp.R;
import com.example.root.socialapp.models.User;
import com.example.root.socialapp.notification.NotificationHandle;
import com.example.root.socialapp.ui.UserProfile;
import com.example.root.socialapp.viewmodels.UsersViewModel;
import com.squareup.picasso.Picasso;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by root on 16/08/18.
 */

public class UsersAdapter  extends RecyclerView.Adapter<UsersAdapter.ViewHolder> implements View.OnClickListener{

    public List<User> list;
    private Activity context;
    private UsersViewModel usersViewModel;

    public UsersAdapter() {

    }

    @Override
    public void onClick(View view) {

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View view;
        public TextView txtName;
        public TextView txtTitle;
        public CircleImageView userImg;
        public ImageButton imgAdd;
        public CardView userCard;

        public ViewHolder(View v) {
            super(v);
            view = v;
            txtName = (TextView) view.findViewById(R.id.item_user_name);
            userImg = (CircleImageView) view.findViewById(R.id.item_user_img);
            imgAdd = (ImageButton) view.findViewById(R.id.btn_add_friend);
            userCard = (CardView) view.findViewById(R.id.user_item_card);

        }

    }

    public UsersAdapter(List<User> userList , Activity mContext) {
        list = userList;
        context = mContext;
    }

    @Override
    public UsersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        UsersAdapter.ViewHolder vh = new UsersAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final UsersAdapter.ViewHolder holder, final int position) {
        final User user = list.get(position);
        holder.txtName.setText(user.getName());
        Picasso.with(context).load(user.getImg()).into(holder.userImg);

        usersViewModel = ViewModelProviders.of((FragmentActivity) context).get(UsersViewModel.class);
        usersViewModel.init();
        // Check request State
        usersViewModel.checkRequestState(holder.imgAdd, user);

        holder.imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usersViewModel.sendConnectRequest(user, 8);
                NotificationHandle.sendNotification(user, "New Friend Request");
            }
        });

        holder.userCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserProfile.class);
                intent.putExtra("user", user);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
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

                                + "\"filters\": [{\"field\": \"tag\", \"key\": \"User_ID\", \"relation\": \"=\", \"value\": \"" + MyData.UserProfileID /*id of other */+ "\"}],"

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

