package vnu.uet.mobilecourse.assistant.network;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.*;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import vnu.uet.mobilecourse.assistant.model.CourseContent;
import vnu.uet.mobilecourse.assistant.model.User;
import vnu.uet.mobilecourse.assistant.network.deserializer.CourseContentDeserializer;

import java.io.IOException;

public class HTTPClient {
    private Retrofit coursesClient;
    private static final String COURSES_BASE_URL = "https://courses.uet.vnu.edu.vn";
    private Gson gson;
    private static HTTPClient instance;
    private HTTPClient(){
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();
                        Log.d("COURSES", "intercept: " + User.getInstance().getToken());
                        HttpUrl url = original.url().newBuilder().addQueryParameter("moodlewsrestformat", "json")
                                .addQueryParameter("wstoken", User.getInstance().getToken())
                                .build();
                        Request request = original.newBuilder().url(url).build();
                        return chain.proceed(request);
                    }
                }).build();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(CourseContent[].class, new CourseContentDeserializer());
        gson = gsonBuilder.create();
        coursesClient = new retrofit2.Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(COURSES_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }
    public static HTTPClient getInstance(){
        if(instance == null){
            instance = new HTTPClient();
        }
        return instance;
    }
    public <T> T request(final Class<T> service){
        return coursesClient.create(service);
    }
    public Gson getGson() {
        return gson;
    }
}
