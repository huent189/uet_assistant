package vnu.uet.mobilecourse.assistant.viewmodel.expandable;

import android.os.Parcel;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import vnu.uet.mobilecourse.assistant.model.FirebaseModel.Todo;
import vnu.uet.mobilecourse.assistant.model.FirebaseModel.TodoList;

public class ExpandableTodoList extends ExpandableGroup<Todo> {
    public ExpandableTodoList(String title, List<Todo> items) {
        super(title, items);
    }

    protected ExpandableTodoList(Parcel in) {
        super(in);
    }

    public static ExpandableTodoList convert(TodoList todoList) {
        String title = todoList.getTitle();

        List<Todo> items = new ArrayList<>(todoList.getTodos());

        return new ExpandableTodoList(title, items);
    }

    public static List<ExpandableTodoList> convert(List<TodoList> todoLists) {
        return todoLists.stream()
                .map(ExpandableTodoList::convert)
                .collect(Collectors.toList());
    }
}
