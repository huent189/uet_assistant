package vnu.uet.mobilecourse.assistant.repository.firebase.chatnoti;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class Client {
    final static String FCM_BASE_URL = "https://fcm.googleapis.com/";
    public static APIService getApiService() {
        return new Retrofit.Builder()
                .baseUrl(FCM_BASE_URL)
                .client(provideClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(APIService.class);
    }
    private static OkHttpClient provideClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient.Builder().addInterceptor(interceptor).addInterceptor(chain -> {
            Request request = chain.request();
            return chain.proceed(request);
        }).build();
    }
}