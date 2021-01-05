package com.project.womensecurityapp.SMS;

import android.telephony.SmsManager;
import android.util.Log;

public class SMS {

    private static final String TAG = "SMS";

    public void sendSMS(String destPhone, String message){

        Log.d(TAG, "sendSMS: trying to send SMS");

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(destPhone, null, message, null, null);
        }
        catch (Exception e){
            Log.d(TAG, "sendSMS: failed to send SMS" + e.getMessage());
        }

    }

}
