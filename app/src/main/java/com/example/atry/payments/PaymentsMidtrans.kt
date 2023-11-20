package com.example.atry.payments


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.atry.databinding.ActivityDetailDonasiBinding
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback
import com.midtrans.sdk.corekit.core.MidtransSDK
import com.midtrans.sdk.corekit.core.TransactionRequest
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme
import com.midtrans.sdk.corekit.models.BillingAddress
import com.midtrans.sdk.corekit.models.CustomerDetails
import com.midtrans.sdk.corekit.models.ItemDetails
import com.midtrans.sdk.corekit.models.ShippingAddress
import com.midtrans.sdk.uikit.SdkUIFlowBuilder

class PaymentsMidtrans : AppCompatActivity() {

    private lateinit var binding: ActivityDetailDonasiBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailDonasiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Midtrans SDK
        SdkUIFlowBuilder.init()
            .setClientKey("SB-Mid-client-Cus_lO_5JXzHSIcU")
            .setContext(applicationContext)
            .setTransactionFinishedCallback(TransactionFinishedCallback { result ->
                //logic
            })
            .setMerchantBaseUrl("http://192.168.1.12/api-mysql-main/midtrans.php/")  // Ensure the trailing slash
            .enableLog(true)
            .setColorTheme(CustomColorTheme("#FFE51255", "#B61548", "#FFE51255"))
            .setLanguage("id")
            .buildSDK()

        binding.buttonDonasi.setOnClickListener {
            val nama = binding.editTextNama.text.toString()
            val doa = binding.editTextDoa.text.toString()
            val donasi = binding.editTextNominal.text.toString()

            val transactionRequest = TransactionRequest("NH-"+System.currentTimeMillis()+ "", donasi.toDouble())
            val detail = ItemDetails("Donasi", donasi.toDouble(), 1, "LKSA NH")
            val itemDetails = ArrayList<ItemDetails>()
            itemDetails.add(detail)

            //uiKitDetails(transactionRequest)
            transactionRequest.itemDetails = itemDetails

            MidtransSDK.getInstance().transactionRequest = transactionRequest
            MidtransSDK.getInstance().startPaymentUiFlow(this)
        }

    }

        private fun uiKitDetails(transactionRequest: TransactionRequest) { // nanti jika menambahkan nama yang ambil daroii sharedpreference
        val customerDetails = CustomerDetails()                             //taruh disini juga part 10
        customerDetails.customerIdentifier = "Supriyanto"
        customerDetails.phone = "082345678999"
        customerDetails.firstName = "Supri"
        customerDetails.lastName = "Yanto"
        customerDetails.email = "supriyanto6543@gmail.com"
        val shippingAddress = ShippingAddress()
        shippingAddress.address = "Baturan, Gantiwarno"
        shippingAddress.city = "Klaten"
        shippingAddress.postalCode = "51193"
        customerDetails.shippingAddress = shippingAddress
        val billingAddress = BillingAddress()
        billingAddress.address = "Baturan, Gantiwarno"
        billingAddress.city = "Klaten"
        billingAddress.postalCode = "51193"
        customerDetails.billingAddress = billingAddress

        transactionRequest.customerDetails = customerDetails
    }
}
