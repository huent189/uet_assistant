package vnu.uet.mobilecourse.assistant.view.profile;

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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import vnu.uet.mobilecourse.assistant.R;
import vnu.uet.mobilecourse.assistant.util.StringConst;

public class ChangeAvatarDialog extends AppCompatDialogFragment {

    private IAvatarChangableFragment mFragment;

    public ChangeAvatarDialog(IAvatarChangableFragment fragment) {
        mFragment = fragment;
    }

    @NonNull
    @Override
    @SuppressLint("InflateParams")
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.menu_choose_avatar, null);

        Button btnLibrary = view.findViewById(R.id.btnLibrary);
        btnLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragment.chooseFromLibrary();
                dismiss();
            }
        });

        Button btnCamera = view.findViewById(R.id.btnCamera);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragment.chooseFromCamera();
                dismiss();
            }
        });

        AlertDialog alertDialog = new AlertDialog.Builder(getActivity(), R.style.Dialog)
                .setView(view)
                .create();

        return alertDialog;
    }
}
