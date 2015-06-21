package rebamit.basic.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;


import rebamit.basic.R;
import rebamit.basic.activity.SearchOutfitActivity;


public class DatePickerDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
            try {
                if (SearchOutfitActivity.dateField == 1)
                    ((EditText) getActivity().findViewById(R.id.new_field1)).setText(String.format("%d-%02d-%02d", year, month + 1, day));
                else if (SearchOutfitActivity.dateField == 2)
                    ((EditText) getActivity().findViewById(R.id.new_field2)).setText(String.format("%d-%02d-%02d", year, month + 1, day));
            } catch (NullPointerException ee) {
                // do nothing
            }
    }

}
