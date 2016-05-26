package com.nanodegree.gaby.bakerylovers.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import com.nanodegree.gaby.bakerylovers.R;

/**
 * Created by gaby_ on 24/5/2016.
 */

public class UpdateAmountDialogFragment extends DialogFragment {
    private UpdateAmountDialogListener mListener;
    private static final String ARG_PRODUCT_ID = "dialog_product_id";
    private static final String ARG_PRODUCT_AMOUNT = "dialog_product_amount";
    private NumberPicker mNumberPicker;
    private long mProductId;
    private int mAmount;

    public static UpdateAmountDialogFragment newInstance(long productId, int num) {
        UpdateAmountDialogFragment f = new UpdateAmountDialogFragment();

        Bundle args = new Bundle();
        args.putLong(ARG_PRODUCT_ID, productId);
        args.putInt(ARG_PRODUCT_AMOUNT, num);
        f.setArguments(args);

        return f;
    }

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface UpdateAmountDialogListener {
        public void onUpdateAmountClick(long productId, int amount);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProductId = getArguments().getLong(ARG_PRODUCT_ID);
        mAmount = getArguments().getInt(ARG_PRODUCT_AMOUNT);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (UpdateAmountDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View contentView = inflater.inflate(R.layout.dialog_update_amount_order, null);
        mNumberPicker = (NumberPicker) contentView.findViewById(R.id.numberPicker);
        mNumberPicker.setMinValue(1);
        mNumberPicker.setMaxValue(10);
        mNumberPicker.setValue(mAmount);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(contentView);
        builder.setMessage(R.string.title_update_amount_order)
                .setPositiveButton(R.string.action_update, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the positive button event back to the host activity
                        mListener.onUpdateAmountClick(mProductId, mNumberPicker.getValue());
                    }
                })
                .setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the negative button event back to the host activity
                        dismiss();
                    }
                });
        return builder.create();
    }
}
