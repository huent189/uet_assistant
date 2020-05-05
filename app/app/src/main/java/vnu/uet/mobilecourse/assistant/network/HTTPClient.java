package vnu.uet.mobilecourse.assistant.network;

import okhttp3.*;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import vnu.uet.mobilecourse.assistant.SharedPreferencesManager;

import java.io.IOException;

public class HTTPClient {
    private static Retrofit coursesClient;
    private static final String COURSES_BASE_URL = "https://courses.uet.vnu.edu.vn";
    public static Retrofit getCoursesClient() {
        if (coursesClient == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    HttpUrl url = original.url().newBuilder().addQueryParameter("service", "moodle_mobile_app")
                                    .addQueryParameter("wstoken", SharedPreferencesManager.getValue(SharedPreferencesManager.TOKEN))
                                    .build();
                    Request request = original.newBuilder().url(url).build();
                    return chain.proceed(request);
                }
                }).build();
            coursesClient = new retrofit2.Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(COURSES_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return coursesClient;
    }
}
