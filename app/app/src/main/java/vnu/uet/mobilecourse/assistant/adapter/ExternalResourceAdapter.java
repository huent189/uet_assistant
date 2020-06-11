package vnu.uet.mobilecourse.assistant.adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import androidx.annotation.NonNull;
import vnu.uet.mobilecourse.assistant.model.Material;
import vnu.uet.mobilecourse.assistant.view.course.MaterialFragment;

public class ExternalResourceAdapter extends ResourceAdapter {

    private String mAttachmentName;
    private String mAttachmentUrl;

    public ExternalResourceAdapter(Material material, MaterialFragment owner) {
        super(owner);

        mAttachmentName = material.getFileName();
        mAttachmentUrl = material.getFileUrl();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mTvAttachmentName.setText(mAttachmentName);
        holder.mTvAttachmentSize.setVisibility(View.INVISIBLE);

        Activity activity = mOwner.getActivity();

        if (activity != null) {
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mAttachmentUrl));
                activity.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return 1;
    }
}
