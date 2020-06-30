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
        int codeComparision = Integer.compare(USER_CODE.hashCode(), mateId.hashCode());

        String id;
        if (codeComparision >= 0) {
            id = BEGIN_PREFIX + USER_CODE + DIRECT_CHAT_PREFIX + mateId;
        } else {
            id = BEGIN_PREFIX + mateId + DIRECT_CHAT_PREFIX + USER_CODE;
        }

        return id;
    }

    public static String getMateId(String directChatId) {
        return directChatId.replace(BEGIN_PREFIX, StringConst.EMPTY)
                .replace(USER_CODE, StringConst.EMPTY)
                .replace(DIRECT_CHAT_PREFIX, StringConst.EMPTY);
    }

    @SuppressLint("RestrictedApi")
    public static String groupChat() {
        String id;

        do {
            id = Util.autoId();
        } while (isDirectedChat(id));

        return id;
    }

    public static String connect(String from, String to) {
        int codeComparision = Integer.compare(from.hashCode(), to.hashCode());

        String id;
        if (codeComparision >= 0) {
            id = BEGIN_PREFIX + from + CONNECT_PREFIX + to;
        } else {
            id = BEGIN_PREFIX + to + CONNECT_PREFIX + from;
        }

        return id;
    }

    public static boolean isDirectedChat(String id) {
        return (id.contains(BEGIN_PREFIX) || id.contains(DIRECT_CHAT_PREFIX));
    }

    private static final String INTEREST_DISCUSSION_PREFIX = "_interest_";
    private static final String DIRECT_CHAT_PREFIX = "_direct_chat_";
    private static final String CONNECT_PREFIX = "_connect_";
    private static final String BEGIN_PREFIX = "structure_id_";
}
