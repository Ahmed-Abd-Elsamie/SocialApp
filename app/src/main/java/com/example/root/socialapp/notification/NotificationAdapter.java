package com.example.root.socialapp.notification;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.root.socialapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

        final Notification_item user = list.get(position);


        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid().toString();

        reference = FirebaseDatabase.getInstance().getReference().child("users");

        reference.child(user.getId()).addValueEventListener(new ValueEventListener() {
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

                Toast.makeText(context , user.getId() , Toast.LENGTH_SHORT).show();

                reference = FirebaseDatabase.getInstance().getReference().child("users");
                reference.child(uid).child("friends").child(user.getId()).child("id").setValue(user.getId());
                reference.child(user.getId()).child("friends").child(uid).child("id").setValue(uid);

                // Removing Request
                reference.child(uid).child("myrequests").child(user.getId()).removeValue();


            }
        });



    }

    @Override
    public int getItemCount() {
        return list.size();
    }



}