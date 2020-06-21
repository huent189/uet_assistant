package vnu.uet.mobilecourse.assistant.util;

import android.annotation.SuppressLint;

import com.google.firebase.firestore.util.Util;

import vnu.uet.mobilecourse.assistant.model.User;

public class FirebaseStructureId {

    private static final String USER_CODE = User.getInstance().getStudentId();

    public static String interestDiscussion(int discussionId) {
        return BEGIN_PREFIX + USER_CODE + INTEREST_DISCUSSION_PREFIX + discussionId;
    }

    public static String directedChat(String mateId) {
        return BEGIN_PREFIX + USER_CODE + DIRECT_CHAT_PREFIX + mateId;

    }

    @SuppressLint("RestrictedApi")
    public static String groupChat() {
        String id;

        do {
            id = Util.autoId();
        } while (isDirectedChat(id));

        return id;
    }

    public static boolean isDirectedChat(String id) {
        return (id.contains(BEGIN_PREFIX) || id.contains(DIRECT_CHAT_PREFIX));
    }

    private static final String INTEREST_DISCUSSION_PREFIX = "_interest_";
    private static final String DIRECT_CHAT_PREFIX = "_direct_chat_";
    private static final String BEGIN_PREFIX = "structure_id_";
}
