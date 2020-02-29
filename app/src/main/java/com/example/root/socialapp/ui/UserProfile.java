package com.example.root.socialapp.ui;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.root.socialapp.MyData;
import com.example.root.socialapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.onesignal.OneSignal;
import com.squareup.picasso.Picasso;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class UserProfile extends AppCompatActivity {


    private Button btnConnect;
    private DatabaseReference referenceRequest;
    private FirebaseAuth mAuth;
    private String uid;
    private CoordinatorLayout AllFriendsActivity;
    private DatabaseReference referenceFriends;
    private TextView numFriends;
    private Button BtnLogout;
    private int GALARY_REQUEST = 100;
    private Uri imgurl;
    private FloatingActionButton UserProfileImg;
    private FloatingActionButton fabEditSkills;

    private StorageReference storageReference;
    private TextView txtInfoEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);


        // Assign Views

        btnConnect = (Button) findViewById(R.id.btn_connect);
        AllFriendsActivity = (CoordinatorLayout) findViewById(R.id.all_friend);
        numFriends = (TextView) findViewById(R.id.txt_num_friends);
        BtnLogout = (Button) findViewById(R.id.btn_logout);
        txtInfoEmail = (TextView) findViewById(R.id.txt_info_email);
        fabEditSkills = (FloatingActionButton) findViewById(R.id.fab_edit_skills);




        // init Firebase

        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid().toString();
        referenceRequest = FirebaseDatabase.getInstance().getReference().child("users");
        storageReference = FirebaseStorage.getInstance().getReference().child("profile_image");





        AllFriendsActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(UserProfile.this , AllFriends.class));



            }
        });

        BtnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                referenceRequest.child(uid).child("token_id").setValue("").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        mAuth.signOut();
                        OneSignal.sendTag("User_ID", "");

                        startActivity(new Intent(UserProfile.this , Login.class));
                        finish();
                    }
                });

            }
        });



        UserProfileImg = (FloatingActionButton) findViewById(R.id.fab);
        UserProfileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                        */

                ChangeProfileImg();
            }
        });

        Picasso.with(this).load(MyData.userProfileIMG).into(UserProfileImg);

        // Check if this is my profile or not
        if(MyData.UserProfileID.equals(uid)){

            btnConnect.setVisibility(View.GONE);
            txtInfoEmail.setText(MyData.email);

        }else {

            txtInfoEmail.setText(MyData.UserProfilEmail);
            fabEditSkills.setVisibility(View.GONE);

        }

        // Check if Request sent or not
        if (MyData.requestStateSent == true){

            btnConnect.setText("Request Pending...");
            btnConnect.setEnabled(false);

        }



        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Send a Friend Request and add to my Requests

                referenceRequest.child(MyData.UserProfileID).child("myrequests").child(uid).child("id").setValue(uid);

                btnConnect.setText("Request Pending...");
                btnConnect.setEnabled(false);
                sendNotification("New Friend Request");


            }
        });

        fabEditSkills.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showSkillsDialog();

            }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();

        // Getting number Of Friends Label

        referenceRequest.child(MyData.UserProfileID).child("friends").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                numFriends.setText(" ." + dataSnapshot.getChildrenCount() + "");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        GetUserSkills();

    }



    private void sendNotification(final String message) {


        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                int SDK_INT = android.os.Build.VERSION.SDK_INT;
                if (SDK_INT > 8) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);


                    try {
                        String jsonResponse;

                        URL url = new URL("https://onesignal.com/api/v1/notifications");
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setUseCaches(false);
                        con.setDoOutput(true);
                        con.setDoInput(true);

                        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        con.setRequestProperty("Authorization", "Basic YzYzMWYyOWQtMjBiMy00MWRmLThhOGYtYTk3YThmMWVjYjdi");
                        con.setRequestMethod("POST");

                        String strJsonBody = "{"
                                + "\"app_id\": \"f963e69c-86c7-4581-9405-c771569969e5\","

                                + "\"filters\": [{\"field\": \"tag\", \"key\": \"User_ID\", \"relation\": \"=\", \"value\": \"" + MyData.UserProfileID /*id of other */+ "\"}],"

                                + "\"data\": {\"foo\": \"bar\"},"
                                + "\"contents\": {\"en\": " + "\" " + message + "\"}"

                                + "}";


                        System.out.println("strJsonBody:\n" + strJsonBody);

                        byte[] sendBytes = strJsonBody.getBytes("UTF-8");
                        con.setFixedLengthStreamingMode(sendBytes.length);

                        OutputStream outputStream = con.getOutputStream();
                        outputStream.write(sendBytes);

                        int httpResponse = con.getResponseCode();
                        System.out.println("httpResponse: " + httpResponse);

                        if (httpResponse >= HttpURLConnection.HTTP_OK
                                && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                            Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        } else {
                            Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        }
                        System.out.println("jsonResponse:\n" + jsonResponse);

                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }
        });
    }


    private void ChangeProfileImg(){

        Intent i = new Intent();
        i.setAction(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        startActivityForResult(i , GALARY_REQUEST);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == GALARY_REQUEST){
            imgurl = data.getData();
            UserProfileImg.setImageURI(imgurl);

            UploadToDatabase();

        }
    }

    private void UploadToDatabase() {

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Saving...");
        pd.show();
        if (imgurl != null) {

            StorageReference file_path = storageReference.child(imgurl.getLastPathSegment());
            file_path.putFile(imgurl).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String downloadUri = taskSnapshot.getDownloadUrl().toString();
                    referenceRequest.child(uid).child("img").setValue(downloadUri);
                    pd.dismiss();
                }
            });

        }else{

        }


    }

    private void GetUserSkills(){


        DatabaseReference referenceSkills;
        referenceSkills = FirebaseDatabase.getInstance().getReference().child("skills").child(MyData.UserProfileID);
        referenceSkills.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot :dataSnapshot.getChildren()){

                    AddSkillItem(snapshot.child("name").getValue().toString());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void AddSkillItem(String skill) {

        LinearLayout ListOfSkills;
        ListOfSkills = (LinearLayout)findViewById(R.id.skills_layout);


        TextView txtSkill = new TextView(UserProfile.this);



        txtSkill.setText(skill);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 10);

        txtSkill.setTextSize(16);
        lp.gravity = Gravity.LEFT;
        txtSkill.setPadding(10,10,10,10);
        txtSkill.setTextColor(Color.parseColor("#FF6E6E6E"));
        txtSkill.setLayoutParams(lp);


        ListOfSkills.addView(txtSkill);





    }


    private void EditMySkills(){



    }


    private void showSkillsDialog(){

        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.edit_skills_dialog , null);
        Button btnAdd = (Button) view.findViewById(R.id.btn_add_skill);
        final EditText txtSkill = (EditText) view.findViewById(R.id.txt_add_skill);
        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.prog_add_skill);
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating Data...");


        final Dialog dialog = new Dialog(this);
        dialog.setContentView(view);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String skill = txtSkill.getText().toString();

                if (!TextUtils.isEmpty(skill)){

                    LinearLayout ListOfSkills;
                    ListOfSkills = (LinearLayout)findViewById(R.id.skills_layout);
                    ListOfSkills.removeAllViews();

                    progressBar.setVisibility(View.VISIBLE);

                    DatabaseReference referenceSkills;
                    referenceSkills = FirebaseDatabase.getInstance().getReference().child("skills");
                    referenceSkills.child(uid).push().child("name").setValue(skill).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){

                                progressBar.setVisibility(View.GONE);
                                txtSkill.setText("");

                            }

                        }
                    });

                }


            }
        });




    }

}