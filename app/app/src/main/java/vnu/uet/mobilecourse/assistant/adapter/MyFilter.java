package vnu.uet.mobilecourse.assistant.adapter;

import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

public abstract class MyFilter<T> extends Filter {

    protected List<T> getListFromResults(FilterResults results) {
        List<T> output = new ArrayList<>();

        if (results.values instanceof List) {
            List list = (List) results.values;

            for (Object item : list) {
                if (item != null) {
                    output.add((T) item);
                }
            }
        }

        return output;
    }
}
