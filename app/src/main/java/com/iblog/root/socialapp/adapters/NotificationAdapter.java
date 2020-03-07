package com.iblog.root.socialapp.adapters;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.iblog.root.socialapp.R;
import com.iblog.root.socialapp.models.Notification_item;
import com.iblog.root.socialapp.viewmodels.NotificationsViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by root on 17/08/18.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>{


    private List<Notification_item> list;
    private Activity context;
    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    private String uid;
    private NotificationsViewModel notificationsViewModel;



    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View view;
        public TextView txtName;
        public TextView txtTitle;
        public CircleImageView userImg;
        public Button btnAccept;
        public CardView notifyCard;


        public ViewHolder(View v) {
            super(v);
            view = v;
            txtName = (TextView) view.findViewById(R.id.item_notify_name);
            userImg = (CircleImageView) view.findViewById(R.id.item_notify_img);
            btnAccept = (Button) view.findViewById(R.id.btn_accept_friend);
            notifyCard = (CardView) view.findViewById(R.id.notify_item_card);


        }

    }

    public NotificationAdapter(List<Notification_item> notificationItemList , Activity mContext) {
        list = notificationItemList;
        context = mContext;
    }

    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        NotificationAdapter.ViewHolder vh = new NotificationAdapter.ViewHolder(v);
        return vh;
    }




    @Override
    public void onBindViewHolder(final NotificationAdapter.ViewHolder holder, final int position) {
        final Notification_item notification_item = list.get(position);
        notificationsViewModel = ViewModelProviders.of((FragmentActivity) context).get(NotificationsViewModel.class);
        notificationsViewModel.init();
        holder.txtName.setText(notification_item.getName());
        Picasso.with(context).load(notification_item.getImg()).into(holder.userImg);


        holder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationsViewModel.acceptRequest(notification_item.getId());
            }
        });
        /*mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();

        reference = FirebaseDatabase.getInstance().getReference().child("users");

        reference.child(notification_item.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                holder.txtName.setText(dataSnapshot.child("name").getValue().toString());
                Picasso.with(context).load(dataSnapshot.child("img").getValue().toString()).into(holder.userImg);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        holder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(context , notification_item.getId() , Toast.LENGTH_SHORT).show();

                reference = FirebaseDatabase.getInstance().getReference().child("users");
                reference.child(uid).child("friends").child(notification_item.getId()).child("id").setValue(notification_item.getId());
                reference.child(notification_item.getId()).child("friends").child(uid).child("id").setValue(uid);

                // Removing Request
                reference.child(uid).child("myrequests").child(notification_item.getId()).removeValue();


            }
        });

*/

    }

    @Override
    public int getItemCount() {
        return list.size();
    }



}