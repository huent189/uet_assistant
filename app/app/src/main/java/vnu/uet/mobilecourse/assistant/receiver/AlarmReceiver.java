package vnu.uet.mobilecourse.assistant.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String data = intent.getStringExtra("data");

        Toast.makeText(context, data, Toast.LENGTH_LONG).show();
    }
}
