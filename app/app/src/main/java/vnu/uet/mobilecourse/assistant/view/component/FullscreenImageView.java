package vnu.uet.mobilecourse.assistant.view.component;

import android.app.Dialog;
import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LifecycleOwner;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.util.AvatarLoader;
import vnu.uet.mobilecourse.assistant.view.GlideApp;

public class FullscreenImageView extends Dialog {

    private ImageView mImageView;
    private Toolbar mToolbar;
    private StorageReference mImageRef;

    public FullscreenImageView(@NonNull Context context, String title) {
        super(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

        setContentView(R.layout.dialog_img_fullscreen);
        mImageView = findViewById(R.id.imageView);

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
    }

    public void loadUser(String id, @NonNull LifecycleOwner lifecycleOwner) {
        new AvatarLoader(getContext(), lifecycleOwner).loadUser(id, mImageView);
    }

    public void loadRoom(String id, @NonNull LifecycleOwner lifecycleOwner) {
        new AvatarLoader(getContext(), lifecycleOwner).loadRoom(id, mImageView);
    }
}
