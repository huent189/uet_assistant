package vnu.uet.mobilecourse.assistant.model.firebase;

import java.util.Arrays;
import java.util.List;

public class Connection implements IFirebaseModel {

    private String id;
    private List<String> studentIds;
    private long timestamp;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getStudentIds() {
        return studentIds;
    }

    public void setStudentIds(String from, String to) {
        this.studentIds = Arrays.asList(from, to);
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
