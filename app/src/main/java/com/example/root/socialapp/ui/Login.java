package com.example.root.socialapp.ui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.root.socialapp.MainActivity;
import com.example.root.socialapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity {

    private Button registerBtn ;
    private Button loginBtn ;
    private EditText txtPassword;
    private EditText txtEmail;
    private FirebaseAuth mAuth;
    private ProgressBar pb;
    private String uid;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Assign Views

        txtPassword = (EditText) findViewById(R.id.txt_user_password_login);
        txtEmail = (EditText) findViewById(R.id.txt_user_email_login);
        pb = (ProgressBar) findViewById(R.id.prog_login);
        registerBtn = (Button) findViewById(R.id.registerButton);
        loginBtn = (Button) findViewById(R.id.login_button);

        // init firebase

        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Login.this , Register.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right , R.anim.slide_out_right);
                finish();
            }
        });


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = txtEmail.getText().toString().trim();
                final String pass = txtPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(pass)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                pb.setVisibility(View.VISIBLE);

                mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()){
                            pb.setVisibility(View.INVISIBLE);
                            Toast.makeText(getApplicationContext(), "Failed Please Check your Data!", Toast.LENGTH_SHORT).show();
                        }else{
                            // Handle Notification
                            uid = mAuth.getCurrentUser().getUid();
                            mAuth.getCurrentUser().getIdToken(true).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
                                @Override
                                public void onSuccess(GetTokenResult getTokenResult) {
                                    String tokenId = getTokenResult.getToken();
                                    reference.child("users").child(uid).child("token_id").setValue(tokenId).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            pb.setVisibility(View.INVISIBLE);
                                            startActivity(new Intent(Login.this , MainActivity.class));
                                            finish();
                                        }
                                    });

                                }
                            });

                        }
                    }
                });


            }
        });


    }

}