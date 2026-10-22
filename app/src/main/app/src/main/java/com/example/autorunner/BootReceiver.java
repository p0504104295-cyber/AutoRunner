package com.example.autorunner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import java.io.DataOutputStream;

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "AutoRunner";
    // זמן המתנה במילישניות (למשל: 120,000 מילישניות = 2 דקות)
    private static final long DELAY_MS = 120000;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d(TAG, "התקבל אירוע אתחול. מתחיל תהליך המתנה.");

            // הרצת ההמתנה והפקודה בתהליך נפרד כדי לא לחסום את המערכת
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(DELAY_MS);
                        executeScriptAsRoot();
                    } catch (InterruptedException e) {
                        Log.e(TAG, "ההמתנה הופסקה: " + e.getMessage());
                    }
                }
            }).start();
        }
    }

    private void executeScriptAsRoot() {
        Process process = null;
        DataOutputStream os = null;
        try {
            Log.d(TAG, "מנסה להפעיל את הסקריפט דרך su...");
            // פתיחת מעטפת su ישירות
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());

            // כתיבת הפקודה ישירות לתוך התהליך
            os.writeBytes("sh /data/local/tmp/autorun.sh\n");
            os.flush();

            // יציאה מהמעטפת בסיום
            os.writeBytes("exit\n");
            os.flush();

            process.waitFor();
            Log.d(TAG, "הסקריפט בוצע בהצלחה.");
        } catch (Exception e) {
            Log.e(TAG, "שגיאה בעת הרצת הסקריפט: " + e.getMessage());
        } finally {
            try {
                if (os != null) os.close();
                if (process != null) process.destroy();
            } catch (Exception ignored) {}
        }
    }
}
