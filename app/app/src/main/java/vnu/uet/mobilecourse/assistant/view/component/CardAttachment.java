package vnu.uet.mobilecourse.assistant.view.component;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.storage.StorageAccess;
import vnu.uet.mobilecourse.assistant.view.GlideApp;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;

public class CardAttachment extends CardView {

    private Context mContext;
    private LayoutInflater mInflater;

    private TextView mTvName, mTvSize;
    private ShimmerFrameLayout mSfl;

    public CardAttachment(@NonNull Context context) {
        super(context);
        mContext = context;
        initialize();
    }

    public CardAttachment(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initialize();
    }

    public CardAttachment(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initialize();
    }

    private void initialize() {
        mInflater = LayoutInflater.from(mContext);

        if (mInflater != null) {
            View root = mInflater.inflate(R.layout.card_attachment_base, this);
            root.setBackgroundResource(0);
            mTvName = root.findViewById(R.id.tvAttachmentName);
            mTvName.setVisibility(GONE);
            mTvSize = root.findViewById(R.id.tvAttachmentSize);
            mTvSize.setVisibility(GONE);
            mSfl = root.findViewById(R.id.sfl);
            mSfl.startShimmerAnimation();
            mSfl.setVisibility(VISIBLE);
        }
    }

    public void setAttachment(String path) {
        StorageReference reference = FirebaseStorage.getInstance().getReference().child(path);
        reference.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                mTvName.setText(storageMetadata.getName());
                long kbs = storageMetadata.getSizeBytes() / 1024;
                String size = kbs + "KB";
                mTvSize.setText(size);

                mSfl.setVisibility(GONE);
                mTvName.setVisibility(VISIBLE);
                mTvSize.setVisibility(VISIBLE);
            }
        });

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onDownload(path);
            }
        });
    }

    protected void onDownload(String path) {
        IStateLiveData<String> downloadState = new StorageAccess().downLoadFile(path);
        downloadState.observeForever(new Observer<StateModel<String>>() {
            @Override
            public void onChanged(StateModel<String> stateModel) {
                switch (stateModel.getStatus()) {
                    case SUCCESS:
                        Toast.makeText(mContext, "Tải về thành công", Toast.LENGTH_SHORT).show();
                        downloadState.removeObserver(this);

                        break;

                    case ERROR:
                        Toast.makeText(mContext, "Tải về thất bại", Toast.LENGTH_SHORT).show();
                        downloadState.removeObserver(this);

                        break;
                }
            }
        });
    }

}
