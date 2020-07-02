package vnu.uet.mobilecourse.assistant.view.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import com.facebook.shimmer.ShimmerFrameLayout;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.User;
import vnu.uet.mobilecourse.assistant.util.DateTimeUtils;
import vnu.uet.mobilecourse.assistant.util.FileUtils;
import vnu.uet.mobilecourse.assistant.view.MainActivity;
import vnu.uet.mobilecourse.assistant.view.component.AvatarView;
import vnu.uet.mobilecourse.assistant.view.component.FullscreenImageView;
import vnu.uet.mobilecourse.assistant.viewmodel.MyProfileViewModel;

import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class MyProfileFragment extends Fragment implements IAvatarChangableFragment {

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
                showMenuDialog();
            }
        });

        Button btnLogout = root.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.signOut();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        AvatarView avatarView = root.findViewById(R.id.avatarView);
        avatarView.setLifecycleOwner(getViewLifecycleOwner());
        avatarView.loadUser(User.getInstance().getStudentId());

        avatarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAvatarFullscreen();
            }
        });

        initializeToolbar(root);

        return root;
    }

    private void initializeToolbar(View root) {
        FragmentActivity mActivity = getActivity();

        if (mActivity instanceof AppCompatActivity) {
            Toolbar toolbar = root.findViewById(R.id.toolbar);

            ((AppCompatActivity) mActivity).setSupportActionBar(toolbar);
        }
    }

    @Override
    public void showMenuDialog() {
        ChangeAvatarDialog dialog = new ChangeAvatarDialog(this);
        dialog.show(getActivity().getSupportFragmentManager(), ChangeAvatarDialog.class.getName());
    }

    private void showAvatarFullscreen() {
        FullscreenImageView d = new FullscreenImageView(getContext(), "Ảnh đại diện");

        d.loadUser(User.getInstance().getStudentId(), getViewLifecycleOwner());
        d.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case FileUtils.REQUEST_CODE_IMAGE:
                if (resultCode == RESULT_OK && data != null) {
                    Uri uri = data.getData();
                    Toast.makeText(getContext(), uri.getPath(), Toast.LENGTH_SHORT).show();
                    mViewModel.changeAvatarFromFile(uri);
                }
                break;

            case FileUtils.REQUEST_CODE_CAMERA:
                if (resultCode == RESULT_OK && data != null) {
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    mViewModel.changeAvatarFromCamera(photo);
                }
                break;
        }
    }

    @Override
    public void chooseFromLibrary() {
        Intent intent = FileUtils.createImageIntent();
        startActivityForResult(intent, FileUtils.REQUEST_CODE_IMAGE);
    }

    @Override
    public void chooseFromCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, FileUtils.REQUEST_CODE_CAMERA);
    }
}
