package vnu.uet.mobilecourse.assistant.model.firebase;

public class OnlineState implements IFirebaseModel {

    private String id;
    private boolean state;

    @Override
    public String getId() {
        return null;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}
