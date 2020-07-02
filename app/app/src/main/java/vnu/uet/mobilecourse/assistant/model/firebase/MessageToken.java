package vnu.uet.mobilecourse.assistant.model.firebase;

public class MessageToken implements IFirebaseModel {

    private String id;
    private long timeUpdated;
    private String token;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTimeUpdated() {
        return timeUpdated;
    }

    public void setTimeUpdated(long timeUpdated) {
        this.timeUpdated = timeUpdated;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
