package com.example.root.socialapp.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.example.root.socialapp.models.Notification_item;
import com.example.root.socialapp.repositories.NotificationsRepo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class NotificationsViewModel extends ViewModel {
    private MutableLiveData<List<Notification_item>> notifications;
    private NotificationsRepo notificationsRepo;
    private MutableLiveData<Boolean> isUpdating = new MutableLiveData<>();
    private DatabaseReference reference;
    private FirebaseAuth auth;

    public void init(){
        if (notifications != null){
            return;
        }
        notificationsRepo = NotificationsRepo.getInstance();
        notifications = notificationsRepo.getNotifications(8);
    }

    public void loadMoreNotifications(final int num_items){
        isUpdating.setValue(true);

        new AsyncTask<Void, Void, Void>(){
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                notifications = notificationsRepo.getNotifications(num_items);
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

    public LiveData<List<Notification_item>> getNotification(){
        return notifications;
    }

    public LiveData<Boolean> getIsUpdating(){
        return isUpdating;
    }

    public void acceptRequest(String uid) {
        isUpdating.setValue(true);
        auth = FirebaseAuth.getInstance();
        String id = auth.getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("users");
        reference.child(id).child("friends").child(uid).child("id").setValue(uid);
        reference.child(uid).child("friends").child(id).child("id").setValue(id);

        // Removing Request
        reference.child(id).child("requests").child("coming").child(uid).removeValue();
        reference.child(uid).child("requests").child("mine").child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                notifications = notificationsRepo.getNotifications(8);
                isUpdating.postValue(false);
            }
        });
    }
}