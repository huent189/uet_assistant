package vnu.uet.mobilecourse.assistant.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.firebase.GroupChat_UserSubCol;
import vnu.uet.mobilecourse.assistant.model.firebase.User;
import vnu.uet.mobilecourse.assistant.repository.firebase.ChatRepository;
import vnu.uet.mobilecourse.assistant.repository.firebase.FirebaseUserRepository;
import vnu.uet.mobilecourse.assistant.storage.StorageAccess;
import vnu.uet.mobilecourse.assistant.view.GlideApp;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateStatus;

public class AvatarLoader {

    private Context mContext;
    private LifecycleOwner mLifecycleOwner;

    public AvatarLoader(@NonNull Context context, @NonNull LifecycleOwner lifecycleOwner) {
        mContext = context.getApplicationContext();
        mLifecycleOwner = lifecycleOwner;
    }


    public void loadUser(String studentCode, ImageView imageView) {
        StorageReference imageRef = new StorageAccess().getAvatar(studentCode);

        IStateLiveData<User> liveData = FirebaseUserRepository.getInstance().search(studentCode);

        if (liveData.getValue() != null && liveData.getValue().getStatus() == StateStatus.SUCCESS) {
            GlideApp.with(mContext)
                    .load(imageRef)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
//                                            .signature(new ObjectKey(time))
//                    .placeholder(R.drawable.avatar)
                    .error(R.drawable.avatar)
                    .into(imageView);
        } else {
            GlideApp.with(mContext)
                    .load(imageRef)
//                    .placeholder(R.drawable.avatar)
                    .error(R.drawable.avatar)
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .skipMemoryCache(true)
                    .into(imageView);
        }

        FirebaseUserRepository.getInstance().search(studentCode)
                .observe(mLifecycleOwner, new Observer<StateModel<User>>() {
                    @Override
                    public void onChanged(StateModel<User> stateModel) {
                        switch (stateModel.getStatus()) {
                            case SUCCESS:
                                if (NetworkUtils.getConnectivityStatus(mContext) != NetworkUtils.TYPE_NOT_CONNECTED) {
                                    long time = stateModel.getData().getAvatar();
                                    GlideApp.with(mContext)
                                            .load(imageRef)
                                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                                            .skipMemoryCache(true)
//                                            .signature(new ObjectKey(time))
//                                            .placeholder(R.drawable.avatar)
                                            .error(R.drawable.avatar)
                                            .into(imageView);
                                }
                        }
                    }
                });


    }

    public void loadRoom(String roomId, ImageView imageView) {
        StorageReference imageRef = new StorageAccess().getAvatar(roomId);

        ChatRepository.getInstance().getUserGroupChat(roomId)
                .observe(mLifecycleOwner, new Observer<StateModel<GroupChat_UserSubCol>>() {
                    @Override
                    public void onChanged(StateModel<GroupChat_UserSubCol> stateModel) {
                        switch (stateModel.getStatus()) {
                            case SUCCESS:
                                long time = stateModel.getData().getAvatar();
                                GlideApp.with(mContext)
                                        .load(imageRef)
                                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                                        .skipMemoryCache(true)
//                                        .signature(new ObjectKey(time))
                                        .error(R.drawable.img_admin_bot)
                                        .into(imageView);
                        }
                    }
                });

        GlideApp.with(mContext)
                .load(imageRef)
                .error(R.drawable.img_admin_bot)
                .into(imageView);
    }

}
