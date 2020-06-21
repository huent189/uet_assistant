package vnu.uet.mobilecourse.assistant.view.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.Date;

import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.User;
import vnu.uet.mobilecourse.assistant.model.firebase.UserInfo;
import vnu.uet.mobilecourse.assistant.util.DateTimeUtils;
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

//        mViewModel.getUserInfo().observe(getViewLifecycleOwner(), stateModel -> {
//            switch (stateModel.getStatus()) {
//                case LOADING:
//                    // hide text
//                    tvUsername.setVisibility(View.INVISIBLE);
//                    tvUserId.setVisibility(View.INVISIBLE);
//                    layoutDob.setVisibility(View.INVISIBLE);
//                    layoutClass.setVisibility(View.INVISIBLE);
//
//                    // show shimmer layout
//                    sflNameAndId.setVisibility(View.VISIBLE);
//                    sflDobAndClass.setVisibility(View.VISIBLE);
//
//                    break;
//
//                case SUCCESS:
//                    // get user info from state data
//                    UserInfo userInfo = stateModel.getData();
//
//                    // update username and student id
//                    tvUsername.setText(userInfo.getName());
//                    tvUserId.setText(userInfo.getId());
//
//                    // update day of birth
//                    Date dob = new Date(userInfo.getDOB());
//                    tvDoB.setText(DateTimeUtils.DATE_FORMAT.format(dob));
//
//                    // update uet class
//                    tvClass.setText(userInfo.getUetClass());
//
//                    // show text
//                    tvUsername.setVisibility(View.VISIBLE);
//                    tvUserId.setVisibility(View.VISIBLE);
//                    layoutDob.setVisibility(View.VISIBLE);
//                    layoutClass.setVisibility(View.VISIBLE);
//
//                    // hide shimmer layout
//                    sflNameAndId.setVisibility(View.INVISIBLE);
//                    sflDobAndClass.setVisibility(View.INVISIBLE);
//
//                    break;
//            }
//        });

        return root;
    }
}
