package vnu.uet.mobilecourse.assistant.view.course;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import vnu.uet.mobilecourse.assistant.viewmodel.MaterialViewModel;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.Material;

import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MaterialFragment extends Fragment {

    private MaterialViewModel mViewModel;

    private FragmentActivity activity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_material, container, false);

        mViewModel = new ViewModelProvider(this).get(MaterialViewModel.class);

        activity = getActivity();

        Toolbar toolbar = initializeToolbar(root);

        TextView tvHtml = root.findViewById(R.id.tvHtml);

        TextView tvAttachment = root.findViewById(R.id.tvAttachment);

        TextView tvStatus = root.findViewById(R.id.tvStatus);

        Bundle args = getArguments();
        if (args != null && toolbar != null) {
            // get material from bundle arguments
            Material material = args.getParcelable("material");

            if (material != null) {
                String title = material.getTitle();

                // setup toolbar title
                toolbar.setTitle(title);

                // change status text view if task hasn't complete
                if (material.getCompletion() != 1) {
                    tvStatus.setText(R.string.material_status_uncomplete);
                    tvStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                            R.drawable.ic_unchecked_circle_24dp, 0);
                }

                // get attachment
                String attachment = material.getFileName();
                if (attachment != null && !attachment.isEmpty()) {
                    tvAttachment.setText(material.getFileName());
                    tvAttachment.setOnClickListener(v -> {
                        String fileUrl = material.getFileUrl();
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(fileUrl));
                        activity.startActivity(intent);
                    });
                }

                // setup html content description
                String html = material.getDescription();
                if (html != null) {
                    SpannableStringBuilder strBuilder = mViewModel.convertHtml(html);
                    tvHtml.setText(strBuilder);
                    tvHtml.setLinksClickable(true);
                    tvHtml.setMovementMethod(LinkMovementMethod.getInstance());
                }
            }
        }

        return root;
    }

    private Toolbar initializeToolbar(View root) {
        if (activity instanceof AppCompatActivity) {
            Toolbar toolbar = root.findViewById(R.id.toolbar);

            ((AppCompatActivity) activity).setSupportActionBar(toolbar);
            setHasOptionsMenu(true);

            return toolbar;
        }

        return null;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MaterialViewModel.class);
        // TODO: Use the ViewModel
    }
}
