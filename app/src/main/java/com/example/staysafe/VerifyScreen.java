package com.example.staysafe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class VerifyScreen extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private static final String TAG = "VerifyScreen";

    //stelnei neo email gia verification
    public void verify(View View){
        FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(VerifyScreen.this,"We have sent you a verification email",Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Email sent.");
                            Intent intent=new Intent(VerifyScreen.this,MainActivity.class);
                            startActivity(intent);
                        }
                    }
                });
    }
    public void already(View view){
        Intent intent=new Intent(VerifyScreen.this,MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_screen);
        mAuth = FirebaseAuth.getInstance();
    }
}