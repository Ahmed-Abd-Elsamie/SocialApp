package com.iblog.root.socialapp.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.iblog.root.socialapp.R;
import com.iblog.root.socialapp.models.Post;
import com.iblog.root.socialapp.ui.CommentsActivity;
import com.iblog.root.socialapp.viewmodels.HomeFragmentViewModel;
import com.squareup.picasso.Picasso;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by root on 26/07/18.
 */


public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private List<Post> list;
    private Activity context;
    private HomeFragmentViewModel homeFragmentViewModel;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public TextView txtName;
        public TextView txtDate;
        public TextView txtTitle;
        public TextView txtDesc;
        public TextView txtComment;
        public TextView txtMore;
        public CircleImageView userImg;
        public ImageView PostImg;
        public Button LikeBtn;
        public Button CommentBtn;
        public Button ShareBtn;
        public LinearLayout separate;
        public LinearLayout container;
        public CardView cardView;
        public LinearLayout Commentlayout;


        public ViewHolder(View v) {
            super(v);
            view = v;

            txtName = (TextView) view.findViewById(R.id.user_name);
            txtDate = (TextView) view.findViewById(R.id.post_date);
            txtDesc = (TextView) view.findViewById(R.id.post_desc);
            txtTitle = (TextView) view.findViewById(R.id.user_title);
            txtMore = view.findViewById(R.id.txt_more);
            txtComment = (TextView) view.findViewById(R.id.post_likes_comments);
            userImg = (CircleImageView) view.findViewById(R.id.user_img);
            PostImg = (ImageView) view.findViewById(R.id.post_img);
            LikeBtn = (Button) view.findViewById(R.id.btn_like);
            CommentBtn = (Button) view.findViewById(R.id.btn_comment);
            ShareBtn = (Button) view.findViewById(R.id.btn_share);
            separate = (LinearLayout) view.findViewById(R.id.separator);
            container = (LinearLayout) view.findViewById(R.id.item_container);
            cardView = (CardView) view.findViewById(R.id.card);
            Commentlayout = (LinearLayout) view.findViewById(R.id.comment_layout);
        }

    }

    public PostAdapter(List<Post> list , Activity context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        PostAdapter.ViewHolder vh = new PostAdapter.ViewHolder(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(final PostAdapter.ViewHolder holder, final int position) {
        final Post post = list.get(position);
        holder.txtName.setText(post.getUser_name());
        holder.txtDate.setText(post.getDate());
        holder.txtTitle.setText("default");

        Picasso.with(context).load(post.getUser_img()).into(holder.userImg);
        Picasso.with(context).load(post.getPost_img()).into(holder.PostImg);

        homeFragmentViewModel = ViewModelProviders.of((FragmentActivity) context).get(HomeFragmentViewModel.class);
        homeFragmentViewModel.init();
        homeFragmentViewModel.checkLikes(post, holder.LikeBtn);

        if (post.getPost_desc().length() > 150){
            holder.txtDesc.setText(post.getPost_desc().substring(0, 130));
        }else {
            holder.txtDesc.setText(post.getPost_desc());
        }

        holder.PostImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previewPostImage(post.getPost_img());
            }
        });

        holder.LikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (post.isLiked()){
                    homeFragmentViewModel.setPostDisLike(post, 4);
                    holder.LikeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like,0,0,0);
                    holder.LikeBtn.setText(Integer.parseInt(holder.LikeBtn.getText().toString()) - 1 + "");
                    post.setLiked(false);
                    post.setLikes_num(Integer.parseInt(holder.LikeBtn.getText().toString()) + "");
                }else {
                    homeFragmentViewModel.setPostLike(post, 4);
                    holder.LikeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_pressed,0,0,0);
                    holder.LikeBtn.setText(Integer.parseInt(holder.LikeBtn.getText().toString()) + 1 + "");
                    post.setLiked(true);
                    post.setLikes_num(Integer.parseInt(holder.LikeBtn.getText().toString()) + "");
                }

            }
        });

        holder.CommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CommentsActivity.class);
                intent.putExtra("post", post);
                context.startActivity(intent);
            }
        });

        holder.txtMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CommentsActivity.class);
                intent.putExtra("post", post);
                context.startActivity(intent);
            }
        });

        holder.ShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context , holder.ShareBtn);
                popupMenu.getMenuInflater().inflate(R.menu.share_menu , popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        Toast.makeText(context, "" + menuItem.getTitle(), Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

    }

    private void previewPostImage(String img_url) {
        Dialog dialog = new Dialog(context);
        LayoutInflater inflater = context.getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_img , null);
        ImageView img = (ImageView) v.findViewById(R.id.img_view);
        dialog.setContentView(v);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        Picasso.with(context).load(img_url).into(img);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}