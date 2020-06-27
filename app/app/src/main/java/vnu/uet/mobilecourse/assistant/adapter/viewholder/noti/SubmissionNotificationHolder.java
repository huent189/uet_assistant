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
import vnu.uet.mobilecourse.assistant.model.CourseOverview;
import vnu.uet.mobilecourse.assistant.model.Material;
import vnu.uet.mobilecourse.assistant.model.notification.SubmissionNotification;
import vnu.uet.mobilecourse.assistant.repository.course.CourseRepository;

public class SubmissionNotificationHolder extends NotificationHolder<SubmissionNotification> {

    public SubmissionNotificationHolder(@NonNull View view) {
        super(view);
    }

    @Override
    protected int getIconResId() {
        return R.drawable.img_material;
    }

    @Override
    protected void onNavigate(SubmissionNotification notification, NavController navController, Fragment owner) {
        int courseId = notification.getCourseId();
        int materialId = notification.getMaterialId();

        CourseRepository.getInstance()
                .getContent(courseId).observe(owner.getViewLifecycleOwner(), new Observer<List<CourseOverview>>() {
                    @Override
                    public void onChanged(List<CourseOverview> courseOverviews) {
                        if (courseOverviews != null && !courseOverviews.isEmpty()) {
                            Material foundMaterial = null;

                            for (CourseOverview overview : courseOverviews) {
                                for (Material material : overview.getMaterials()) {
                                    if (material.getId() == materialId) {
                                        foundMaterial = material;
                                        break;
                                    }
                                }

                                if (foundMaterial != null) break;
                            }

                            if (foundMaterial != null) {
                                Bundle bundle = new Bundle();
                                bundle.putParcelable("material", foundMaterial);
                                navController.navigate(R.id.action_navigation_notifications_to_navigation_material, bundle);
                            } else {
                                Context context = owner.getContext();
                                final String MATERIAL_NOT_FOUND_MSG = "Không tìm thấy bài tập";

                                Toast.makeText(context, MATERIAL_NOT_FOUND_MSG, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}
