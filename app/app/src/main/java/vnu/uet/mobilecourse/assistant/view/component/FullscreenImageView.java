package vnu.uet.mobilecourse.assistant.view.component;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.storage.StorageAccess;
import vnu.uet.mobilecourse.assistant.util.AvatarLoader;
import vnu.uet.mobilecourse.assistant.view.GlideApp;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;

public class FullscreenImageView extends Dialog {

    private ImageView mImageView;
    private Toolbar mToolbar;
    private ImageButton mBtnDownload;
    private StorageReference mImageRef;

    public FullscreenImageView(@NonNull Context context, String title) {
        super(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

        setContentView(R.layout.dialog_img_fullscreen);
        mImageView = findViewById(R.id.imageView);
        mBtnDownload = findViewById(R.id.btnDownload);

        initializeToolbar(title);
    }

    private void initializeToolbar(String title) {
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle(title);
    }

    public void setPhotoReference(@NonNull StorageReference reference) {
        mImageRef = reference;

        GlideApp.with(getContext())
                .load(mImageRef)
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .skipMemoryCache(true)
                .into(mImageView);

        setupButtonDownloadWith(reference);
    }

    public void loadUser(String id, @NonNull LifecycleOwner lifecycleOwner) {
        new AvatarLoader(getContext(), lifecycleOwner).loadUser(id, mImageView);

        setupButtonDownloadWith(new StorageAccess().getAvatar(id));
    }

    public void loadRoom(String id, @NonNull LifecycleOwner lifecycleOwner) {
        new AvatarLoader(getContext(), lifecycleOwner).loadRoom(id, mImageView);

        setupButtonDownloadWith(new StorageAccess().getAvatar(id));
    }

    private void setupButtonDownloadWith(StorageReference reference) {
        mBtnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = reference.getPath();
                download(path);
            }
        });
    }

    private void download(String cloudPath) {
        IStateLiveData<String> downloadState = new StorageAccess().downLoadFile(cloudPath);
        downloadState.observeForever(new Observer<StateModel<String>>() {
            @Override
            public void onChanged(StateModel<String> stateModel) {
                switch (stateModel.getStatus()) {
                    case SUCCESS:
                        Toast.makeText(getContext(), "Tải về thành công", Toast.LENGTH_SHORT).show();
                        downloadState.removeObserver(this);

                        break;

                    case ERROR:
                        Toast.makeText(getContext(), "Tải về thất bại", Toast.LENGTH_SHORT).show();
                        downloadState.removeObserver(this);

                        break;
                }
            }
        });
    }
}
