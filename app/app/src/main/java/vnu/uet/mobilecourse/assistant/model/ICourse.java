package vnu.uet.mobilecourse.assistant.model;

import android.os.Parcelable;

public interface ICourse extends Parcelable {
    String getTitle();
    String getCode();
    int getThumbnail();
}
