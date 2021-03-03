package com.example.staysafe;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUpScreen extends AppCompatActivity {
    EditText editTEmail2, editTPass2, editTRPass;
    private FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference myRef;
    private String userId;
    private static final String TAG = "SignUpScreen";

    public void signUp2(View view) {
        final String email = editTEmail2.getText().toString();
        final String password = editTPass2.getText().toString();
        final String rpassword = editTRPass.getText().toString();
        //elegxei an einai kapio koutaki keno kai antistoixa to kanei focus
        if (email.isEmpty()) {
            editTEmail2.setError("Email is required");
            editTEmail2.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            editTPass2.setError("Password is required");
            editTPass2.requestFocus();
            return;
        }
        if (rpassword.isEmpty()) {
            editTRPass.setError("Reapeat Password is required");
            editTRPass.requestFocus();
            return;
        }
        if (!password.equals(rpassword)) {
            Toast.makeText(SignUpScreen.this, "The password is not the same", Toast.LENGTH_SHORT).show();
        }
        //dimiourgei to user
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                          if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            user.sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "Email sent.");
                                            }
                                        }
                                    });
                            Toast.makeText(SignUpScreen.this, "You have signed up successfully.",
                                      Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpScreen.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_screen);
        editTEmail2 = findViewById(R.id.editTEmail2);
        editTPass2 = findViewById(R.id.editTPass2);
        editTRPass = findViewById(R.id.editTRPass);
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference("users");
    }
}