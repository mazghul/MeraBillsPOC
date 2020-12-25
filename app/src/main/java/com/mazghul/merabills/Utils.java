package com.mazghul.merabills;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.mazghul.merabills.models.PaymentList;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Utils {
    private static final String paymentListFilename = "LastPayment.txt";

    public static void writeRecordsToFile(PaymentList paymentList, Activity activity) {
        FileOutputStream fos;
        ObjectOutputStream oos = null;
        try {
            fos = activity.getApplicationContext().openFileOutput(paymentListFilename, Context.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(paymentList);
            Toast.makeText(activity, "Payment data saved successfully", Toast.LENGTH_SHORT).show();
            oos.close();
        } catch (Exception e) {
            Log.e(activity.getLocalClassName(), "Cant save records" + e.getMessage());
        } finally {
            if (oos != null)
                try {
                    oos.close();
                } catch (Exception e) {
                    Log.e(activity.getLocalClassName(), "Error while closing stream " + e.getMessage());
                }
        }
    }

    public static PaymentList readRecordsFromFile(Activity activity) {
        FileInputStream fin;
        ObjectInputStream ois = null;
        PaymentList paymentList = new PaymentList();
        try {
            fin = activity.getApplicationContext().openFileInput(paymentListFilename);
            ois = new ObjectInputStream(fin);
            paymentList = (PaymentList) ois.readObject();
            ois.close();
            Log.v(activity.getLocalClassName(), "Records read successfully");
        } catch (Exception e) {
            Log.e(activity.getLocalClassName(), "Cant read saved records" + e.getMessage());
        } finally {
            if (ois != null)
                try {
                    ois.close();
                } catch (Exception e) {
                    Log.e(activity.getLocalClassName(), "Error in closing stream while reading records" + e.getMessage());
                }
        }
        return paymentList;
    }
}
