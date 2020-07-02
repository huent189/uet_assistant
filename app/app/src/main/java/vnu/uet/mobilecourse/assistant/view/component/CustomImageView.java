package vnu.uet.mobilecourse.assistant.view.component;

import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import de.hdodenhof.circleimageview.CircleImageView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.repository.firebase.FirebaseUserRepository;
import vnu.uet.mobilecourse.assistant.util.AvatarLoader;
import vnu.uet.mobilecourse.assistant.util.DimensionUtils;
import vnu.uet.mobilecourse.assistant.view.GlideApp;
import vnu.uet.mobilecourse.assistant.view.chat.RenameDialog;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;

public class CustomImageView extends CardView {

    private Context mContext;
    private LayoutInflater mInflater;
    private StorageReference mImageRef;
    private ImageView mImageView;

    public CustomImageView(@NonNull Context context) {
        super(context);
        mContext = context;
        initialize();
    }

    public CustomImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initialize();
    }

    public CustomImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initialize();
    }

    private void initialize() {
        mInflater = LayoutInflater.from(mContext);

        if (mInflater != null) {
            View root = mInflater.inflate(R.layout.card_image_attachment, this);
            root.setBackgroundResource(0);
            mImageView = root.findViewById(R.id.imageView);
            mImageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    showFullscreen();
                }
            });
        }
    }

    private void showFullscreen() {
        FullscreenImageView d = new FullscreenImageView(getContext(), "Hình ảnh");

        if (mImageRef != null) {
            d.setPhotoReference(mImageRef);
            d.show();
        }
    }

    public void setImage(String path) {
        mImageRef = FirebaseStorage.getInstance().getReference().child(path);
        GlideApp.with(mContext)
                .load(mImageRef)
                .placeholder(R.drawable.transparent_button_background)
//                .skipMemoryCache(true)
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(R.drawable.avatar)
                .into(mImageView);
    }
}
