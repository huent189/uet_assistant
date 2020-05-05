package vnu.uet.mobilecourse.assistant.network.response;

import com.google.gson.annotations.SerializedName;

public class LoginResponse{
    @SerializedName("privatetoken")
    private String privateToken;
    @SerializedName("token")
    private String token;

    public String getPrivateToken() {
        return privateToken;
    }

    public void setPrivateToken(String privateToken) {
        this.privateToken = privateToken;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    @Override
    public String toString() {
        return "User{" +
                "privateToken='" + privateToken + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
