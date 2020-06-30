package vnu.uet.mobilecourse.assistant.network;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.*;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import vnu.uet.mobilecourse.assistant.model.CourseOverview;
import vnu.uet.mobilecourse.assistant.model.Grade;
import vnu.uet.mobilecourse.assistant.model.User;
import vnu.uet.mobilecourse.assistant.model.forum.Post;
import vnu.uet.mobilecourse.assistant.model.material.AssignmentContent;
import vnu.uet.mobilecourse.assistant.model.material.PageContent;
import vnu.uet.mobilecourse.assistant.network.deserializer.*;
import vnu.uet.mobilecourse.assistant.network.error_detector.ConnectivityInterceptor;
import vnu.uet.mobilecourse.assistant.view.MyApplication;

import java.io.IOException;

public class CourseClient {
    private Retrofit coursesClient;
    private static final String COURSES_BASE_URL = "https://courses.uet.vnu.edu.vn";
    private Gson gson;
    private static CourseClient instance;
    private CourseClient(){
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new ConnectivityInterceptor(MyApplication.getInstance().getApplicationContext()))
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
        gsonBuilder.registerTypeAdapter(CourseOverview[].class, new CourseContentDeserializer())
                    .registerTypeAdapter(AssignmentContent[].class, new AssignmentDeserializer())
                    .registerTypeAdapter(PageContent[].class, new PageContentDeseializer())
                    .registerTypeAdapter(Post.class, new ForumPostDeserializer())
                    .registerTypeAdapter(Grade[].class, new CourseGradeDeserializer());
        gson = gsonBuilder.create();
        coursesClient = new retrofit2.Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(COURSES_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }
    public static CourseClient getInstance(){
        if(instance == null){
            instance = new CourseClient();
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
