package com.hj.locationsos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TextView txt_Number;
    EditText edt_Number;
    Button sosBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt_Number = findViewById(R.id.txt_Number);
        edt_Number = findViewById(R.id.edt_Number);
        sosBtn = findViewById(R.id.sosBtn);
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.SEND_SMS
        }, 0);

        SharedPreferences sharedPreferences = getSharedPreferences("pref", MODE_PRIVATE);
        String saveNumber = sharedPreferences.getString("saveNumber", "");
        edt_Number.setText(saveNumber);
        txt_Number.setText("현재 저장되어 있는 번호 : "+saveNumber);

        sosBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE);
                try{
                    Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    String phoneNo =edt_Number.getText().toString();
                    txt_Number.setText("저장한 번호 : "+phoneNo);
                    SharedPreferences sharedPreferences =getSharedPreferences("pref",MODE_PRIVATE);
                    SharedPreferences.Editor editor= sharedPreferences.edit();
                    editor.putString("saveNumber",phoneNo);
                    editor.commit();
                    String gps_location =String.valueOf(location.getLatitude())+","+String.valueOf(location.getLongitude()); //위도와 경도
                    Log.d("log",gps_location);
                    SMS_Send(phoneNo,gps_location);//sms_send로 번호와 ,위도경도를 보내주기

                }catch (SecurityException e){
                    e.getMessage();
                }
            }
        });
    }
    private void SMS_Send(String phoneNO, String message){ // SmsManager API
        String sms_message = "구글 지도 위치를 보내왔습니다.\n";
        sms_message += "http://maps.google.com/maps?f=q&q="+message+"\n"+"누르면 상대방의 위치를 확인할 수 있습니다.";

        try {
            //전송
            SmsManager smsManager = SmsManager.getDefault();
            ArrayList<String> parts = smsManager.divideMessage(sms_message);
            smsManager.sendMultipartTextMessage(phoneNO, null, parts, null, null);
            Toast.makeText(getApplicationContext(), "위치전송 문자보내기 완료!,자리에서 벗어나지 마십시오.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "SMS faild, please try again later!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }



    }

}