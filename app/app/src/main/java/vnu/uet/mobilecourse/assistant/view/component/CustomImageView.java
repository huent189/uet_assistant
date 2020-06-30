package vnu.uet.mobilecourse.assistant.view.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import de.hdodenhof.circleimageview.CircleImageView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.repository.firebase.FirebaseUserRepository;
import vnu.uet.mobilecourse.assistant.util.AvatarLoader;
import vnu.uet.mobilecourse.assistant.util.DimensionUtils;
import vnu.uet.mobilecourse.assistant.view.GlideApp;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;

public class CustomImageView extends CardView {

    private Context mContext;
    private LayoutInflater mInflater;

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
        }
    }

    public void setImage(String path) {
        StorageReference imageRef = FirebaseStorage.getInstance().getReference().child(path);
        GlideApp.with(mContext)
                .load(imageRef)
                .error(R.drawable.avatar)
                .into(mImageView);
    }
}
