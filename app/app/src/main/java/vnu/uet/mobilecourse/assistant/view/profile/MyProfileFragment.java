package vnu.uet.mobilecourse.assistant.view.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.storage.StorageReference;

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.User;
import vnu.uet.mobilecourse.assistant.model.firebase.UserInfo;
import vnu.uet.mobilecourse.assistant.storage.Storage;
import vnu.uet.mobilecourse.assistant.util.DateTimeUtils;
import vnu.uet.mobilecourse.assistant.util.FileUtils;
import vnu.uet.mobilecourse.assistant.view.GlideApp;
import vnu.uet.mobilecourse.assistant.viewmodel.MyProfileViewModel;

public class MyProfileFragment extends Fragment {

    private MyProfileViewModel mViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mViewModel = new ViewModelProvider(this).get(MyProfileViewModel.class);

        View root = inflater.inflate(R.layout.fragment_my_profile, container, false);

        TextView tvDoB = root.findViewById(R.id.tvDoB);
        TextView tvClass = root.findViewById(R.id.tvClass);
        TextView tvUsername = root.findViewById(R.id.tvUsername);
        TextView tvUserId = root.findViewById(R.id.tvUserId);

        LinearLayout layoutDob = root.findViewById(R.id.layoutDob);
        LinearLayout layoutClass = root.findViewById(R.id.layoutClass);

        ShimmerFrameLayout sflNameAndId = root.findViewById(R.id.sflNameAndId);
        sflNameAndId.startShimmerAnimation();

        ShimmerFrameLayout sflDobAndClass = root.findViewById(R.id.sflDobAndClass);
        sflDobAndClass.startShimmerAnimation();

        // show text
        tvUsername.setVisibility(View.VISIBLE);
        tvUserId.setVisibility(View.VISIBLE);
        layoutDob.setVisibility(View.VISIBLE);
        layoutClass.setVisibility(View.VISIBLE);

        // hide shimmer layout
        sflNameAndId.setVisibility(View.INVISIBLE);
        sflDobAndClass.setVisibility(View.INVISIBLE);

        User user = User.getInstance();
        tvUsername.setText(user.getName());
        tvUserId.setText(user.getStudentId());
        tvClass.setText(user.getUetClass());
        Date dob = new Date(user.getDob());
        tvDoB.setText(DateTimeUtils.DATE_FORMAT.format(dob));

        Button btnChangeAvatar = root.findViewById(R.id.btnChangeAvatar);
        btnChangeAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = FileUtils.createImageIntent();
                startActivityForResult(intent, FileUtils.REQUEST_CODE_IMAGE);
            }
        });

        CircleImageView civAvatar = root.findViewById(R.id.civAvatar);

        StorageReference avatarRef = new Storage().getAvatar(User.getInstance().getStudentId());
        GlideApp.with(getContext())
                .load(avatarRef)
                .placeholder(R.drawable.avatar)
                .error(R.drawable.avatar)
                .into(civAvatar);

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case FileUtils.REQUEST_CODE_IMAGE:
                String path = data.getData().getPath();
                Toast.makeText(getContext(), path, Toast.LENGTH_SHORT).show();
                new Storage().changeAvatar(User.getInstance().getStudentId(), path);
                break;
        }
    }
}
