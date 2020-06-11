package vnu.uet.mobilecourse.assistant.model.firebase;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.Exclude;

import java.util.Date;

import vnu.uet.mobilecourse.assistant.model.event.IEvent;
import vnu.uet.mobilecourse.assistant.util.DateTimeUtils;

public class Todo implements Parcelable, IFirebaseModel, IEvent {
    private String ownerId;
    private String todoListId;
    private String id;
    private String title;
    private String description;
    private long deadline;
    private boolean completed;

    @Exclude
    private String category;

    public Todo() {}

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getTodoListId() {
        return todoListId;
    }

    public void setTodoListId(String todoListId) {
        this.todoListId = todoListId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    @Override
    @Exclude
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public Date getTime() {
        return DateTimeUtils.fromSecond(deadline);
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

    public long getDeadline() {
        return deadline;
    }

    public void setDeadline(long deadline) {
        this.deadline = deadline;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ownerId);
        dest.writeString(todoListId);
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeLong(deadline);
        dest.writeInt(completed ? 1 : 0);
        dest.writeString(category);
    }

    public Todo(Parcel in) {
        ownerId = in.readString();
        todoListId = in.readString();
        id = in.readString();
        title = in.readString();
        description = in.readString();
        deadline = in.readInt();
        completed = in.readInt() == 1;
        category = in.readString();
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
}
