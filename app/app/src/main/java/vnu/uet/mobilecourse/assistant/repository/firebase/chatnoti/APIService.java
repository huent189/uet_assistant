package vnu.uet.mobilecourse.assistant.repository.firebase.chatnoti;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Authorization:key=AAAA6FoildQ:APA91bHsjzzQc432iF2Pz5XNagXHihtU7-nCJmnOCbWhfbSPZshkkBEu0tZjJozZWTRy5GHGcuWMjbbyWRI1mhVYdjlsKKaUS12wp9vH-l8-_8vaJymG0NvzWMvuUZmuU_i6XCK7Gk71",
                    "Content-Type: application/json"
            })
    @POST("fcm/send")
    Call<JsonObject> sendNotification(@Body JsonObject payload);
}