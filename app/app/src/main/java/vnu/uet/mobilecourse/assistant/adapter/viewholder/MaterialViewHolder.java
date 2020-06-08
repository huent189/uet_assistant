package vnu.uet.mobilecourse.assistant.adapter.viewholder;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.Material;
import vnu.uet.mobilecourse.assistant.model.material.CourseConstant;

public class MaterialViewHolder extends ChildViewHolder {

    private TextView mTvTaskTitle;
    private ImageView mIvTaskStatus;
    private ImageView mIvMaterialIcon;
    private View mView;
    private NavController mNavController;

    public MaterialViewHolder(@NonNull View itemView, NavController navController) {
        super(itemView);

        mTvTaskTitle = itemView.findViewById(R.id.tvTaskTitle);
        mIvTaskStatus = itemView.findViewById(R.id.ivTaskStatus);
        mIvMaterialIcon = itemView.findViewById(R.id.ivMaterialIcon);
        mView = itemView;

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

            default:
                mIvMaterialIcon.setImageResource(R.drawable.ic_school_32dp);
                break;
        }

        if (material.getCompletion() == 1) {
            mIvTaskStatus.setImageResource(R.drawable.ic_check_circle_24dp);
        } else {
            mIvTaskStatus.setImageResource(R.drawable.ic_unchecked_circle_24dp);
        }

        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("material", material);

                int actionId = R.id.action_navigation_explore_course_to_navigation_material;

                mNavController.navigate(actionId, bundle);
            }
        });
    }
}