package vnu.uet.mobilecourse.assistant.network;

import java.util.HashMap;
import java.util.Map;

public class EncodedArray {
    private String arrayName;

    public EncodedArray(String arrayName) {
        this.arrayName = arrayName;
    }

    HashMap<String, String> opts = new HashMap<>();
    public void put(String key, String value) {
        opts.put(key, value);
    }

    public HashMap<String, String> getOpts() {
        HashMap<String, String> params = new HashMap<>();
        int i = 0;
        for (Map.Entry<String, String> entry: opts.entrySet()) {
            params.put(arrayName + "["+i+"][key]", entry.getKey());
            params.put(arrayName + "["+i+"][value]", entry.getValue());
            i++;
        }
        return params;
    }
}
