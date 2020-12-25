package com.mazghul.merabills;

import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.mazghul.merabills.models.Payment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class PaymentDialogFragment extends DialogFragment {

    final String CASH = "Cash";
    final String BANK_TRANSFER = "Bank Transfer";
    final String CREDIT_CARD = "Credit Card";

    private Spinner payment_type;
    private EditText amountEditText, provider_editText, transaction_reference;
    private LinearLayout additional_payment_details;
    private List<Payment> paymentList;
    private List<String> paymentTypes = new ArrayList<>(Arrays.asList(CASH, BANK_TRANSFER, CREDIT_CARD));

    public void setData(List<Payment> paymentList) {
        this.paymentList = paymentList;
    }

    @Override
    public void onResume() {
        Window window = Objects.requireNonNull(getDialog()).getWindow();
        Point size = new Point();
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        window.setLayout((int) (size.x * 0.9), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        super.onResume();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        amountEditText = view.findViewById(R.id.amount_editText);
        provider_editText = view.findViewById(R.id.provider_editText);
        transaction_reference = view.findViewById(R.id.transaction_reference);
        Button btnDone = view.findViewById(R.id.btnDone);
        Button btnCancel = view.findViewById(R.id.btn_cancel);
        loadSpinner(view);
        btnDone.setOnClickListener(view1 -> saveData());
        btnCancel.setOnClickListener(view1 -> dismiss());
    }



    private void loadSpinner(View view) {
        payment_type = view.findViewById(R.id.payment_types);
        additional_payment_details = view.findViewById(R.id.additional_payment_details);
        List<String> completedPayments = new ArrayList<>();

        for (Payment payment : paymentList) {
            try {
                completedPayments.add(payment.getPaymentType());
            } catch (Exception e) {
                Log.d("removing payment", e.getMessage());
            }
        }
        paymentTypes.removeAll(new HashSet<>(completedPayments));
        ArrayAdapter<String> payment_type_adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item,
                paymentTypes);
        payment_type_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        payment_type.setAdapter(payment_type_adapter);

        payment_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(Arrays.asList(BANK_TRANSFER, CREDIT_CARD).contains(payment_type.getSelectedItem().toString())) {
                    additional_payment_details.setVisibility(View.VISIBLE);
                } else {
                    additional_payment_details.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void saveData() {
        if(amountEditText.getText().toString().equals("")) {
            Toast.makeText(getActivity(), "Please enter an Amount", Toast.LENGTH_SHORT).show();
            return;
        }
        DialogListener dialogListener = (DialogListener) getActivity();
        Payment payment = new Payment();
        payment.setId(UUID.randomUUID().toString());
        try {
            payment.setAmount(Double.valueOf(amountEditText.getText().toString()));
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Please enter an Amount", Toast.LENGTH_SHORT).show();
            return;
        }
        if(payment_type.getSelectedItem() == null) {
            Toast.makeText(getActivity(), "You are not allowed to perform this action", Toast.LENGTH_SHORT).show();
            return;
        }
        payment.setPaymentType(payment_type.getSelectedItem().toString());
        payment.setTransactionId(transaction_reference.getText().toString());
        payment.setProvider(provider_editText.getText().toString());
        assert dialogListener != null;
        dialogListener.onFinishEditDialog(payment);
        dismiss();
    }

        public interface DialogListener {
        void onFinishEditDialog(Payment payment);
    }

}
