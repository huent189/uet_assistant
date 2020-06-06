package vnu.uet.mobilecourse.assistant.work;

import com.google.gson.JsonElement;
import retrofit2.Call;
import vnu.uet.mobilecourse.assistant.database.CoursesDatabase;
import vnu.uet.mobilecourse.assistant.network.HTTPClient;
import vnu.uet.mobilecourse.assistant.network.request.CourseRequest;
import vnu.uet.mobilecourse.assistant.network.response.CoursesResponseCallback;
import vnu.uet.mobilecourse.assistant.network.response.PageContentResponse;
import vnu.uet.mobilecourse.assistant.repository.course.CourseRepository;

import java.io.IOException;

public class CourseDataSynchonization {
    static final String TAG = "CourseDataSynchonization";
    CourseRequest sender = HTTPClient.getInstance().request(CourseRequest.class);
    CoursesDatabase database = CoursesDatabase.getDatabase();
    CourseRepository courseRepo = CourseRepository.getInstance();
    public void checkUpdate(long lastModifiedTime, boolean isAppRunning) throws IOException {
        courseRepo.synchronizeAccessTime();
        checkPageContent(lastModifiedTime);

    }
    public void checkPageContent(long lastModifiedTime) throws IOException {
        int[] courses = database.coursesDAO().getCourseId();
        Call<JsonElement> call = sender.getPagesByCourses(null);
        CoursesResponseCallback<PageContentResponse[]> handler =
                new CoursesResponseCallback<PageContentResponse[]>(PageContentResponse[].class) {
            @Override
            public void onSuccess(PageContentResponse[] response) {
                for(PageContentResponse page : response){
                    if(page.getTimeModified() >= lastModifiedTime) {
                        CoursesDatabase.databaseWriteExecutor.execute(() -> {
                            database.materialDAO().updatePageContent(
                                    page.getContent(), page.getTimeModified(), page.getCourseId()
                            );
                        });
                    }
                }
            }
        };
        handler.onResponse(call, call.execute());
    }
}
