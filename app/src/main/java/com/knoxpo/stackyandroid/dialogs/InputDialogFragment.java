package com.knoxpo.stackyandroid.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.knoxpo.stackyandroid.R;

/**
 * Created by khushboo on 20/1/17.
 */

public class InputDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {

    private static final String
            TAG = InputDialogFragment.class.getSimpleName(),
            ARGS_HINT_ID = TAG + ".ARGS_HINT_ID",
            ARGS_INPUT_TYPE = TAG + ".ARGS_INPUT_TYPE";

    public static final String
            EXTRA_INPUT = TAG + ".EXTRA_INPUT";

    private EditText mInputET;

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

        mInputET.setHint(arguments.getInt(ARGS_HINT_ID));
        mInputET.setInputType(arguments.getInt(ARGS_INPUT_TYPE));

       return builder.create();
    }

    private void init(View v) {
        mInputET = (EditText) v.findViewById(R.id.et_input);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if(which == DialogInterface.BUTTON_POSITIVE){
            Intent data = new Intent();
            data.putExtra(EXTRA_INPUT,mInputET.getText().toString());
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK,data);
        }else{
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED,null);
        }
    }
}