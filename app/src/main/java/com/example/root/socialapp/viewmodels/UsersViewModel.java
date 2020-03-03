package com.example.root.socialapp.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.os.AsyncTask;
import android.widget.ImageButton;

import com.example.root.socialapp.MyData;
import com.example.root.socialapp.R;
import com.example.root.socialapp.models.User;
import com.example.root.socialapp.repositories.UsersRepo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class UsersViewModel  extends ViewModel {

    private MutableLiveData<List<User>> users;
    private UsersRepo usersRepo;
    private MutableLiveData<Boolean> isUpdating = new MutableLiveData<>();
    private DatabaseReference reference;
    private FirebaseAuth auth;


    public void init(){
        if (users != null){
            return;
        }
        usersRepo = UsersRepo.getInstance();
        users = usersRepo.getUsers(8);
    }

    public void loadMoreUsers(final int num_items){
        isUpdating.setValue(true);

        new AsyncTask<Void, Void, Void>(){
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                users = usersRepo.getUsers(num_items);
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

    public void sendConnectRequest(final User user, final int num_items){
        isUpdating.setValue(true);
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                usersRepo.sendConnectRequest(user);
                users = usersRepo.getUsers(num_items);
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

    public void  checkRequestState(final ImageButton imgAdd, final User user){
        auth = FirebaseAuth.getInstance();
        final String id = auth.getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(id).child("requests").child("mine").hasChild(user.getId())){
                    imgAdd.setBackgroundResource(R.drawable.ic_request_sent);
                    imgAdd.setEnabled(false);
                }
                if (dataSnapshot.child(id).child("requests").child("coming").hasChild(user.getId())){
                    imgAdd.setBackgroundResource(R.drawable.ic_accept_black_24dp);
                    imgAdd.setEnabled(false);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public LiveData<List<User>> getUsers(){
        return users;
    }

    public LiveData<Boolean> getIsUpdating(){
        return isUpdating;
    }

}
