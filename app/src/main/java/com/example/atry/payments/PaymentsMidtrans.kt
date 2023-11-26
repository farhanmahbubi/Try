package com.example.atry.payments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.lifecycleScope
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
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
import org.json.JSONException
import org.json.JSONObject
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
            .withMerchantUrl("http://192.168.1.19/api-mysql-main/midtrans.php/")
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
            .addHeader(
                "authorization",
                "Basic U0ItTWlkLXNlcnZlci1TMUdwX0o3b2Z0aWJ0dXNPWTdqUjhKalA6"
            )
            .build()

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()

                response.use {
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()

                        runOnUiThread {
                            Log.d("PaymentsMidtrans", "Response: $responseBody")

                            try {
                                val jsonObject = JSONObject(responseBody)

                                // Extract specific values from the JSON object
                                val transactionId = jsonObject.getString("transaction_id")
                                val grossAmount = jsonObject.getDouble("gross_amount")
                                val orderId = jsonObject.getString("order_id")
                                val settlementTime = jsonObject.getString("settlement_time")


                                // Do something with the extracted values
                                Log.d(
                                    "PaymentsMidtrans",
                                    "transaction_id: $transactionId, gross_amount: $grossAmount, order_id: $orderId, settlement_time: $settlementTime"
                                )

                                // Send data to the server
                                sendDataToServer(jsonObject)
                            } catch (e: JSONException) {
                                // Handle JSON parsing exception
                                e.printStackTrace()
                                Log.e("PaymentsMidtrans", "Error parsing JSON")
                            }
                        }
                    } else {
                        runOnUiThread {
                            val errorMessage = "Error: ${response.code} ${response.message}"
                            Log.e("PaymentsMidtrans", errorMessage)
                            Toast.makeText(this@PaymentsMidtrans, "Error", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    val errorMessage = "Error: ${e.message}"
                    Toast.makeText(this@PaymentsMidtrans, "Error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun sendDataToServer(jsonObject: JSONObject) {
        // Example code (modify it based on your server-side implementation)
        val url = "http://192.168.1.19/api-mysql-main/api-insertDonasi.php"

        val sharedPreferences = getSharedPreferences("donatur_prefs", Context.MODE_PRIVATE)
        val idDonatur = sharedPreferences.getString("id_donatur", "")

        val request: RequestQueue = Volley.newRequestQueue(applicationContext)

        val stringRequest = object : StringRequest(
            com.android.volley.Request.Method.POST,
            url,
            { response ->
                runOnUiThread {
                    if (response.contains("Data berhasil disimpan")) {
                        Log.d("PaymentsMidtrans", "Data berhasil disimpan di server")
                        // Handle success
                    } else {
                        Log.e("PaymentsMidtrans", "Error dari server: $response")
                        // Handle error
                    }
                }
            },
            { error ->
                runOnUiThread {
                    Log.e("PaymentsMidtrans", "Error sending data to server: ${error.message}")
                    // Handle error
                }
            }
        ) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                // Sesuaikan parameter dengan nama yang diharapkan oleh server-side PHP
                params["transaction_id"] = jsonObject.getString("transaction_id")
                params["gross_amount"] = jsonObject.getDouble("gross_amount").toString()
                params["order_id"] = jsonObject.getString("order_id")
                params["settlement_time"] = jsonObject.getString("settlement_time")
                params["id_donatur"] = idDonatur ?: ""
                return params
            }
        }

        request.add(stringRequest)
    }
}