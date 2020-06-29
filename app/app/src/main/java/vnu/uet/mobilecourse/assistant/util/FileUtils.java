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
        intent.setType("*/*");

        return intent;
    }

    public static Intent createIntent(String mimeType) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(mimeType);

        return intent;
    }

    public static final int REQUEST_CODE_IMAGE = 2000;
    public static final int REQUEST_CODE_FILE = 1999;
}
