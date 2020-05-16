package vnu.uet.mobilecourse.assistant.model.todo;

import android.os.Parcel;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import vnu.uet.mobilecourse.assistant.model.CourseContent;
import vnu.uet.mobilecourse.assistant.model.ExpandableCourseContent;
import vnu.uet.mobilecourse.assistant.model.Material;

public class ExpandableTodoList extends ExpandableGroup<Todo> {
    public ExpandableTodoList(String title, List<Todo> items) {
        super(title, items);
    }

    protected ExpandableTodoList(Parcel in) {
        super(in);
    }

    public static ExpandableTodoList convert(TodoList todoList) {
        String title = todoList.getTitle();

        List<Todo> items = new ArrayList<>(todoList);

        return new ExpandableTodoList(title, items);
    }

    public static List<ExpandableTodoList> convert(List<TodoList> todoLists) {
        return todoLists.stream()
                .map(ExpandableTodoList::convert)
                .collect(Collectors.toList());
    }
}
