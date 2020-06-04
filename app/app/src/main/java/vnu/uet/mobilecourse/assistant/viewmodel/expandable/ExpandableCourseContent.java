package vnu.uet.mobilecourse.assistant.viewmodel.expandable;

import android.os.Parcel;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;
import java.util.stream.Collectors;

import vnu.uet.mobilecourse.assistant.model.CourseOverview;
import vnu.uet.mobilecourse.assistant.model.Material;

public class ExpandableCourseContent extends ExpandableGroup<Material> {
    public ExpandableCourseContent(String title, List<Material> items) {
        super(title, items);
    }

    protected ExpandableCourseContent(Parcel in) {
        super(in);
    }

    public static ExpandableCourseContent convert(CourseOverview content) {
        String title = content.getWeekInfo().getTitle();
        List<Material> items = content.getMaterials();

        return new ExpandableCourseContent(title, items);
    }

    public static List<ExpandableCourseContent> convert(List<CourseOverview> contents) {
        return contents.stream()
                .map(ExpandableCourseContent::convert)
                .collect(Collectors.toList());
    }
}
