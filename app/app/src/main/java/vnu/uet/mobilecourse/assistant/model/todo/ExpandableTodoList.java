package vnu.uet.mobilecourse.assistant.model.todo;

import android.os.Parcel;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import vnu.uet.mobilecourse.assistant.model.CourseContent;
import vnu.uet.mobilecourse.assistant.model.ExpandableCourseContent;
import vnu.uet.mobilecourse.assistant.model.FirebaseModel.TodoDocument;
import vnu.uet.mobilecourse.assistant.model.FirebaseModel.TodoListDocument;
import vnu.uet.mobilecourse.assistant.model.Material;

public class ExpandableTodoList extends ExpandableGroup<TodoDocument> {
    public ExpandableTodoList(String title, List<TodoDocument> items) {
        super(title, items);
    }

    protected ExpandableTodoList(Parcel in) {
        super(in);
    }

    public static ExpandableTodoList convert(TodoListDocument todoList) {
        String title = todoList.getTitle();

        List<TodoDocument> items = new ArrayList<>(todoList.getTodos());

        return new ExpandableTodoList(title, items);
    }

    public static List<ExpandableTodoList> convert(List<TodoListDocument> todoLists) {
        return todoLists.stream()
                .map(ExpandableTodoList::convert)
                .collect(Collectors.toList());
    }
}
