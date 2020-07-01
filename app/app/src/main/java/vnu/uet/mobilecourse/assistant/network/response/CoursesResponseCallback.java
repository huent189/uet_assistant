package vnu.uet.mobilecourse.assistant.network.response;

import android.util.Log;
import com.google.gson.JsonElement;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vnu.uet.mobilecourse.assistant.exception.InvalidLoginException;
import vnu.uet.mobilecourse.assistant.exception.UnavailableHostException;
import vnu.uet.mobilecourse.assistant.network.CourseClient;

import java.lang.reflect.Type;
import java.net.SocketTimeoutException;

public abstract class CoursesResponseCallback<T> implements Callback<JsonElement> {
    private Type typeParameterClass;
    private String deserializeElement;
    public CoursesResponseCallback(Class<T> typeParameterClass) {
        this.typeParameterClass = typeParameterClass;
    }

    public CoursesResponseCallback(Type typeParameterClass, String deserializeElement) {
        this.typeParameterClass = typeParameterClass;
        this.deserializeElement = deserializeElement;
    }

    @Override
    public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
        boolean flag = true;
        JsonElement error = null;
        if(response.body().isJsonObject()){
            error = response.body().getAsJsonObject().get("errorcode");
            flag = (error == null);
        }
        if(flag){
            if(deserializeElement != null){
                JsonElement realResponse = response.body().getAsJsonObject().get(deserializeElement);
                if(realResponse != null){
                    onSuccess(CourseClient.getInstance().getGson()
                            .fromJson(realResponse, typeParameterClass));
                }
                else {
                    onError(call, new Exception("structure not match"));
                }
            } else {
                onSuccess(CourseClient.getInstance().getGson().fromJson(response.body(), typeParameterClass));
            }
            Log.d("COURSES", "onResponse: " + call.request().url());

        } else {
            onError(call, errorCodeToException(error.getAsString()));
        }
    }

    @Override
    public void onFailure(Call<JsonElement> call, Throwable throwable) {
        if(throwable instanceof SocketTimeoutException){
            onError(call, new UnavailableHostException(throwable.getMessage()));
        } else {
            onError(call, throwable);
        }
    }
    public abstract void onSuccess(T response);
    public void onError(Call<JsonElement> call, Throwable throwable){
        Log.e("COURSES", "onError: " + call.request().url() + " "
                + throwable.getClass() +  " " + throwable.getMessage() );
    }
    private Throwable errorCodeToException(String errorcode){
        switch (errorcode){
            case "invalidlogin":
                return new InvalidLoginException();
            default:
                return new Exception(errorcode);
        }
    }
}
