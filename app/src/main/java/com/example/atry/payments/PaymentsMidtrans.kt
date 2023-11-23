package com.example.atry.payments

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.lifecycleScope
import com.example.atry.databinding.ActivityDetailDonasiBinding
import com.midtrans.sdk.corekit.core.PaymentMethod
import com.midtrans.sdk.uikit.api.model.SnapTransactionDetail
import com.midtrans.sdk.uikit.api.model.TransactionResult
import com.midtrans.sdk.uikit.external.UiKitApi
import com.midtrans.sdk.uikit.internal.util.UiKitConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import okhttp3.*
import java.util.UUID

class PaymentsMidtrans : AppCompatActivity() {

    private lateinit var mbinding: ActivityDetailDonasiBinding
    var random = "NH-${UUID.randomUUID()}".substring(0, 8)
    val id_order = initTransactionDetails().orderId

    val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result?.resultCode == RESULT_OK) {
                result.data?.let {
                    val transactionResult =
                        it.getParcelableExtra<TransactionResult>(UiKitConstants.KEY_TRANSACTION_RESULT)
                    Toast.makeText(this, "${transactionResult?.transactionId}", Toast.LENGTH_LONG)
                        .show()
                    Toast.makeText(this, id_order, Toast.LENGTH_LONG)
                        .show()
                    checkOrderStatus(id_order)
                }
            }
        }

    private var customerDetails = com.midtrans.sdk.uikit.api.model.CustomerDetails(
        firstName = "name",
        customerIdentifier = "mail@mail.com",
        email = "mail@mail.com",
        phone = "085310102020"
    )

    private var itemDetails = listOf(
        com.midtrans.sdk.uikit.api.model.ItemDetails(
            "DNS-${UUID.randomUUID()}",
            50000.00,
            1,
            "DONASI"
        )
    )

    private fun initTransactionDetails(): SnapTransactionDetail {
        return SnapTransactionDetail(
            orderId = random,
            grossAmount = 50000.00
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mbinding = ActivityDetailDonasiBinding.inflate(layoutInflater)
        setContentView(mbinding.root)
        buildUiKit()

        mbinding.buttonDonasi.setOnClickListener {
            UiKitApi.getDefaultInstance().startPaymentUiFlow(
                activity = this@PaymentsMidtrans,
                launcher = launcher,
                transactionDetails = initTransactionDetails(),
                customerDetails = customerDetails,
                itemDetails = itemDetails,
                paymentMethod = PaymentMethod.BANK_TRANSFER
            )
        }
    }

    private fun buildUiKit() {
        UiKitApi.Builder()
            .withContext(this.applicationContext)
            .withMerchantUrl("http://192.168.1.12/api-mysql-main/midtrans.php/")
            .withMerchantClientKey("SB-Mid-client-Cus_lO_5JXzHSIcU")
            .enableLog(true)
            .withColorTheme(
                com.midtrans.sdk.uikit.api.model.CustomColorTheme(
                    "#FFE51255",
                    "#B61548",
                    "#FFE51255"
                )
            )
            .build()
        setLocaleNew("id")
        uiKitCustomSetting()
    }

    private fun setLocaleNew(languageCode: String?) {
        val locales = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(locales)
    }

    private fun uiKitCustomSetting() {
        val uIKitCustomSetting = UiKitApi.getDefaultInstance().uiKitSetting
        uIKitCustomSetting.saveCardChecked = true
    }

    private fun checkOrderStatus(orderId: String) {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("https://api.sandbox.midtrans.com/v2/$orderId/status")
            .get()
            .addHeader("accept", "application/json")
            .addHeader("authorization", "Basic U0ItTWlkLXNlcnZlci1TMUdwX0o3b2Z0aWJ0dXNPWTdqUjhKalA6")
            .build()

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    runOnUiThread {
                        Toast.makeText(this@PaymentsMidtrans, responseBody, Toast.LENGTH_LONG).show()
//                        val intent = Intent(this@PaymentsMidtrans, DonasiActivity::class.java)
//                        startActivity(intent)
                        //code
                    }
                } else {
                    runOnUiThread {
                        val errorMessage = "Error: ${response.code} ${response.message}"
                        Toast.makeText(this@PaymentsMidtrans, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    val errorMessage = "Error: ${e.message}"
                    Toast.makeText(this@PaymentsMidtrans, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
