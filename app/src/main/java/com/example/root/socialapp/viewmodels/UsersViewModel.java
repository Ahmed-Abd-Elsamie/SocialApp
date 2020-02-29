package com.example.root.socialapp.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.os.AsyncTask;
import com.example.root.socialapp.models.User;
import com.example.root.socialapp.repositories.UsersRepo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

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

    public LiveData<List<User>> getUsers(){
        return users;
    }

    public LiveData<Boolean> getIsUpdating(){
        return isUpdating;
    }

}
