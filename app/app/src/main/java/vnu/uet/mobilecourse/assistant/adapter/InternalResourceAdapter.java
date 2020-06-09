package vnu.uet.mobilecourse.assistant.adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import vnu.uet.mobilecourse.assistant.model.Material;
import vnu.uet.mobilecourse.assistant.model.material.InternalFile;
import vnu.uet.mobilecourse.assistant.view.course.MaterialFragment;

public class InternalResourceAdapter extends ResourceAdapter {

    private List<InternalFile> mFiles;

    public InternalResourceAdapter(List<InternalFile> files, MaterialFragment owner) {
        super(owner);

        mFiles = files;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final InternalFile file = mFiles.get(position);

        holder.mTvAttachmentName.setText(file.getFileName());

        long sizeInKB = file.getFileSize() / 1024;
        holder.mTvAttachmentSize.setText(String.format(Locale.ROOT, "%dKB", sizeInKB));

        Activity activity = mOwner.getActivity();

        if (activity != null) {
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(file.getFileUrl()));
                activity.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }
}
