package com.example.autorunner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import java.io.DataOutputStream;

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "AutoRunner";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) ||
            "android.intent.action.LOCKED_BOOT_COMPLETED".equals(intent.getAction())) {
            
            Log.d(TAG, "התקבל אירוע אתחול. שולח פקודת השהיה והרצה ל-su.");
            executeDelayedScriptAsRoot();
        }
    }

    private void executeDelayedScriptAsRoot() {
        try {
            Process process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());

            // הפעלת תהליך רקע עצמאי (שורד סגירת אפליקציה) שממתין 120 שניות ומריץ את הסקריפט
            os.writeBytes("(sleep 120 && sh /data/local/tmp/autorun.sh) &\n");
            os.flush();

            os.writeBytes("exit\n");
            os.flush();

            process.waitFor();
            Log.d(TAG, "פקודת הרקע נשלחה בהצלחה ל-su.");
        } catch (Exception e) {
            Log.e(TAG, "שגיאה בעת שליחת הפקודה: " + e.getMessage());
        }
    }
}
