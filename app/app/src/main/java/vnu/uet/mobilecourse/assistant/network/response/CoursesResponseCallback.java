package vnu.uet.mobilecourse.assistant.network.response;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vnu.uet.mobilecourse.assistant.exception.InvalidLoginException;

public abstract class CoursesResponseCallback<T> implements Callback<JsonObject> {
    private Class<T> typeParameterClass;

    public CoursesResponseCallback(Class<T> typeParameterClass) {
        this.typeParameterClass = typeParameterClass;
    }

    @Override
    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
        JsonElement error = response.body().get("errorcode");
        if(error == null){
            T successReponse = new Gson().fromJson(response.body(), typeParameterClass);
            onSucess(successReponse);
        } else {
            onError(errorCodeToException(error.getAsString()));
        }
    }

    @Override
    public void onFailure(Call<JsonObject> call, Throwable throwable) {
        onError(new Exception(throwable.getMessage()));
    }
    public abstract void onSucess(T response);
    public abstract void onError(Exception e);
    private Exception errorCodeToException(String errorcode){
        switch (errorcode){
            case "invalidlogin":
                return new InvalidLoginException();
            default:
                return new Exception(errorcode);
        }
    }
}
