package vnu.uet.mobilecourse.assistant.util;

public class ChatRoomTracker {

    private static ChatRoomTracker instance;
    private String mRoomId = null;

    public static ChatRoomTracker getInstance() {
        if (instance == null) {
            instance = new ChatRoomTracker();
        }

        return instance;
    }

    public void setCurrentRoom(String roomId) {
        mRoomId = roomId;
    }

    public String getCurrentRoom() {
        return mRoomId;
    }

    public void outRoom() {
        mRoomId = null;
    }
}
