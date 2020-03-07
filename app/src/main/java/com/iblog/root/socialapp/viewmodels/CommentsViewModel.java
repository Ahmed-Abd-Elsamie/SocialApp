package com.iblog.root.socialapp.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.os.AsyncTask;

import com.iblog.root.socialapp.models.Comment;
import com.iblog.root.socialapp.repositories.CommentsRepo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class CommentsViewModel extends ViewModel {

    private MutableLiveData<List<Comment>> comments;
    private CommentsRepo commentsRepo;
    private MutableLiveData<Boolean> isUpdating = new MutableLiveData<>();
    private DatabaseReference reference;
    private FirebaseAuth auth;


    public void init(String postId){
        if (comments != null){
            return;
        }
        commentsRepo = CommentsRepo.getInstance();
        comments = commentsRepo.getComments(4, postId);
    }

    public void loadMoreComments(final int num_items, final String postId){
        isUpdating.setValue(true);

        new AsyncTask<Void, Void, Void>(){
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                comments = commentsRepo.getComments(num_items, postId);
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

    public void NewComment(final int num_items, final Comment comment, final String postId){
        isUpdating.setValue(true);
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                commentsRepo.postNewComment(postId, comment);
                comments = commentsRepo.getComments(num_items, postId);
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

    public LiveData<List<Comment>> getComments(){
        return comments;
    }

    public LiveData<Boolean> getIsUpdating(){
        return isUpdating;
    }

}
