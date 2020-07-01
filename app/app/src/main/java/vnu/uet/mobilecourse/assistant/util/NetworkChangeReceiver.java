package vnu.uet.mobilecourse.assistant.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

public class NetworkChangeReceiver extends BroadcastReceiver {

    private static NetworkChangeReceiver instance;

    public static NetworkChangeReceiver getInstance() {
        if (instance == null) {
            instance = new NetworkChangeReceiver();
        }

        return instance;
    }

    private MutableLiveData<Integer> liveData = new MutableLiveData<>();

    @Override
    public void onReceive(final Context context, final Intent intent) {

        int status = NetworkUtils.getConnectivityStatusString(context);

        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            liveData.postValue(status);
            Log.d("NetworkChangeReceiver", "onReceive: " + status);
        }
    }

    public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super Integer> observer) {
        liveData.observe(owner, observer);
    }

    public LiveData<Integer> getLiveData() {
        return liveData;
    }
}