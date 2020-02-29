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

import com.example.root.socialapp.MainActivity;
import com.example.root.socialapp.MyData;
import com.example.root.socialapp.R;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class NewPostActivity extends AppCompatActivity {

    private Button btnPost;
    private ImageButton btnPhoto;
    private EditText txtPost;
    private CircleImageView imgUser;
    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    private StorageReference storageReference;
    private int GALARY_REQUEST = 100;
    private Uri imgUri;
    private String uid;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);


        // Assign Views

        btnPost = (Button) findViewById(R.id.btn_new_post);
        btnPhoto = (ImageButton) findViewById(R.id.btn_new_post_img);
        txtPost = (EditText) findViewById(R.id.txt_new_post);
        imgUser = (CircleImageView) findViewById(R.id.new_post_user_img);


        // load user image

        Picasso.with(this).load(MyData.img).into(imgUser);



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

            if (TextUtils.isEmpty(postText) && imgUri == null){
                pd.dismiss();
                return;
            }

            if (TextUtils.isEmpty(postText)){
                postText = " ";
            }
            if (imgUri == null){
                imgUri = Uri.parse("no img");
            }

            StorageReference file_path = storageReference.child(imgUri.getLastPathSegment());
            final String finalPostText = postText;
            file_path.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String downloadUri = taskSnapshot.getDownloadUrl().toString();

                    DatabaseReference r = reference.push();
                    r.child("name").setValue(MyData.name);
                    r.child("userImg").setValue(MyData.img);
                    r.child("desc").setValue(finalPostText);
                    r.child("date").setValue(GetDate());
                    r.child("postImg").setValue(downloadUri);


                    pd.dismiss();

                    startActivity(new Intent(NewPostActivity.this , MainActivity.class));
                    finish();
                }
            });

        }else{
            final ProgressDialog pd1 = new ProgressDialog(this);
            pd.setMessage("Posting...");
            pd1.show();

            String postText = txtPost.getText().toString();

            if (TextUtils.isEmpty(postText) && imgUri == null){
                pd1.dismiss();
                return;
            }

            if (TextUtils.isEmpty(postText)){
                postText = " ";
            }

            final String finalPostText = postText;

                    DatabaseReference r = reference.push();
                    r.child("name").setValue(MyData.name);
                    r.child("userImg").setValue(MyData.img);
                    r.child("desc").setValue(finalPostText);
                    r.child("date").setValue(GetDate());
                    r.child("postImg").setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                pd1.dismiss();
                                startActivity(new Intent(NewPostActivity.this , MainActivity.class));
                                finish();
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
