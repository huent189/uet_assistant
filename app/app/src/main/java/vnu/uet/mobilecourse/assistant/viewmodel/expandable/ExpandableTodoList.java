package vnu.uet.mobilecourse.assistant.viewmodel.expandable;

import android.os.Parcel;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import vnu.uet.mobilecourse.assistant.model.firebase.Todo;
import vnu.uet.mobilecourse.assistant.model.firebase.TodoList;

public class ExpandableTodoList extends ExpandableGroup<Todo> {

    private String mId;

    public ExpandableTodoList(String todoListId, String title, List<Todo> items) {
        super(title, items);
        mId = todoListId;
    }

    public String getId() {
        return mId;
    }

    protected ExpandableTodoList(Parcel in) {
        super(in);
        mId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(mId);
    }

    public static ExpandableTodoList convert(TodoList todoList) {
        String title = todoList.getTitle();

        List<Todo> items = new ArrayList<>(todoList.getTodos());

        return new ExpandableTodoList(todoList.getId(), title, items);
    }

    public static List<ExpandableTodoList> convert(List<TodoList> todoLists) {
        return todoLists.stream()
                .map(ExpandableTodoList::convert)
                .collect(Collectors.toList());
    }
}
