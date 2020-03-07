package com.iblog.root.socialapp.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.os.AsyncTask;
import android.widget.Button;

import com.iblog.root.socialapp.R;
import com.iblog.root.socialapp.models.Post;
import com.iblog.root.socialapp.repositories.PostsRepo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class HomeFragmentViewModel extends ViewModel {

    private MutableLiveData<List<Post>> posts;
    private PostsRepo postsRepo;
    private MutableLiveData<Boolean> isUpdating = new MutableLiveData<>();
    private DatabaseReference reference;
    private FirebaseAuth auth;


    public void init(){
        if (posts != null){
            return;
        }
        postsRepo = PostsRepo.getInstance();
        posts = postsRepo.getPosts(4);
    }

    public void loadMorePosts(final int num_items){
        isUpdating.setValue(true);

        new AsyncTask<Void, Void, Void>(){
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                posts = postsRepo.getPosts(num_items);
                isUpdating.postValue(false);
            }


            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();

    }

    public void setPostLike(final Post post, final int num_items){
        isUpdating.setValue(true);
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                postsRepo.sendPostLike(post);
                posts = postsRepo.getPosts(num_items);
                isUpdating.postValue(false);
            }

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    public void setPostDisLike(final Post post, final int num_items){
        isUpdating.setValue(true);
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                postsRepo.sendPostDisLike(post);
                posts = postsRepo.getPosts(num_items);
                isUpdating.postValue(false);
            }

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    public void likePost(final String postId, final Button btnLike){
        auth = FirebaseAuth.getInstance();
        final String id = auth.getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("posts").child(postId).child("likes");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("actors").hasChild(id)){
                    //reference.child("actors").child(id).getRef().removeValue();
                    //decrementLikes(dataSnapshot.child("num").getValue().toString(), postId);
                    btnLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like,0,0,0);
                }else {
                    setLike(postId, id);
                    btnLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_pressed,0,0,0);
                    //incrementLikes(dataSnapshot.child("num").getValue().toString(), postId);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void decrementLikes(String likes, String postId) {
        int num = Integer.parseInt(likes) - 1;
        reference = FirebaseDatabase.getInstance().getReference().child("posts").child(postId).child("likes");
        reference.child("num").setValue(num + "");
    }

    private void incrementLikes(String likes, String postId) {
        int num = Integer.parseInt(likes) + 1;
        reference = FirebaseDatabase.getInstance().getReference().child("posts").child(postId).child("likes");
        reference.child("num").setValue(num + "");
    }

    private void setLike(String postId, String id) {
        reference = FirebaseDatabase.getInstance().getReference().child("posts").child(postId).child("likes");
        reference.child("actors").child(id).setValue("like");
    }


    public LiveData<List<Post>> getPosts(){
        return posts;
    }

    public LiveData<Boolean> getIsUpdating(){
        return isUpdating;
    }

    public void checkLikes(Post post, Button likeBtn) {
        likeBtn.setText(post.getLikes_num());
        if (post.isLiked()){
            likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_pressed,0,0,0);
        }else {
            likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like,0,0,0);
        }
    }

}