package com.dglasser.intellidrive.CustomDialogs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.dglasser.intellidrive.POJO.StringPair;
import com.dglasser.intellidrive.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TripDialogFragment extends DialogFragment {

    private OnFragmentDismissedListener listener;

    /**
     * Cancels fragment.
     */
    @BindView(R.id.cancel_button) Button cancelButton;

    /**
     * Button start trip.
     */
    @BindView(R.id.start_button) Button startButton;

    /**
     *
     */
    @BindView(R.id.trip_name_field) TextInputEditText tripNameField;

    @BindView(R.id.radio_group) RadioGroup radioGroup;

    @BindView(R.id.personal_radio) RadioButton personalRadio;
    @BindView(R.id.business_radio) RadioButton businessRadio;
    @BindView(R.id.other_radio) RadioButton otherRadio;

    public TripDialogFragment() {
        // Required empty public constructor
    }

    public static TripDialogFragment newInstance(String title) {
        TripDialogFragment frag = new TripDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trip_dialog, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view

        ButterKnife.bind(this, view);

        String title = getArguments().getString("title", "Enter Name");
        getDialog().setTitle(title);

        cancelButton.setOnClickListener(v -> this.dismiss());

        startButton.setOnClickListener(v -> {
            String atomicString;
            if (personalRadio.isChecked()) {
                atomicString = "PERSONAL";
            } else if (businessRadio.isChecked()) {
                atomicString = "BUSINESS";
            } else {
                atomicString = "OTHER";
            }

            if (listener != null) {
                listener.onFragmentDismissed(
                    new StringPair(tripNameField.getText().toString(), atomicString));
            }
            dismiss();
        });
    }

    /**
     * Listener to talk with the parent.
     * @param listener Listener to talk with fragment.
     */
    public void setListener(OnFragmentDismissedListener listener) {
        this.listener = listener;
    }

    public interface OnFragmentDismissedListener {
        void onFragmentDismissed(StringPair pair);
    }
}
