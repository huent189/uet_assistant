package vnu.uet.mobilecourse.assistant.view.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import de.hdodenhof.circleimageview.CircleImageView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.firebase.User;
import vnu.uet.mobilecourse.assistant.network.error_detector.NetworkUtil;
import vnu.uet.mobilecourse.assistant.repository.firebase.FirebaseUserRepository;
import vnu.uet.mobilecourse.assistant.util.AvatarLoader;
import vnu.uet.mobilecourse.assistant.util.DimensionUtils;
import vnu.uet.mobilecourse.assistant.util.NetworkUtils;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;

public class AvatarView extends FrameLayout {

    private Context mContext;
    private LayoutInflater mInflater;
    private LifecycleOwner mLifecycleOwner;

    private CircleImageView mCivAvatar;
    private ImageView mIvStatus;

    private AvatarLoader mLoader;

    private boolean mShowStatus;

    public AvatarView(@NonNull Context context) {
        super(context);
        mContext = context;
        initialize();
    }

    public AvatarView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initialize();
        setBorder(attrs);
    }

    public AvatarView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initialize();
        setBorder(attrs);
    }

    public AvatarView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        initialize();
        setBorder(attrs);
    }

    private void initialize() {
        mInflater = LayoutInflater.from(mContext);

        if (mInflater != null) {
            View root = mInflater.inflate(R.layout.layout_avatar, this);

            mCivAvatar = root.findViewById(R.id.civAvatar);
            mIvStatus = root.findViewById(R.id.ivStatus);

//            setBorder();
        }
    }

    public void setLifecycleOwner(LifecycleOwner mLifecycleOwner) {
        this.mLifecycleOwner = mLifecycleOwner;
        mLoader = new AvatarLoader(mContext, mLifecycleOwner);
    }

    public void setBorder(AttributeSet attrs) {
        TypedArray ta = mContext.obtainStyledAttributes(attrs, R.styleable.AvatarView, 0, 0);
        try {
            int defaultVal = DimensionUtils.dpToPx(2, mContext);
            int statusPadding = (int) ta.getDimension(R.styleable.AvatarView_borderWidth, defaultVal);
            mIvStatus.setPaddingRelative(statusPadding, statusPadding, statusPadding, statusPadding);

            mShowStatus = ta.getBoolean(R.styleable.AvatarView_showStatus, true);
        } finally {
            ta.recycle();
        }
    }

    public void setDefault() {
        mCivAvatar.setImageResource(R.drawable.avatar);
        mIvStatus.setVisibility(GONE);
    }

    public void loadUser(String id) {
        if (mLoader == null)
            return;

        mLoader.loadUser(id, mCivAvatar);

        if (mShowStatus) loadStatus(id);
        else mIvStatus.setVisibility(GONE);
    }

    public void loadRoom(String id) {
        if (mLoader == null)
            return;

        mLoader.loadRoom(id, mCivAvatar);
        mIvStatus.setVisibility(GONE);
    }

    public void loadStatus(String id) {
//        FirebaseUserRepository.getInstance().search(id).observe(mLifecycleOwner, new Observer<StateModel<User>>() {
//            @Override
//            public void onChanged(StateModel<User> stateModel) {
//                switch (stateModel.getStatus()) {
//                    case LOADING:
//                    case ERROR:
//                        mIvStatus.setVisibility(GONE);
//                        break;
//
//                    case SUCCESS:
//                        int networkStatus = NetworkUtils.getConnectivityStatus(getContext());
//                        boolean isOnline = stateModel.getData().isOnline()
//                                && networkStatus != NetworkUtils.TYPE_NOT_CONNECTED;
//                        mIvStatus.setVisibility(isOnline ? VISIBLE : GONE);
//                        break;
//                }
//            }
//        });

        FirebaseUserRepository.getInstance().onlineState(id).observe(mLifecycleOwner, new Observer<StateModel<Boolean>>() {
            @Override
            public void onChanged(StateModel<Boolean> stateModel) {
                switch (stateModel.getStatus()) {
                    case LOADING:
                    case ERROR:
                        mIvStatus.setVisibility(GONE);
                        break;

                    case SUCCESS:
                        mIvStatus.setVisibility(stateModel.getData() ? VISIBLE : GONE);
                        break;
                }
            }
        });
    }
}
