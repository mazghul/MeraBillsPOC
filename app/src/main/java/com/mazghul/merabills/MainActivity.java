package com.mazghul.merabills;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.mazghul.merabills.models.Payment;
import com.mazghul.merabills.models.PaymentList;

import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

public class MainActivity extends AppCompatActivity implements PaymentDialogFragment.DialogListener {

    private ChipGroup chipGroup;
    private PaymentList paymentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCenter.start(getApplication(), "cb5f3ba5-12dc-44b6-b480-c914271d0180",
                Analytics.class, Crashes.class);
        paymentList = Utils.readRecordsFromFile(this); // Read data from file.
        chipGroup = findViewById(R.id.chip_group_main);
        for (int i = 0; i < paymentList.getPayments().size(); i++) {
            chipGroup.addView(createChip(paymentList.getPayments().get(i))); // Add Chip for each payment
        }
        updateUIValues();
        Button save = findViewById(R.id.save_records);
        save.setOnClickListener(view -> Utils.writeRecordsToFile(paymentList, this)); // Save records to file.
    }

    // Create Chip to display payments.
    private Chip createChip(Payment payment) {
        String chipText = payment.getPaymentType() + " = Rs." + payment.getAmount();
        Chip chip = new Chip(this);
        chip.setText(chipText);
        chip.setChipBackgroundColorResource(R.color.chip_color);
        chip.setCloseIconVisible(true);
        chip.setCloseIcon(getDrawable(R.drawable.ic_close_icon));
        chip.setTextColor(getResources().getColor(R.color.black));
        chip.setTextAppearance(R.style.ChipTextAppearance);
        chip.setOnCloseIconClickListener(view -> {
            chipGroup.removeView(chip);
            paymentList.getPayments().remove(payment);
            updateUIValues();
        });
        return chip;
    }

    private void calculateTotals() {
        double total = 0d;
        for (int i = 0; i < paymentList.getPayments().size(); i++) {
            Double amount = paymentList.getPayments().get(i).getAmount();
            total = total + amount;
        }
        TextView total_amount = findViewById(R.id.total_amount);
        total_amount.setText(String.valueOf(total));
    }

    private void updateUIValues() {
        calculateTotals(); // For total Amount.
        if(paymentList.getPayments().size() == 0) {
            findViewById(R.id.no_payments).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.no_payments).setVisibility(View.GONE);
        }
    }

    public void addPayment(View view) {
        PaymentDialogFragment paymentDialogFragment = new PaymentDialogFragment();
        paymentDialogFragment.setData(paymentList.getPayments());
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        paymentDialogFragment.show(ft, "dialog");
    }

    @Override
    public void onFinishEditDialog(Payment payment) {
        paymentList.getPayments().add(payment);
        chipGroup.addView(createChip(payment));
        updateUIValues();
    }


}