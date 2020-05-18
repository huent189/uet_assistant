package vnu.uet.mobilecourse.assistant.model.FirebaseModel;


import android.os.Parcel;
import android.os.Parcelable;

public class TodoDocument implements Parcelable {
    private String ownerId;
    private String todoListId;
    private String todoId;
    private String title;
    private String description;
    private int deadline;
    private String status;

    public TodoDocument() {

    }

    public TodoDocument(Parcel in) {
        ownerId = in.readString();
        todoListId = in.readString();
        todoId = in.readString();
        title = in.readString();
        description = in.readString();
        deadline = in.readInt();
        status = in.readString();
    }

    public static final Creator<TodoDocument> CREATOR = new Creator<TodoDocument>() {
        @Override
        public TodoDocument createFromParcel(Parcel in) {
            return new TodoDocument(in);
        }

        @Override
        public TodoDocument[] newArray(int size) {
            return new TodoDocument[size];
        }
    };

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

    public String getTodoId() {
        return todoId;
    }

    public void setTodoId(String todoId) {
        this.todoId = todoId;
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

    public int getDeadline() {
        return deadline;
    }

    public void setDeadline(int deadline) {
        this.deadline = deadline;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static final String DONE = "done";
    public static final String DOING = "doing";

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ownerId);
        dest.writeString(todoListId);
        dest.writeString(todoId);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeInt(deadline);
        dest.writeString(status);
    }
}
