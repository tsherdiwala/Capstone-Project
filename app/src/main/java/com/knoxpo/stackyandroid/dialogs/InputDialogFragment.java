package com.knoxpo.stackyandroid.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.knoxpo.stackyandroid.R;

/**
 * Created by khushboo on 20/1/17.
 */

public class InputDialogFragment extends DialogFragment {

    private EditText mInputET;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_input_dialog,null);
        init(v);


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        return super.onCreateDialog(savedInstanceState);
    }

    private void init(View v){

    }
}
