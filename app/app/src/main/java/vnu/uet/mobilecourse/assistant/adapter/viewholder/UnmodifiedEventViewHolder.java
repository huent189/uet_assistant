package vnu.uet.mobilecourse.assistant.adapter.viewholder;

import android.view.View;

import androidx.annotation.NonNull;

public class UnmodifiedEventViewHolder extends EventViewHolder {

    public UnmodifiedEventViewHolder(@NonNull View itemView) {
        super(itemView);

        mCbDone.setActivated(false);
        mCbDone.setClickable(false);
    }
}