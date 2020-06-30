package vnu.uet.mobilecourse.assistant.adapter.viewholder;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.Material;
import vnu.uet.mobilecourse.assistant.model.material.CourseConstant;
import vnu.uet.mobilecourse.assistant.repository.course.CourseActionRepository;

public class MaterialViewHolder extends ChildViewHolder {

    private TextView mTvTaskTitle;
    private CheckBox mCbTaskStatus;
    private ImageView mIvMaterialIcon;
    private NavController mNavController;

    public MaterialViewHolder(@NonNull View itemView, NavController navController) {
        super(itemView);

        mTvTaskTitle = itemView.findViewById(R.id.tvTaskTitle);
        mCbTaskStatus = itemView.findViewById(R.id.cbTaskStatus);
        mIvMaterialIcon = itemView.findViewById(R.id.ivMaterialIcon);

        mNavController = navController;
    }

    public void bind(Material material) {
        mTvTaskTitle.setText(material.getTitle());

        switch (material.getType()) {
            case CourseConstant.MaterialType.PAGE:
                mIvMaterialIcon.setImageResource(R.drawable.ic_description_32dp);
                break;

            case CourseConstant.MaterialType.ASSIGN:
                mIvMaterialIcon.setImageResource(R.drawable.ic_assignment_32dp);
                break;

            case CourseConstant.MaterialType.QUIZ:
                mIvMaterialIcon.setImageResource(R.drawable.ic_format_list_bulleted_32dp);
                break;

            case CourseConstant.MaterialType.URL:
                mIvMaterialIcon.setImageResource(R.drawable.ic_language_32dp);
                break;

            case CourseConstant.MaterialType.RESOURCE:
                mIvMaterialIcon.setImageResource(R.drawable.ic_image_32dp);
                break;

            case CourseConstant.MaterialType.LABEL:
                mIvMaterialIcon.setImageResource(R.drawable.ic_text_fields_32dp);
                break;

            case CourseConstant.MaterialType.FORUM:
                mIvMaterialIcon.setImageResource(R.drawable.ic_widgets_32dp);
                break;

            default:
                mIvMaterialIcon.setImageResource(R.drawable.ic_extension_32dp);
                break;
        }

        //TODO: mark as done
//        mCbTaskStatus.setClickable(false);
//        mCbTaskStatus.setActivated(false);

        if (material.getCompletion() == 1) {
            mCbTaskStatus.setChecked(true);
        } else {
            mCbTaskStatus.setChecked(false);
        }

        mCbTaskStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
            CourseActionRepository repository = new CourseActionRepository();
            if (isChecked) {
                repository.triggerMaterialCompletion(material);
            } else {
                repository.triggerMaterialUnCompletion(material);
            }

            onCompleteChange();
        });

        itemView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putParcelable("material", material);
            int actionId;

            if (CourseConstant.MaterialType.FORUM.equals(material.getType())) {
                actionId = R.id.action_navigation_explore_course_to_navigation_forum;
            } else {
                actionId = R.id.action_navigation_explore_course_to_navigation_material;
            }

            mNavController.navigate(actionId, bundle);

        });
    }

    protected void onCompleteChange() {

    }
}