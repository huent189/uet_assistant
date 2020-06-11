package vnu.uet.mobilecourse.assistant.adapter;

import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.view.course.MaterialFragment;

public abstract class ResourceAdapter extends RecyclerView.Adapter<ResourceAdapter.ViewHolder> {

    protected MaterialFragment mOwner;

    protected ResourceAdapter(MaterialFragment owner) {
        this.mOwner = owner;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mOwner.getLayoutInflater()
                .inflate(R.layout.card_attachment, parent, false);

        return new ViewHolder(view);
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView mTvAttachmentName;
        TextView mTvAttachmentSize;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            mTvAttachmentName = itemView.findViewById(R.id.tvAttachmentName);
            mTvAttachmentName.setMovementMethod(new ScrollingMovementMethod());

            mTvAttachmentSize = itemView.findViewById(R.id.tvAttachmentSize);
        }
    }
}
