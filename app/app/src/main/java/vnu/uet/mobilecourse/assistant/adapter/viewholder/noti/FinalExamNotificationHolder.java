package vnu.uet.mobilecourse.assistant.adapter.viewholder.noti;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.ICourse;
import vnu.uet.mobilecourse.assistant.model.notification.CourseAttendantNotification;
import vnu.uet.mobilecourse.assistant.model.notification.FinalExamNotification;
import vnu.uet.mobilecourse.assistant.repository.course.CourseRepository;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;

public class FinalExamNotificationHolder extends NotificationHolder<FinalExamNotification> {

    public FinalExamNotificationHolder(@NonNull View view) {
        super(view);
    }

    @Override
    protected int getIconResId() {
        return R.drawable.img_graduation;
    }

    @Override
    protected void onNavigate(FinalExamNotification notification, NavController navController, Fragment owner) {
        String courseCode = notification.getCourseCode();

        CourseRepository.getInstance().getFullCourses().observe(owner.getViewLifecycleOwner(), new Observer<StateModel<List<ICourse>>>() {
            @Override
            public void onChanged(StateModel<List<ICourse>> stateModel) {
                switch (stateModel.getStatus()) {
                    case SUCCESS:
                        List<ICourse> courses = stateModel.getData();

                        courses.stream()
                                .filter(course -> course.getCode().equals(courseCode))
                                .findFirst()
                                .ifPresent(course -> {
                                    Bundle bundle = new Bundle();
                                    bundle.putParcelable("course", course);

                                    navController.navigate(R.id.action_navigation_notifications_to_navigation_explore_course, bundle);
                                });

                        break;

                    case ERROR:
                        Context context = owner.getContext();
                        final String COURSE_NOT_FOUND_MSG = "Không tìm thấy khóa học";

                        Toast.makeText(context, COURSE_NOT_FOUND_MSG, Toast.LENGTH_SHORT).show();
                        break;

                }
            }
        });
    }
}
