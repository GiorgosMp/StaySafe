package com.example.staysafe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {
    EditText editTEmail3;
    private FirebaseAuth mAuth;

    public void confirm(View view) {
        final String email=editTEmail3.getText().toString();
        //checkarei an einai keno
        if (email.isEmpty()) {
            editTEmail3.setError("Email is required");
            editTEmail3.requestFocus();
            return;
        }
            //stelnei email gia allagi password
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ForgotPassword.this, "Reset email instructions sent to " + email, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ForgotPassword.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(ForgotPassword.this, email + " does not exist", Toast.LENGTH_LONG).show();

                    }
                }
            });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        editTEmail3 = findViewById(R.id.editTEmail3);
        mAuth = FirebaseAuth.getInstance();
    }
}