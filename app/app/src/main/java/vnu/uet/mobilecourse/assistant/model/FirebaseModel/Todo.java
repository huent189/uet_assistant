package vnu.uet.mobilecourse.assistant.model.FirebaseModel;


import android.os.Parcel;
import android.os.Parcelable;

public class Todo implements Parcelable, IFirebaseModel {
    private String ownerId;
    private String todoListId;
    private String id;
    private String title;
    private String description;
    private long deadline;
    private boolean completed;

    public Todo() {

    }

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

    public void setDeadline(int deadline) {
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
    }

    public Todo(Parcel in) {
        ownerId = in.readString();
        todoListId = in.readString();
        id = in.readString();
        title = in.readString();
        description = in.readString();
        deadline = in.readInt();
        completed = in.readInt() == 1;
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
