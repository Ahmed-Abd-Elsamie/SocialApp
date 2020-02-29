package com.example.root.socialapp.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.root.socialapp.R;
import com.example.root.socialapp.models.Comment;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by root on 25/08/18.
 */

public class CommentsAdapter  extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> implements View.OnClickListener{

    private List<Comment> list;
    private Activity context;

    @Override
    public void onClick(View view) {

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public TextView txtName;
        public TextView txtTitle;
        public CircleImageView userImg;
        public TextView txtComment;
        public TextView txtDate;


        public ViewHolder(View v) {
            super(v);
            view = v;
            txtName = (TextView) view.findViewById(R.id.txt_user_name_comment);
            userImg = (CircleImageView) view.findViewById(R.id.img_user_comment);
            txtDate = (TextView) view.findViewById(R.id.txt_comment_date);
            txtComment = (TextView) view.findViewById(R.id.txt_comment_text);
        }

    }

    public CommentsAdapter(List<Comment> list , Activity context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public CommentsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        CommentsAdapter.ViewHolder vh = new CommentsAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final CommentsAdapter.ViewHolder holder, final int position) {
        Comment comment = list.get(position);
        Picasso.with(context).load(comment.getImg()).into(holder.userImg);
        holder.txtComment.setText(comment.getComment());
        holder.txtDate.setText(comment.getDate());
        holder.txtName.setText(comment.getName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}