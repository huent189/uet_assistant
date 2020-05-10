package vnu.uet.mobilecourse.assistant.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import vnu.uet.mobilecourse.assistant.model.Course;
import vnu.uet.mobilecourse.assistant.repository.CourseRepository;
import vnu.uet.mobilecourse.assistant.view.course.CoursesFragment;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CoursesViewModel extends ViewModel {

    private LiveData<List<Course>> courses;

    private CourseRepository repository;

    private CoursesFragment view;

    private static final int MAX_RECENTLY_INDEX = 4;

    public void initialize() {
        if (courses == null) {
            repository = CourseRepository.getInstance();
            courses = repository.getCourses();
        }
    }

    public LiveData<List<Course>> getCourses() {
        return courses;
    }

    public LiveData<List<Course>> getRecentlyCourses() {
        MutableLiveData<List<Course>> liveData = new MutableLiveData<List<Course>>();

        if (view != null) {
            courses.observe(view.getViewLifecycleOwner(), new Observer<List<Course>>() {
                @Override
                public void onChanged(List<Course> courses) {
                    if (courses == null) {
                        liveData.postValue(null);
                        return;
                    }

                    List<Course> recentlyCourses = courses
                            .stream()
                            .sorted(new Comparator<Course>() {
                                @Override
                                public int compare(Course o1, Course o2) {
                                    return o1.getCode().compareTo(o2.getCode());
                                }
                            })
                            .collect(Collectors.toList())
                            .subList(0, MAX_RECENTLY_INDEX);

                    liveData.postValue(recentlyCourses);
                }
            });
        }

        return liveData;
    }

    public void setView(CoursesFragment view) {
        this.view = view;
    }

    public CoursesFragment getView() {
        return view;
    }
}