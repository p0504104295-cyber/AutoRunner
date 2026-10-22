package com.example.autorunner;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        TextView textView = new TextView(this);
        textView.setText("השירות הוגדר בהצלחה. האפליקציה תפעל כעת באתחול.");
        textView.setPadding(40, 40, 40, 40);
        setContentView(textView);
    }
}
