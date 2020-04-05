package vnu.uet.mobilecourse.assistant.ui.courses;

import android.animation.ArgbEvaluator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.Course;

public class Adapter extends PagerAdapter {
    private List<Course> courses;

    private LayoutInflater layoutInflater;

    private Fragment context;

    public Adapter(List<Course> courses, Fragment context) {
        this.courses = courses;
        this.context = context;
    }

    @Override
    public int getCount() {
        return courses.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Course course = courses.get(position);

        layoutInflater = context.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.card_course, container, false);

        CardView cvCourseContainer = view.findViewById(R.id.cvCourseContainer);
        ImageView ivThumbnail = view.findViewById(R.id.ivCourseThumbnail);
        TextView tvTitle = view.findViewById(R.id.tvCourseTitle);

        ivThumbnail.setImageResource(course.getThumbnail());
        tvTitle.setText(course.getTitle());

        int cardColor;

        switch (course.getThumbnail()) {
            case R.drawable.isometric_course_thumbnail:
                cardColor = R.color.cardColor1;
                break;

            case R.drawable.isometric_math_course_background:
                cardColor = R.color.cardColor2;
                tvTitle.setTextColor(Color.BLACK);
                break;

            default:
                cardColor = R.color.cardColor1;
                break;
        }

        cvCourseContainer.setCardBackgroundColor(ContextCompat.getColor(context.getContext(), cardColor));

        container.addView(view, 0);


        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
