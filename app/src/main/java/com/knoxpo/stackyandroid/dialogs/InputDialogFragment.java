package com.knoxpo.stackyandroid.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.knoxpo.stackyandroid.R;

import java.util.List;

/**
 * Created by khushboo on 20/1/17.
 */

public class InputDialogFragment extends DialogFragment implements DialogInterface.OnClickListener, DialogInterface.OnShowListener, View.OnClickListener {

    private static final String
            TAG = InputDialogFragment.class.getSimpleName(),
            ARGS_HINT_ID = TAG + ".ARGS_HINT_ID",
            ARGS_INPUT_TYPE = TAG + ".ARGS_INPUT_TYPE";

    public static final String
            EXTRA_INPUT = TAG + ".EXTRA_INPUT";

    private static final String
            DOMAIN_REGEX = "(?i)(www.)?stackoverflow.com",
            QUESTION_REGEX = "(?i)q|questions",
            QUESTION_ID_REGEX = "([1-9])+([0-9])*",
            SCHEME = "(http|https)://(.*)",
            SCHEME_PREFIX = "http://";


    private EditText mInputET;
    private TextInputLayout mTextInputLayout;

    public static InputDialogFragment newInstance(int hintId, int inputType) {

        Bundle args = new Bundle();
        args.putInt(ARGS_HINT_ID, hintId);
        args.putInt(ARGS_INPUT_TYPE, inputType);

        InputDialogFragment fragment = new InputDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_input_dialog, null);
        init(v);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setPositiveButton(android.R.string.ok, this);
        builder.setNegativeButton(android.R.string.cancel, this);
        builder.setView(v);

        Bundle arguments = getArguments();

        mTextInputLayout.setHint(getString(arguments.getInt(ARGS_HINT_ID)));
        mInputET.setInputType(arguments.getInt(ARGS_INPUT_TYPE));

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(this);

        return dialog;
    }

    private void init(View v) {
        mInputET = (EditText) v.findViewById(R.id.et_input);
        mTextInputLayout = (TextInputLayout) mInputET.getParent().getParent();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            onPositiveButtonClicked();
        } else {
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, null);
        }
    }

    @Override
    public void onClick(View v) {
        onPositiveButtonClicked();
    }

    private void onPositiveButtonClicked() {
        String input = mInputET.getText().toString();

        if(!input.matches(SCHEME)){
            input = SCHEME_PREFIX+input;
        }


        boolean isValid = true;
        String questionId = null;

        Uri uri = Uri.parse(input);
        List<String> pathSegments = uri.getPathSegments();
        try {

            String domain = uri.getAuthority();
            String question = pathSegments.get(0);
            questionId = pathSegments.get(1);

            if (!domain.matches(DOMAIN_REGEX)
                    || !questionId.matches(QUESTION_ID_REGEX)
                    || !question.matches(QUESTION_REGEX)) {
                throw new IllegalArgumentException();
            }
        } catch (NullPointerException | IndexOutOfBoundsException | IllegalArgumentException e) {
            isValid = false;
        }

        if (questionId != null && isValid) {
            Intent data = new Intent();
            data.putExtra(EXTRA_INPUT, questionId);
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, data);
            dismiss();
        } else {
            mTextInputLayout.setError(
                    getString(R.string.error_invalid_url)
            );
        }
    }

    @Override
    public void onShow(DialogInterface dialog) {
        Button button = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);
        button.setOnClickListener(this);
    }


}