package vnu.uet.mobilecourse.assistant.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.ui.view.CoursesFragment;
import vnu.uet.mobilecourse.assistant.ui.model.Course;

public class RecentlyCoursesAdapter extends PagerAdapter {
    private List<Course> courses;

    private LayoutInflater layoutInflater;

    private CoursesFragment owner;

    public RecentlyCoursesAdapter(List<Course> courses, CoursesFragment owner) {
        this.courses = courses;
        this.owner = owner;
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

        layoutInflater = owner.getLayoutInflater();

        View view = layoutInflater.inflate(R.layout.card_course, container, false);

        ImageView ivThumbnail = view.findViewById(R.id.ivCourseThumbnail);
        ivThumbnail.setImageResource(course.getThumbnail());

        TextView tvTitle = view.findViewById(R.id.tvCourseTitle);
        tvTitle.setText(course.getTitle());

        CardView cvCourseContainer = view.findViewById(R.id.cvCourseContainer);
        int cardColor = getBackgroundColor(course.getThumbnail());
        cvCourseContainer.setCardBackgroundColor(cardColor);

        container.addView(view, 0);

        return view;
    }

    private int getBackgroundColor(int thumbnailImage) {
        int cardColor;

        switch (thumbnailImage) {
            case R.drawable.isometric_course_thumbnail:
                cardColor = R.color.cardColor1;
                break;

            case R.drawable.isometric_math_course_background:
                cardColor = R.color.cardColor2;
                break;

            default:
                cardColor = R.color.cardColor1;
                break;
        }

        Context context = Objects.requireNonNull(owner.getContext());

        return ContextCompat.getColor(context, cardColor);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
