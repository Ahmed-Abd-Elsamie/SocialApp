package com.example.root.socialapp.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.root.socialapp.MainActivity;
import com.example.root.socialapp.MyData;
import com.example.root.socialapp.R;
import com.example.root.socialapp.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewPostActivity extends AppCompatActivity {

    private Button btnPost;
    private ImageButton btnPhoto, btnVideo;
    private EditText txtPost;
    private CircleImageView imgUser;
    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    private StorageReference storageReference;
    private int GALARY_REQUEST = 100;
    private Uri imgUri;
    private String uid;
    private User myData;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        Intent intent = getIntent();
        myData = intent.getParcelableExtra("user");

        // Assign Views
        btnPost = (Button) findViewById(R.id.btn_new_post);
        btnPhoto = (ImageButton) findViewById(R.id.btn_new_post_img);
        btnVideo = (ImageButton) findViewById(R.id.btn_new_post_video);
        txtPost = (EditText) findViewById(R.id.txt_new_post);
        imgUser = (CircleImageView) findViewById(R.id.new_post_user_img);

        // load user image
        Picasso.with(this).load(myData.getImg()).into(imgUser);

        // init firebase
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid().toString();
        reference = FirebaseDatabase.getInstance().getReference().child("posts");
        storageReference = FirebaseStorage.getInstance().getReference().child("posts_imgs");

        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowDialog();
            }
        });

        btnVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(NewPostActivity.this, "SOON !", Toast.LENGTH_SHORT).show();
            }
        });

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadToDatabase();
            }
        });

    }

    public void ShowDialog(){
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.alert_dialog_lay,null);

        Button btnCamera = (Button) view.findViewById(R.id.camera_img);
        Button btnGallery = (Button) view.findViewById(R.id.gallery_img);

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(NewPostActivity.this, "SOON !", Toast.LENGTH_SHORT).show();
            }
        });

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setAction(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                startActivityForResult(i , GALARY_REQUEST);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("")
                .setView(view)
                .show();

    }


    private void UploadToDatabase() {

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Posting...");
        pd.show();
        if (imgUri != null) {
            String postText = txtPost.getText().toString();
            if (TextUtils.isEmpty(postText)){
                postText = " ";
            }

            StorageReference file_path = storageReference.child(imgUri.getLastPathSegment());
            final String finalPostText = postText;
            file_path.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String downloadUri = taskSnapshot.getDownloadUrl().toString();
                    Map map = new HashMap();
                    map.put("uid", myData.getId());
                    map.put("date", GetDate());
                    map.put("desc", finalPostText);
                    map.put("likes", "");
                    map.put("name", myData.getName());
                    map.put("postImg", downloadUri);
                    map.put("userImg", myData.getImg());
                    final DatabaseReference r = reference.push();
                    r.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            pd.dismiss();
                            if (task.isSuccessful()){
                                reference.child(r.getKey()).child("likes").child("num").setValue("0").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        startActivity(new Intent(NewPostActivity.this , MainActivity.class));
                                        finish();
                                    }
                                });
                            }else {
                                Toast.makeText(NewPostActivity.this, "Failed to Post !", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            });

        }else{
            String postText = txtPost.getText().toString();
            if (TextUtils.isEmpty(postText)){
                pd.dismiss();
                return;
            }

            final String finalPostText = postText;
            Map map = new HashMap();
            map.put("uid", myData.getId());
            map.put("date", GetDate());
            map.put("desc", finalPostText);
            map.put("likes", "");
            map.put("name", myData.getName());
            map.put("postImg", "default");
            map.put("userImg", myData.getImg());
            final DatabaseReference r = reference.push();
            r.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    pd.dismiss();
                    if (task.isSuccessful()){
                        reference.child(r.getKey()).child("likes").child("num").setValue("0").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                startActivity(new Intent(NewPostActivity.this , MainActivity.class));
                                finish();
                            }
                        });
                    }else {
                        Toast.makeText(NewPostActivity.this, "Failed to Post !", Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == GALARY_REQUEST){
            imgUri = data.getData();

            LinearLayout l = (LinearLayout) findViewById(R.id.attachments_layout_post);

            ImageView imageView = new ImageView(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(10, 10, 10, 10);
            imageView.setImageURI(imgUri);
            lp.gravity = Gravity.CENTER;
            imageView.setLayoutParams(lp);

            l.addView(imageView);


        }
    }


    private String GetDate(){

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DATE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);

        String full_date = day + "/" + month + "/" + year + "   at " + hour + ":" + minutes;




        return full_date;


    }

}
