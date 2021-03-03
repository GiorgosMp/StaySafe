package com.example.staysafe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Timestamp;
import java.util.ArrayList;

public class MessageScreen extends AppCompatActivity implements LocationListener, AdapterView.OnItemSelectedListener {
    SQLiteDatabase db;
    View view;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    EditText editTextName, editTextAd;
    SharedPreferences preferences;
    private Spinner spinner;
    private String userId;
    String choice, s, s2;
    private static final int REC_RESULT = 653;
    private static final String[] paths = {"1", "2", "3", "4", "5", "6"};

    //gia to spinner
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (i) {
            case 0:
                choice = "1";
                break;
            case 1:
                choice = "2";
                break;
            case 2:
                choice = "3";
                break;
            case 3:
                choice = "4";
                break;
            case 4:
                choice = "5";
                break;
            case 5:
                choice = "6";
                break;
        }
    }

    //shared preferences gia to onoma
    public void NameOnClick(View view) {
        SharedPreferences.Editor editor = preferences.edit();
        if (editTextName.getText().toString().trim().length() == 0 || editTextName.getText().toString().matches("0")) {
            Toast.makeText(MessageScreen.this, "Please insert somethin!=0", Toast.LENGTH_LONG).show();
        } else {
            editor.putString("myvalue", editTextName.getText().toString());
            editor.apply();
            Toast.makeText(this, "Data saved!", Toast.LENGTH_LONG).show();
            s = preferences.getString("myvalue", "Write a Name");
            editTextName.setText(s, TextView.BufferType.EDITABLE);
        }
    }

    //shared preferences gia ti dieuthinsi
    public void AddressOnClick(View view) {
        SharedPreferences.Editor editor = preferences.edit();
        if (editTextAd.getText().toString().trim().length() == 0 || editTextAd.getText().toString().matches("0")) {
            Toast.makeText(MessageScreen.this, "Please insert somethin!=0", Toast.LENGTH_LONG).show();
        } else {
            editor.putString("myvalue2", editTextAd.getText().toString());
            editor.apply();
            Toast.makeText(this, "Data saved!", Toast.LENGTH_LONG).show();
            s2 = preferences.getString("myvalue2", "Write an Address");
            editTextAd.setText(s2, TextView.BufferType.EDITABLE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void sendSMS(View view) {
        //gia to sms
        try {
            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.setType("vnd.android-dir/mms-sms");
            sendIntent.putExtra("address", new String("13033"));
            sendIntent.putExtra("sms_body", choice + " " + " " + s + " " + s2);
            startActivity(Intent.createChooser(sendIntent, "Send sms via:"));
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 0, (LocationListener) MessageScreen.this);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(bestProvider);
        //gia na apothikeuei sto realtime database eite tin topothesia tou eite "null" sto katalilo timestamp gia ton katalilo user
        if (location == null) {
            Toast.makeText(getApplicationContext(), "GPS signal not found", Toast.LENGTH_SHORT).show();
            userId = mAuth.getUid();
            databaseReference = firebaseDatabase.getReference(userId);
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            databaseReference.child(String.valueOf(timestamp)).setValue("null");
        }
        if (location != null) {
            userId = mAuth.getUid();
            databaseReference = firebaseDatabase.getReference(userId);
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            databaseReference.child(String.valueOf(timestamp)).setValue(String.valueOf(location.getLongitude())+" "+String.valueOf(location.getLatitude()));
        }
        }
        catch(Exception e){
            Toast.makeText(MessageScreen.this, "SMS Failed to Send, Please try again", Toast.LENGTH_SHORT).show();
        }
    }

    //gia tin anagnwrisi tis fwnis
    public void recognize(View view){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Please say something!");
        startActivityForResult(intent,REC_RESULT);
    }

    //fwnitiki entoli
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REC_RESULT && resultCode==RESULT_OK){
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            //an anagnwrisei ti leksi 1
            if (matches.contains("1")) {
                choice = "1";
                //stelnei to sms
                sendSMS(view);
                //vazei sto spinner tin epilogi 0
                spinner.setSelection(0);
            }
            //an anagnwrisei ti leksi 2
            if (matches.contains("2")) {
                choice = "2";
                sendSMS(view);
                spinner.setSelection(1);
            }
            //an anagnwrisei ti leksi 3
            if (matches.contains("3")) {
                choice = "3";
                sendSMS(view);
                spinner.setSelection(2);
            }
            //an anagnwrisei ti leksi 4
            if (matches.contains("4")) {
                choice = "4";
                sendSMS(view);
                spinner.setSelection(3);
            }
            //an anagnwrisei ti leksi 5
            if (matches.contains("5")) {
                choice = "5";
                sendSMS(view);
                spinner.setSelection(4);
            }
            //an anagnwrisei ti leksi 6
            if (matches.contains("6")) {
                choice = "6";
                sendSMS(view);
                spinner.setSelection(5);
            }
        }

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
        setContentView(R.layout.activity_message_screen);
        editTextName =findViewById(R.id.editTextName);
        editTextAd =findViewById(R.id.editTextAd);

        firebaseDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        s = preferences.getString("myvalue", "Write a Name");
        s2 = preferences.getString("myvalue2", "Write an Address");
        editTextName.setText(s,TextView.BufferType.EDITABLE);
        editTextAd.setText(s2,TextView.BufferType.EDITABLE);

        db = openOrCreateDatabase("MYDB", Context.MODE_PRIVATE, null);

        //gia to spinner
        spinner = (Spinner)findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MessageScreen.this,
                android.R.layout.simple_spinner_item,paths);

        Cursor cursor = db.rawQuery("SELECT * FROM MESSAGE",null);
        int i=0;
        while (cursor.moveToNext()) {
            paths[i]=cursor.getString(0);
            i++;
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);
    }
}