package vnu.uet.mobilecourse.assistant.repository;

import androidx.lifecycle.LiveData;
import vnu.uet.mobilecourse.assistant.database.CoursesDatabase;
import vnu.uet.mobilecourse.assistant.database.DAO.CoursesDAO;
import vnu.uet.mobilecourse.assistant.model.Course;
import vnu.uet.mobilecourse.assistant.model.User;
import vnu.uet.mobilecourse.assistant.network.HTTPClient;
import vnu.uet.mobilecourse.assistant.network.request.CourseRequest;
import vnu.uet.mobilecourse.assistant.network.response.CoursesResponseCallback;

import java.util.List;

public class CourseRepository {
    /**
     * Singleton instance
     */
    private static CourseRepository instance;
    private CoursesDAO dao;
    private LiveData<List<Course>> myCourses;
    /**
     * Data set
     */
    /**
     * Get singleton instance
     * @return singleton instance
     */
    public static CourseRepository getInstance() {
        if (instance == null) {
            instance = new CourseRepository();
            instance.dao = CoursesDatabase.getDatabase().coursesDAO();
            instance.myCourses = instance.dao.getMyCourses();
        }

        return instance;
    }

    public LiveData<List<Course>> getCourses() {
        CourseRequest request = HTTPClient.getCoursesClient().create(CourseRequest.class);
        request.getMyCoures(User.getInstance().getUserId())
                .enqueue(new CoursesResponseCallback<Course[]>(Course[].class) {
                    @Override
                    public void onSucess(Course[] response) {
                        CoursesDatabase.databaseWriteExecutor.execute(new Runnable() {
                            @Override
                            public void run() {
                                for (Course entity:response) {
                                    dao.insert(entity);
                                }
                            }
                        });
                    }
                });
        return myCourses;
    }
}
