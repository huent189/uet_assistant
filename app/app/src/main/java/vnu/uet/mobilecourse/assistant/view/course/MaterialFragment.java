package vnu.uet.mobilecourse.assistant.view.course;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import vnu.uet.mobilecourse.assistant.model.material.AssignmentContent;
import vnu.uet.mobilecourse.assistant.model.material.MaterialContent;
import vnu.uet.mobilecourse.assistant.util.DateTimeUtils;
import vnu.uet.mobilecourse.assistant.viewmodel.MaterialViewModel;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.model.Material;

import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Date;

public class MaterialFragment extends Fragment {

    private MaterialViewModel mViewModel;

    private FragmentActivity mActivity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_material, container, false);

        mViewModel = new ViewModelProvider(this).get(MaterialViewModel.class);

        mActivity = getActivity();

        Toolbar toolbar = initializeToolbar(root);

        TextView tvHtml = root.findViewById(R.id.tvHtml);

        TextView tvAttachment = root.findViewById(R.id.tvAttachment);

        TextView tvStatus = root.findViewById(R.id.tvStatus);

        TextView tvModifyTime = root.findViewById(R.id.tvModifyTime);

        TextView tvStartTime = root.findViewById(R.id.tvStartTime);
        TextView tvDeadline = root.findViewById(R.id.tvDeadline);
        TextView tvMaxGrade = root.findViewById(R.id.tvMaxGrade);
        TextView tvMaxAttempt = root.findViewById(R.id.tvMaxAttempt);

        Bundle args = getArguments();
        if (args != null && toolbar != null) {
            // get material from bundle arguments
            Material material = args.getParcelable("material");

            if (material != null) {
                String title = material.getTitle();

                int materialId = material.getId();
                String type = material.getType();

                mViewModel.getDetailContent(materialId, type).observe(getViewLifecycleOwner(), new Observer<MaterialContent>() {
                    @Override
                    public void onChanged(MaterialContent content) {
                        if (content != null) {
                            Date modifyTime = DateTimeUtils.fromSecond(content.getTimeModified());
                            tvModifyTime.setText(DateTimeUtils.DATE_TIME_FORMAT.format(modifyTime));

                            String intro = content.getIntro();
                            if (intro != null) {
                                SpannableStringBuilder strBuilder = mViewModel.convertHtml(intro);
                                tvHtml.setText(strBuilder);
                                tvHtml.setLinksClickable(true);
                                tvHtml.setMovementMethod(LinkMovementMethod.getInstance());
                            }

                            if (content instanceof AssignmentContent) {
                                AssignmentContent assignmentContent = (AssignmentContent) content;

                                Date startTime = DateTimeUtils.fromSecond(assignmentContent.getStartDate());
                                tvStartTime.setText(DateTimeUtils.DATE_TIME_FORMAT.format(startTime));

                                Date deadline = DateTimeUtils.fromSecond(assignmentContent.getDeadline());
                                tvDeadline.setText(DateTimeUtils.DATE_TIME_FORMAT.format(deadline));

                                String maxGrade = String.valueOf(assignmentContent.getMaximumGrade());
                                tvMaxGrade.setText(maxGrade);

                                int maxAttempt = assignmentContent.getMaxAttemptAllowed();
                                if (maxAttempt <= 0) tvMaxAttempt.setText("Không thể làm bài");
                                else tvMaxAttempt.setText(String.valueOf(maxAttempt));
                            }
                        }
                    }
                });

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
                        mActivity.startActivity(intent);
                    });
                }

                // setup html content description
//                String html = material.getDescription();
//                if (html != null) {
//                    SpannableStringBuilder strBuilder = mViewModel.convertHtml(html);
//                    tvHtml.setText(strBuilder);
//                    tvHtml.setLinksClickable(true);
//                    tvHtml.setMovementMethod(LinkMovementMethod.getInstance());
//                }
            }
        }

        return root;
    }

    private Toolbar initializeToolbar(View root) {
        if (mActivity instanceof AppCompatActivity) {
            Toolbar toolbar = root.findViewById(R.id.toolbar);

            ((AppCompatActivity) mActivity).setSupportActionBar(toolbar);
            setHasOptionsMenu(true);

            return toolbar;
        }

        return null;
    }
}
