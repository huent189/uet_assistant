package vnu.uet.mobilecourse.assistant.network.response;

import android.util.Log;
import com.google.gson.JsonElement;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vnu.uet.mobilecourse.assistant.exception.InvalidLoginException;
import vnu.uet.mobilecourse.assistant.network.HTTPClient;

import java.lang.reflect.Type;

public abstract class CoursesResponseCallback<T> implements Callback<JsonElement> {
    private Type typeParameterClass;

    public CoursesResponseCallback(Class<T> typeParameterClass) {
        this.typeParameterClass = typeParameterClass;
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
            T successReponse = HTTPClient.getInstance().getGson().fromJson(response.body(), typeParameterClass);
            Log.d("COURSES", "onResponse: " + call.request().url());
            onSuccess(successReponse);
        } else {
            onError(call, errorCodeToException(error.getAsString()));
        }
    }

    @Override
    public void onFailure(Call<JsonElement> call, Throwable throwable) {
        onError(call, new Exception(throwable.getMessage()));
    }
    public abstract void onSuccess(T response);
    public void onError(Call<JsonElement> call, Exception e){
        Log.e("COURSES", "onError: " + call.request().url() + " "
                + e.getClass() +  " " + e.getMessage() );
    }
    private Exception errorCodeToException(String errorcode){
        switch (errorcode){
            case "invalidlogin":
                return new InvalidLoginException();
            default:
                return new Exception(errorcode);
        }
    }
}
