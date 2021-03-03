package com.example.staysafe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements LocationListener{
    SQLiteDatabase db;
    LocationManager locationManager;
    private FirebaseAuth mAuth;
    EditText editTEmail, editTPass;
    private static final String TAG = "MainActivity";

    public void signUp(View view){
        Intent intent = new Intent(this,SignUpScreen.class);
        startActivity(intent);
    }

    public void logIn(View view) {
        final String email = editTEmail.getText().toString();
        final String password = editTPass.getText().toString();
        //check oti einai simplirwmena kai zitaei focus an den einai
        if (email.isEmpty()) {
            editTEmail.setError("Email is required");
            editTEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            editTPass.setError("Password is required");
            editTPass.requestFocus();
            return;
        }
        //log in to user
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(user.isEmailVerified()) {
                                    Intent intent1= new Intent(MainActivity.this,MessageScreen.class);
                                startActivity(intent1);
                            }else{
                                Intent intent2= new Intent(MainActivity.this,VerifyScreen.class);
                                startActivity(intent2);
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    public void forgetPassword(View view) {
        Intent intent=new Intent(MainActivity.this,ForgotPassword.class);
        startActivity(intent);
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTEmail = findViewById(R.id.editTEmail);
        editTPass = findViewById(R.id.editTPass);
        mAuth = FirebaseAuth.getInstance();
        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

        //dimiourgei ti db kai to table messages
        db = openOrCreateDatabase("MYDB", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS MESSAGE(messages TEXT);");
        Cursor cursor = db.rawQuery("SELECT * FROM MESSAGE",null);
        if (cursor.getCount()==0)
            db.execSQL("INSERT INTO MESSAGE VALUES('1. Φαρμακείο/Γιατρός')," +
                    "('2. Super Market')," +
                    "('3. Δημόσια Υπηρεσία/Τράπεζα')," +
                    "('4. Παροχή βοήθειας/ Συνοδεία ανήλικων μαθητών')," +
                    "('5. Τελετή/Μετάβαση εν διαστάσει γονέων'),('6 Άσκηση/Κίνηση με κατοικίδιο')");
        //checkarei gia perimission kai zitaei an den uparxei
        if (ActivityCompat.
                checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
                    0, this);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 6);
        }
        //checkarei gia perimission kai zitaei an den uparxei
        if (ActivityCompat.
                checkSelfPermission(this, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS}, 6);
        }
    }
}