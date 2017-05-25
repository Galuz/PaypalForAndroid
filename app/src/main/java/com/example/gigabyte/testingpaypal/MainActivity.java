package com.example.gigabyte.testingpaypal;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import java.math.BigDecimal;

public class MainActivity extends AppCompatActivity {

    EditText m_response;
    PayPalConfiguration m_configuration;
    //id is the link to the paypal account
    String m_paypalClientId;
    Intent m_service;
    int m_paypalRequestCode = 999;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_response = (EditText) findViewById(R.id.response);

        m_configuration = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)//sandbox for text and production for real
            .clientId(m_paypalClientId);
        m_service = new Intent(this, PayPalService.class);
        m_service.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, m_configuration); // Configuration above
        startService(m_service);//paypal service, listening to call to paypal app
    }

    void pay(View view){
        PayPalPayment payment = new PayPalPayment(new BigDecimal(10), "USD", "Test payment with paypal", PayPalPayment.PAYMENT_INTENT_SALE );
        Intent intent = new Intent(this, PayPalPayment.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, m_configuration);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
        startActivityForResult(intent, m_paypalRequestCode);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == m_paypalRequestCode){
            if (resultCode == Activity.RESULT_OK){
                //This is to confirm that the payment is working to avoid fraud
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                if (confirmation != null){
                    String state = confirmation.getProofOfPayment().getState();

                    if (state.equals("approved")){
                        m_response.setText("payment approved");
                    }else {
                        m_response.setText("Payment Error");
                    }
                }else {
                    m_response.setText("confirmation is  null");
                }
            }
        }
    }
}
