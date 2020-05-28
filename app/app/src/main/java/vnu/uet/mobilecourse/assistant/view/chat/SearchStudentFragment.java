package vnu.uet.mobilecourse.assistant.view.chat;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.database.DAO.UserInfoDAO;
import vnu.uet.mobilecourse.assistant.model.firebase.UserInfo;
import vnu.uet.mobilecourse.assistant.viewmodel.SearchStudentViewModel;
import vnu.uet.mobilecourse.assistant.viewmodel.state.IStateLiveData;
import vnu.uet.mobilecourse.assistant.viewmodel.state.StateModel;

public class SearchStudentFragment extends Fragment {

    private static final String TAG = SearchStudentFragment.class.getSimpleName();

    private SearchStudentViewModel mViewModel;
    private FragmentActivity mActivity;
    private NavController mNavController;

    private TextView mTvSearchResultTitle;
    private View mLayoutSearchResult;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mViewModel = new ViewModelProvider(this).get(SearchStudentViewModel.class);

        mActivity = getActivity();
        if (mActivity != null) {
            mNavController = Navigation.findNavController(mActivity, R.id.nav_host_fragment);
        }

        View root = inflater.inflate(R.layout.fragment_search_student, container, false);

        SearchView searchView = root.findViewById(R.id.searchView);
        searchView.onActionViewExpanded();
        searchView.setIconified(false);

        mTvSearchResultTitle = root.findViewById(R.id.tvSearchResultTitle);
        mLayoutSearchResult = root.findViewById(R.id.layoutSearchResult);

        TextView tvName = mLayoutSearchResult.findViewById(R.id.tvName);
        TextView tvId = mLayoutSearchResult.findViewById(R.id.tvId);

        hideSearchResult();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() != 8) {
                    hideSearchResult();
                    return false;
                }

                new UserInfoDAO().read(newText).observe(getViewLifecycleOwner(), new Observer<StateModel<UserInfo>>() {
                    @Override
                    public void onChanged(StateModel<UserInfo> stateModel) {
                        switch (stateModel.getStatus()) {
                            case SUCCESS:
                                showSearchResult();

                                UserInfo userInfo = stateModel.getData();
                                tvName.setText(userInfo.getName());
                                tvId.setText(userInfo.getId());

                                break;

                            case LOADING:
                                hideSearchResult();
                                Log.i(TAG, "Loading");
                                break;

                            case ERROR:
                                hideSearchResult();
                                Log.e(TAG, "Error");
                                break;
                        }
                    }
                });

                return false;
            }
        });

        Button btnChat = mLayoutSearchResult.findViewById(R.id.btnChat);
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("title", tvName.getText().toString());
                mNavController.navigate(R.id.action_navigation_search_student_to_navigation_chat_room, bundle);
            }
        });

        return root;
    }

    private void hideSearchResult() {
        mTvSearchResultTitle.setVisibility(View.GONE);
        mLayoutSearchResult.setVisibility(View.GONE);
    }

    private void showSearchResult() {
        mTvSearchResultTitle.setVisibility(View.VISIBLE);
        mLayoutSearchResult.setVisibility(View.VISIBLE);
    }
}
