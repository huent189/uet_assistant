package vnu.uet.mobilecourse.assistant.adapter;

import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import vnu.uet.mobilecourse.assistant.R;

public abstract class ResourceAdapter extends RecyclerView.Adapter<ResourceAdapter.ViewHolder> {

    protected Context mContext;

    protected ResourceAdapter(Context context) {
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
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
