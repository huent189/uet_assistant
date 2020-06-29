package vnu.uet.mobilecourse.assistant.util;

import android.content.Intent;

public class FileUtils {

    public static Intent createImageIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");

        return intent;
    }

    public static Intent createIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(MIME_ALL);

        return intent;
    }

    public static Intent createIntent(String mimeType) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(mimeType);

        return intent;
    }

    public static final int REQUEST_CODE_IMAGE = 2000;
    public static final int REQUEST_CODE_FILE = 1999;

    public static final String MIME_IMAGE = "image/*";
    public static final String MIME_AUDIO = "audio/*";
    public static final String MIME_VIDEO = "video/*";
    public static final String MIME_TEXT = "text/*";
    public static final String MIME_APP = "application/*";
    public static final String MIME_ALL = "*/*";
}
