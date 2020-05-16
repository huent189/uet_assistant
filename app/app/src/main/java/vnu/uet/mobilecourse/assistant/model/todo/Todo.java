package vnu.uet.mobilecourse.assistant.model.todo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Todo implements Parcelable {
    private String id;

    private String title;

    private String todoListId;

    private String todoListTitle;

    private String description;

    private Date deadline;

    private Status status = Status.DOING;

    public Todo() {

    }

    public Todo(Parcel in) {
        id = in.readString();
        title = in.readString();
        todoListId = in.readString();
        todoListTitle = in.readString();
        description = in.readString();
    }

    public static final Creator<Todo> CREATOR = new Creator<Todo>() {
        @Override
        public Todo createFromParcel(Parcel in) {
            return new Todo(in);
        }

        @Override
        public Todo[] newArray(int size) {
            return new Todo[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTodoListId() {
        return todoListId;
    }

    public void setTodoListId(String todoListId) {
        this.todoListId = todoListId;
    }

    public void setTodoListTitle(String todoListTitle) {
        this.todoListTitle = todoListTitle;
    }

    public String getTodoListTitle() {
        return todoListTitle;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(todoListId);
        dest.writeString(todoListTitle);
        dest.writeString(description);
    }

    public enum Status {
        DOING,
        DONE
    }
}
