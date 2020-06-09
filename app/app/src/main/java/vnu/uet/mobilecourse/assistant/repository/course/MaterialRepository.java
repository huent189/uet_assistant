package vnu.uet.mobilecourse.assistant.repository.course;

import vnu.uet.mobilecourse.assistant.database.CoursesDatabase;
import vnu.uet.mobilecourse.assistant.network.HTTPClient;
import vnu.uet.mobilecourse.assistant.network.request.CourseRequest;

public class MaterialRepository {
    private CourseRequest sender = HTTPClient.getInstance().request(CourseRequest.class);
    private CoursesDatabase database = CoursesDatabase.getDatabase();
    private void retrivePageContents(){

    }
}
