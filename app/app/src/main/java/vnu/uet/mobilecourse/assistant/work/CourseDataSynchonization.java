package vnu.uet.mobilecourse.assistant.work;

import vnu.uet.mobilecourse.assistant.database.CoursesDatabase;
import vnu.uet.mobilecourse.assistant.network.HTTPClient;
import vnu.uet.mobilecourse.assistant.network.request.CourseRequest;
import vnu.uet.mobilecourse.assistant.repository.course.CourseRepository;

import java.io.IOException;

public class CourseDataSynchonization {
    static final String TAG = "CourseDataSynchonization";
    CourseRequest sender = HTTPClient.getInstance().request(CourseRequest.class);
    CoursesDatabase database = CoursesDatabase.getDatabase();
    CourseRepository courseRepo = CourseRepository.getInstance();
    public void checkUpdate(long lastModifiedTime, boolean isAppRunning) throws IOException {
        courseRepo.updateMyCourses();
//        checkPageContent(lastModifiedTime);

    }
}
