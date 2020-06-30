package vnu.uet.mobilecourse.assistant.work.courses;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.google.gson.JsonElement;
import retrofit2.Response;
import vnu.uet.mobilecourse.assistant.database.CoursesDatabase;
import vnu.uet.mobilecourse.assistant.network.CourseClient;
import vnu.uet.mobilecourse.assistant.network.request.CourseRequest;

import java.io.IOException;

public class CourseActionWorker extends Worker {
    public static final String MATERIAL_ID = "MATERIAL_ID";
    public CourseActionWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        int id = getInputData().getInt(MATERIAL_ID, -1);
        if(id != -1){
            int completion = CoursesDatabase.getDatabase().materialDAO().getMaterialCompletion(id);
            try {
                 Response<JsonElement> response = CourseClient.getInstance().request(CourseRequest.class).updateMaterialCompletion(id, completion).execute();
                if(!response.isSuccessful()){
                    return Result.failure();
                }
            } catch (IOException e) {
                return Result.retry();
            }
        }
        return Result.success();
    }
}
