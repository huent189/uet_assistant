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

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import vnu.uet.mobilecourse.assistant.adapter.ExternalResourceAdapter;
import vnu.uet.mobilecourse.assistant.adapter.InternalResourceAdapter;
import vnu.uet.mobilecourse.assistant.model.material.AssignmentContent;
import vnu.uet.mobilecourse.assistant.model.material.CourseConstant;
import vnu.uet.mobilecourse.assistant.model.material.ExternalResourceContent;
import vnu.uet.mobilecourse.assistant.model.material.InternalFile;
import vnu.uet.mobilecourse.assistant.model.material.InternalResourceContent;
import vnu.uet.mobilecourse.assistant.model.material.MaterialContent;
import vnu.uet.mobilecourse.assistant.model.material.QuizNoGrade;
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
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Date;
import java.util.List;
import java.util.Locale;

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
        TextView tvStartTimeTitle = root.findViewById(R.id.tvStartTimeTitle);

        TextView tvDeadline = root.findViewById(R.id.tvDeadline);
        TextView tvDeadlineTitle = root.findViewById(R.id.tvDeadlineTitle);

        TextView tvLimitTime = root.findViewById(R.id.tvLimitTime);
        TextView tvLimitTimeTitle = root.findViewById(R.id.tvLimitTimeTitle);

        LinearLayout layoutGradeAndAttempt = root.findViewById(R.id.layoutGradeAndAttempt);
        TextView tvMaxGrade = root.findViewById(R.id.tvMaxGrade);
        TextView tvMaxAttempt = root.findViewById(R.id.tvMaxAttempt);

        RecyclerView rvAttachments = root.findViewById(R.id.rvAttachments);

        LinearLayoutManager layoutManager = new LinearLayoutManager(
                getContext(),
                LinearLayoutManager.HORIZONTAL,
                false);

        rvAttachments.setLayoutManager(layoutManager);

        Bundle args = getArguments();
        if (args != null && toolbar != null) {
            // get material from bundle arguments
            Material material = args.getParcelable("material");

            if (material != null) {
                String title = material.getTitle();

                int materialId = material.getId();
                String type = material.getType();

                switch (type) {
                    case CourseConstant.MaterialType.ASSIGN:
                        tvStartTimeTitle.setVisibility(View.VISIBLE);
                        tvStartTime.setVisibility(View.VISIBLE);

                        tvDeadlineTitle.setVisibility(View.VISIBLE);
                        tvDeadline.setVisibility(View.VISIBLE);

                        layoutGradeAndAttempt.setVisibility(View.VISIBLE);
                        break;

                    case CourseConstant.MaterialType.QUIZ:
                        tvStartTimeTitle.setVisibility(View.VISIBLE);
                        tvStartTime.setVisibility(View.VISIBLE);

                        tvDeadlineTitle.setVisibility(View.VISIBLE);
                        tvDeadline.setVisibility(View.VISIBLE);

                        tvLimitTime.setVisibility(View.VISIBLE);
                        tvLimitTimeTitle.setVisibility(View.VISIBLE);

                        layoutGradeAndAttempt.setVisibility(View.VISIBLE);
                        break;
                }

                mViewModel.getDetailContent(materialId, type).observe(getViewLifecycleOwner(), new Observer<MaterialContent>() {
                    @Override
                    public void onChanged(MaterialContent content) {
                        if (content != null) {
                            Date modifyTime = DateTimeUtils.fromSecond(content.getTimeModified());
                            tvModifyTime.setText(DateTimeUtils.DATE_TIME_FORMAT.format(modifyTime));

                            String intro = content.getIntro();
                            if (intro != null) {
                                SpannableStringBuilder strBuilder = mViewModel.convertHtml(intro);

                                if (strBuilder.length() > 0) {
                                    tvHtml.setText(strBuilder);
                                    tvHtml.setLinksClickable(true);
                                    tvHtml.setMovementMethod(LinkMovementMethod.getInstance());
                                }
                            }

                            if (content instanceof AssignmentContent) {
                                AssignmentContent assignment = (AssignmentContent) content;

                                Date startTime = DateTimeUtils.fromSecond(assignment.getStartDate());
                                tvStartTime.setText(DateTimeUtils.DATE_TIME_FORMAT.format(startTime));

                                Date deadline = DateTimeUtils.fromSecond(assignment.getDeadline());
                                tvDeadline.setText(DateTimeUtils.DATE_TIME_FORMAT.format(deadline));

                                String maxGrade = String.valueOf(assignment.getMaximumGrade());
                                tvMaxGrade.setText(maxGrade);

                                int maxAttempt = assignment.getMaxAttemptAllowed();
                                if (maxAttempt <= 0) {
                                    tvMaxAttempt.setText(R.string.material_assignment_no_attempt_title);
                                } else {
                                    tvMaxAttempt.setText(String.valueOf(maxAttempt));
                                }

                            } else if (content instanceof QuizNoGrade) {
                                QuizNoGrade quiz = (QuizNoGrade) content;

                                Date startTime = DateTimeUtils.fromSecond(quiz.getTimeOpen());
                                tvStartTime.setText(DateTimeUtils.DATE_TIME_FORMAT.format(startTime));

                                Date deadline = DateTimeUtils.fromSecond(quiz.getTimeClose());
                                tvDeadline.setText(DateTimeUtils.DATE_TIME_FORMAT.format(deadline));

                                String limitTime = String.format(Locale.ROOT, "%d phút", quiz.getTimeLimit() / 60);
                                tvLimitTime.setText(limitTime);

                                String maxGrade = String.valueOf(quiz.getMaximumGrade());
                                tvMaxGrade.setText(maxGrade);

                                int maxAttempt = quiz.getMaximumAttempt();
                                if (maxAttempt <= 0) {
                                    tvMaxAttempt.setText(R.string.material_assignment_no_attempt_title);
                                } else {
                                    tvMaxAttempt.setText(String.valueOf(maxAttempt));
                                }

                            } else if (content instanceof InternalResourceContent) {
                                InternalResourceContent internalResource = (InternalResourceContent) content;

                                List<InternalFile> files = internalResource.getFiles();

                                if (files != null) {
                                    InternalResourceAdapter adapter = new InternalResourceAdapter(files, MaterialFragment.this);
                                    rvAttachments.setAdapter(adapter);
                                }

                            } else if (content instanceof ExternalResourceContent) {
                                ExternalResourceAdapter adapter = new ExternalResourceAdapter(material, MaterialFragment.this);
                                rvAttachments.setAdapter(adapter);
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
