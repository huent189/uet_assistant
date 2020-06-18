package vnu.uet.mobilecourse.assistant.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import vnu.uet.mobilecourse.assistant.model.Material;
import vnu.uet.mobilecourse.assistant.model.material.InternalFile;
import vnu.uet.mobilecourse.assistant.view.course.MaterialFragment;

public class InternalResourceAdapter extends ResourceAdapter {

    private List<InternalFile> mFiles;

    public InternalResourceAdapter(List<InternalFile> files, Context context) {
        super(context);

        mFiles = files;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final InternalFile file = mFiles.get(position);

        Log.d(getClass().getSimpleName(), "onBindViewHolder: " + file.toString());

        holder.mTvAttachmentName.setText(file.getFileName());

        long sizeInKB = file.getFileSize() / 1024;
        holder.mTvAttachmentSize.setText(String.format(Locale.ROOT, "%dKB", sizeInKB));

//        Activity activity = mContext.getA();

        if (mContext != null) {
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(file.getFileUrl()));
                mContext.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }
}
