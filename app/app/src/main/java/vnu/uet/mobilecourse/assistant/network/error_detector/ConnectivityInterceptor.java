package vnu.uet.mobilecourse.assistant.network.error_detector;

import android.content.Context;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import vnu.uet.mobilecourse.assistant.exception.NoConnectivityException;

import java.io.IOException;

public class ConnectivityInterceptor implements Interceptor {

    private Context mContext;

    public ConnectivityInterceptor(Context context) {
        mContext = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (!NetworkUtil.isOnline(mContext)) {
            throw new NoConnectivityException();
        }

        Request.Builder builder = chain.request().newBuilder();
        return chain.proceed(builder.build());
    }
}
