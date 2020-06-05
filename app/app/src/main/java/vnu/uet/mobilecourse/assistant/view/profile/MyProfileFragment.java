package vnu.uet.mobilecourse.assistant.view.profile;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import vnu.uet.mobilecourse.assistant.viewmodel.MyProfileViewModel;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.firebase.UserInfo;
import vnu.uet.mobilecourse.assistant.util.DateTimeUtils;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.Date;

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

        mViewModel.getUserInfo().observe(getViewLifecycleOwner(), new Observer<StateModel<UserInfo>>() {
            @Override
            public void onChanged(StateModel<UserInfo> stateModel) {
                switch (stateModel.getStatus()) {
                    case LOADING:
                        tvUsername.setVisibility(View.INVISIBLE);
                        tvUserId.setVisibility(View.INVISIBLE);
                        layoutDob.setVisibility(View.INVISIBLE);
                        layoutClass.setVisibility(View.INVISIBLE);

                        sflNameAndId.setVisibility(View.VISIBLE);
                        sflDobAndClass.setVisibility(View.VISIBLE);

                    case SUCCESS:
                        UserInfo userInfo = stateModel.getData();

                        tvUsername.setText(userInfo.getName());
                        tvUserId.setText(userInfo.getId());

                        Date dob = new Date(userInfo.getDOB());
                        tvDoB.setText(DateTimeUtils.DATE_FORMAT.format(dob));

                        tvClass.setText(userInfo.getUetClass());

                        tvUsername.setVisibility(View.VISIBLE);
                        tvUserId.setVisibility(View.VISIBLE);
                        layoutDob.setVisibility(View.VISIBLE);
                        layoutClass.setVisibility(View.VISIBLE);

                        sflNameAndId.setVisibility(View.INVISIBLE);
                        sflDobAndClass.setVisibility(View.INVISIBLE);

                        break;
                }
            }
        });

        return root;
    }
}
