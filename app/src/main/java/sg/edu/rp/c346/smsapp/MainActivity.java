ackage sg.edu.rp.c346.smsapp;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    EditText etTo;
    EditText etContent;
    Button bsend;
    Button bsendmsg;
    BroadcastReceiver br = new MessageReciever();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission();

        etTo = findViewById(R.id.editTextTo);
        etContent = findViewById(R.id.editTextContent);
        bsend = findViewById(R.id.button);
        bsendmsg = findViewById(R.id.button2);

        bsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // SmsManager smsManager = SmsManager.getDefault();
                // smsManager.sendTextMessage("phoneNo", null, "Message content", null, null);

                String receipentsNumber[] = {etTo.getText().toString()};
                for (int i = 0; i < receipentsNumber.length; i++) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(receipentsNumber[i], null, etContent.getText().toString(), null, null);
                }


            }
        });

        bsendmsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    // only for gingerbread and newer versions
                    Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                    smsIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    smsIntent.setType("vnd.android-dir/mms-sms");
                    smsIntent.setData(Uri.parse("sms:" + etTo.getText().toString()));
                    smsIntent.putExtra("sms_body", etContent.getText().toString());
                    startActivity(smsIntent);
                }
                else {
                    String no=etTo.getText().toString();
                    String msg=etContent.getText().toString();
                    Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                    PendingIntent pi=PendingIntent.getActivity(getApplicationContext(), 0, intent,0);
                    SmsManager sms=SmsManager.getDefault();
                    sms.sendTextMessage(no, null, msg, pi,null);
                }
            }
        });
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        this.registerReceiver(br,filter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(br);
    }

    private void checkPermission() {
        int permissionSendSMS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS);
        int permissionRecvSMS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_SMS);
        if (permissionSendSMS != PackageManager.PERMISSION_GRANTED &&
                permissionRecvSMS != PackageManager.PERMISSION_GRANTED) {
            String[] permissionNeeded = new String[]{Manifest.permission.SEND_SMS,
                    Manifest.permission.RECEIVE_SMS};
            ActivityCompat.requestPermissions(this, permissionNeeded, 1);
        }
    }
}
