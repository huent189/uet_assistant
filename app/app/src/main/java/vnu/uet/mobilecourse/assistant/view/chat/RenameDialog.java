package vnu.uet.mobilecourse.assistant.view.chat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.FragmentActivity;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.util.StringConst;

public class RenameDialog extends AppCompatDialogFragment {

    private EditText mEtRoomTitle;

    private OnSubmitListener mListener;

    @NonNull
    @Override
    @SuppressLint("InflateParams")
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_rename_room_chat, null);

        mEtRoomTitle = view.findViewById(R.id.etRoomTitle);
        if (getArguments() != null) {
            String prev = getArguments().getString("title");
            mEtRoomTitle.setText(prev);
        }

        mEtRoomTitle.requestFocus();

        FragmentActivity activity = getActivity();
        assert activity != null;
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        AlertDialog alertDialog = new AlertDialog.Builder(getActivity(), R.style.Dialog)
                .setView(view)
                .setTitle("Đổi tên phòng chat")
                .setNegativeButton("Thoát", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            String text = mEtRoomTitle.getText().toString();
                            text = text.trim();
                            mListener.onSubmit(text);
                        } catch (Exception ex) {
                            Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                        } finally {
                            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        }
                    }
                })
                .setNeutralButton("Xóa", null)
                .create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button btnClear = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
                btnClear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mEtRoomTitle.setText(StringConst.EMPTY);
                    }
                });
            }
        });

        return alertDialog;
    }

    public void setOnSubmitListener(OnSubmitListener l) {
        mListener = l;
    }

    public interface OnSubmitListener {
        void onSubmit(String title);
    }
}
