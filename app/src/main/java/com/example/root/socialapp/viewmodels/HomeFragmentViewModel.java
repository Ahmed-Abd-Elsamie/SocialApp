package com.example.root.socialapp.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.os.AsyncTask;

import com.example.root.socialapp.models.Post;
import com.example.root.socialapp.repositories.PostsRepo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    public void likePost(final String postId){
        auth = FirebaseAuth.getInstance();
        final String id = auth.getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("posts").child(postId).child("likes");
        reference.child("actors").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.hasChild(id)){
                    dataSnapshot.child(id).getRef().removeValue();
                }else {
                    setLike(postId, id);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

}
