package vnu.uet.mobilecourse.assistant.network.response;

import com.google.gson.annotations.SerializedName;

public abstract class CoursesResponse {
    @SerializedName("errorcode")
    private String errorCode = null;
    public boolean isSuccess(){
        return errorCode == null;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
