package com.example.labcoc;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;



public class infoEntry extends AppCompatDialogFragment {
    private EditText emailText;
    private EditText passwordText;
    private InfoEntryListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog, null);

        builder.setView(view)
                .setTitle("Login")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String email = emailText.getText().toString();
                        String password = passwordText.getText().toString();

                        listener.onRecieveInfo(email, password);
                    }
                });

        emailText = view.findViewById(R.id.emailTextBox);
        passwordText = view.findViewById(R.id.passwordTextBox);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (InfoEntryListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "Must implement InfoEntryListener");
        }
    }

    public interface InfoEntryListener {
        void onRecieveInfo(String email, String password);
    }
}
