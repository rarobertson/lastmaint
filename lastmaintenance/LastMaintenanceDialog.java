package com.rafaela.lastmaintenance;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class LastMaintenanceDialog extends Dialog implements
        View.OnClickListener, DialogInterface.OnDismissListener {
    private Activity activity;
    private Button btnYes, btnNo, btnDate, btnCancel, btnSave;
    private EditText dateEditText, mileageEditText;

    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;

    private String lmFinalValue;

    private String mStrDate, mStrMileage;
    private String auxStrMileage;
    private String auxStrDate;

    String lastMaintenance;

    public LastMaintenanceDialog(Activity activity, String date, String mileage) {
        super(activity);
        setActivity(activity);
        mStrDate = date;
        mStrMileage = mileage;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.last_maint_popup);

        initValues();
    }

    private void initValues() {

        btnYes = (Button) findViewById(R.id.yes_btn);
        btnNo = (Button) findViewById(R.id.no_btn);
        btnDate = (Button) findViewById(R.id.date_picker_icon_btn);
        dateEditText = (EditText) findViewById(R.id.date_desc_btn);
        mileageEditText = (EditText) findViewById(R.id.mileage_text);
        btnSave = (Button) findViewById(R.id.save_btn);
        btnCancel = (Button) findViewById(R.id.cancel_btn);

        btnYes.setOnClickListener(this);
        btnNo.setOnClickListener(this);
        btnDate.setOnClickListener(this);
        dateEditText.setOnClickListener(this);
        mileageEditText.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        auxStrMileage = mStrMileage;
        auxStrDate = mStrDate;
        lastMaintenance = "";

        setDatePicker();
        setKeyboard();

        setOnDismissListener(this);
    }

    private void setKeyboard() {
        mileageEditText.requestFocus();
        DecimalFormat formatter = new DecimalFormat("#,###,###");
        if(!mStrMileage.isEmpty()){
            mileageEditText.setText(formatter.format(Integer.parseInt(mStrMileage.trim())));
        } else {
            mileageEditText.setText(mStrMileage);
        }

        mileageEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    mileageEditText.setText(v.getText());
                    mStrMileage = v.getText().toString();
                    DecimalFormat formatter = new DecimalFormat("#,###,###");
                    mileageEditText.setText(formatter.format(Integer.parseInt(mStrMileage.trim())));
                    hideKeyboard(v);
                    return true;
                }
                return false;
            }
        });
    }

    private void hideKeyboard(TextView v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private void setDatePicker() {
        dateFormatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        dateEditText.requestFocus();
        dateEditText.setText(mStrDate);

        datePickerDialog = new DatePickerDialog(getContext());
        datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                                                  @Override
                                                  public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                                      Calendar newDate = Calendar.getInstance();
                                                      newDate.set(year, month, dayOfMonth);
                                                      dateEditText.setText(dateFormatter.format(newDate.getTime()));
                                                      mStrDate = dateEditText.getText().toString();
                                                  }
                                              }
        );

        if(!mStrDate.isEmpty()){
            mStrDate.trim();
            String[] date = mStrDate.split("/");
            Calendar cal = Calendar.getInstance();
            int day = Integer.parseInt(date[0]);
            int month = Integer.parseInt(date[1]);
            int year = Integer.parseInt(date[2].trim());
            cal.set(day, month, year);
            day = cal.get(Calendar.DATE);
            month = cal.get(Calendar.MONTH);
            year = cal.get(Calendar.YEAR);
            datePickerDialog.updateDate(year, month, day);
        }
        datePickerDialog.hide();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.yes_btn:
                resetAllInputs();
                lastMaintenance = "yes";
                break;

            case R.id.no_btn:
                lastMaintenance = "no";
                clean();
                break;

            case R.id.save_btn:
                if (lastMaintenance.isEmpty()) {
                    showLastMaintenanceToast();
                    return;
                }

                if (isInputValid()) {
                    save();
                    dismiss();
                } else {
                    showInputErrors();
                }
                break;

            case R.id.cancel_btn:
                clean();
                save();
                cancel();
                break;

            case R.id.date_desc_btn:
            case R.id.date_picker_icon_btn:
                showDatePicker();
                break;

            case R.id.mileage_text:
                showKeyboard();
                break;

            default:
                break;
        }

    }

    private void showLastMaintenanceToast() {
        Toast.makeText(activity, "Please Select Yes or No!",
                Toast.LENGTH_LONG).show();
        //TODO: add aim toast here
//        LayoutInflater myInflater = LayoutInflater.from(this);
//        View view = myInflater.inflate(R.layout.your_custom_layout, null);
//        Toast mytoast = new Toast(this);
//        mytoast.setView(view);
//        mytoast.setDuration(Toast.LENGTH_LONG);
//        mytoast.show();
    }

    private void resetAllInputs() {
        fillInput(mStrDate, auxStrDate, dateEditText);
        fillInput(mStrMileage, auxStrMileage, mileageEditText);
    }

    private void showDatePicker() {
        if (!datePickerDialog.isShowing()) {
            datePickerDialog.getCurrentFocus();
            datePickerDialog.show();
        }
    }

    private void showKeyboard() {
       // InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
       // imm.showSoftInput(mileageEditText, InputMethodManager.SHOW_IMPLICIT);
    }

    private void save() {
        lmFinalValue = (mStrDate.isEmpty() && mStrMileage.isEmpty()) ? "" : mStrDate + " - " + mStrMileage;
        //TODO save on vehicle
    }

    private void clean() {
        auxStrMileage = mStrMileage;
        auxStrDate = mStrDate;
        mStrMileage = "";
        mStrDate = "";
    }

    private void fillInput(String currentValue, String previousValue, EditText editText) {
        if (currentValue.isEmpty() && !previousValue.isEmpty()) {
            editText.setText(auxStrDate);
            currentValue = previousValue;
            previousValue = "";
        }
        return;
    }

    private void showInputErrors() {

        if (mStrDate.isEmpty()) {
            btnDate.setError("Please enter a valid date");
        }

        if (mStrMileage.isEmpty()) {
            btnDate.setError("Please enter a valid mileage");
        }
    }

    private boolean isInputValid() {
        if(lastMaintenance.equalsIgnoreCase("no")){
            return true;
        }
        return !mStrDate.isEmpty() && !mStrMileage.isEmpty();
    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        //super.onDismiss(dialog);
        final Activity activity = getActivity();
        if (activity instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }

    public String getLmFinalValue() {
        return lmFinalValue;
    }

    public void setLmFinalValue(String lmFinalValue) {
        this.lmFinalValue = lmFinalValue;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}
