package com.rafaela.lastmaintenance;


import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements DialogInterface.OnDismissListener{

    EditText maintenanceEditText;
    ImageButton mAddBtn;
    LastMaintenanceDialog lastMaintDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        maintenanceEditText = (EditText) findViewById(R.id.last_maintanence_btn);
        maintenanceEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                TextView textView = (TextView) findViewById(R.id.last_maintanence_desc);
                textView.setTextColor(Color.RED);
                showDialog();
            }

        });

        mAddBtn = (ImageButton) findViewById(R.id.add_btn);
        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                TextView textView = (TextView) findViewById(R.id.last_maintanence_desc);
                textView.setTextColor(Color.YELLOW);
                showDialog();
            }
        });
    }

    private void showDialog() {
        String date = "";
        String mileage = "";

        String valueLM = maintenanceEditText.getText().toString();
        if(!valueLM.isEmpty()){
            String[] values = valueLM.split("-");
            date = values[0];
            mileage = values[1];
        }

        lastMaintDialog = new LastMaintenanceDialog(MainActivity.this,date,mileage);
        lastMaintDialog.show();

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        //super.onDismiss(dialog);
        maintenanceEditText.setText(lastMaintDialog.getLmFinalValue());
    }
}
