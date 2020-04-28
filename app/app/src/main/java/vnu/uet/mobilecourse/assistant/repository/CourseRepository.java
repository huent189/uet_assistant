package vnu.uet.mobilecourse.assistant.repository;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.MutableLiveData;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.Course;

public class CourseRepository {
    /**
     * Singleton instance
     */
    private static CourseRepository instance;

    /**
     * Data set
     */
    private List<Course> courses = new ArrayList<>();


    /**
     * Get singleton instance
     * @return singleton instance
     */
    public static CourseRepository getInstance() {
        if (instance == null) {
            instance = new CourseRepository();
            instance.setCourses();
            instance.setCourses();

        }

        return instance;
    }

    public MutableLiveData<List<Course>> getCourses() {
        MutableLiveData<List<Course>> data = new MutableLiveData<>();
        data.setValue(courses);

        return data;
    }

    private void setCourses() {
        courses.add(new Course(R.drawable.isometric_course_thumbnail, "Công nghệ phần mềm"));
        courses.add(new Course(R.drawable.isometric_math_course_background, "Phát triển ứng dụng di động"));
        courses.add(new Course(R.drawable.isometric_course_thumbnail, "Quản lý dự án phần mềm"));
        courses.add(new Course(R.drawable.isometric_math_course_background, "Nguyên lý hệ điều hành"));
        courses.add(new Course(R.drawable.isometric_course_thumbnail, "Cầu lông"));
    }
}
