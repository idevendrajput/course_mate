package com.dr.coursemate.booksandnotes

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import coil.load
import com.android.volley.AuthFailureError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.cashfree.pg.CFPaymentService
import com.dr.coursemate.utils.AppConstant.AMOUNT
import com.dr.coursemate.utils.AppConstant.COLLECTION_PAYMENT_ORDERS
import com.dr.coursemate.utils.AppConstant.DESCRIPTION
import com.dr.coursemate.utils.AppConstant.DOCUMENT_ID
import com.dr.coursemate.utils.AppConstant.IMAGES
import com.dr.coursemate.utils.AppConstant.ITEM_TYPE
import com.dr.coursemate.utils.AppConstant.MAP_KEY
import com.dr.coursemate.utils.AppConstant.MY_ITEMS
import com.dr.coursemate.utils.AppConstant.ORDER_ID
import com.dr.coursemate.utils.AppConstant.PAYMENT_METHOD
import com.dr.coursemate.utils.AppConstant.PAYMENT_STATUS
import com.dr.coursemate.utils.AppConstant.PAYMENT_STATUS_PENDING
import com.dr.coursemate.utils.AppConstant.PAYMENT_STATUS_SUCCESS
import com.dr.coursemate.utils.AppConstant.PRICE
import com.dr.coursemate.utils.AppConstant.SOMETHING_WENT_WRONG
import com.dr.coursemate.utils.AppConstant.THE_TITLE
import com.dr.coursemate.utils.AppConstant.TIMESTAMP_FIELD
import com.dr.coursemate.utils.AppConstant.TITLE
import com.dr.coursemate.utils.AppConstant.USER_COLLECTION
import com.dr.coursemate.utils.AppConstant.USER_ID
import com.dr.coursemate.R
import com.dr.coursemate.utils.Utils.Companion.getCashFreeApi
import com.dr.coursemate.utils.Utils.Companion.getCashFreeSecret
import com.dr.coursemate.utils.Utils.Companion.getEmail
import com.dr.coursemate.utils.Utils.Companion.getPhone
import com.dr.coursemate.utils.Utils.Companion.getUserName
import com.dr.coursemate.utils.Utils.Companion.mUid
import com.dr.coursemate.databinding.FragmentNotesInfoBinding
import com.dr.coursemate.databinding.RowItemImageNotesInfoBinding
import com.dr.coursemate.room.MyDatabase
import com.dr.coursemate.room.MyNotesEntity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class NotesInfo : Fragment() {

    private lateinit var binding: FragmentNotesInfoBinding
    private lateinit var mContext: Context
    private lateinit var dId: String
    private var price = 0
    private lateinit var title: String
    private lateinit var images: ArrayList<String>
    private lateinit var room: MyDatabase
    private lateinit var description: String
    private var isPurchased = false
    private lateinit var loading: Dialog
    private val db = FirebaseFirestore.getInstance()

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotesInfoBinding.inflate(layoutInflater)

        room = MyDatabase.getDatabase(mContext)
        dId = arguments?.getString(DOCUMENT_ID).toString()
        isPurchased = room.myNotes().getMyNotesById(dId).isNotEmpty()
        images = arguments?.getSerializable(IMAGES) as ArrayList<String>
        description = arguments?.getString(DESCRIPTION).toString()
        price = arguments?.getInt(PRICE, 0) ?: 0
        title = arguments?.getString(TITLE).toString()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        actions()
        loading()

        Handler(Looper.getMainLooper())
            .post {
                updateUi()
                loadImages()
            }

    }

    private fun loading() {
        loading = Dialog(mContext)
        loading.setContentView(R.layout.dialog_loading)
        loading.setCancelable(false)
    }

    private fun actions() {

        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    private fun updateUi() {

        binding.heading.text = title
        binding.title.text = title
        binding.description.text = "More Details\n\n$description"
        binding.price.text = if (price == 0) "Free" else "Price: INR.$price"

        if (isPurchased) {
            binding.buyNow.text = "Open"
        }

    }

    private fun loadImages() {

        binding.vpImages.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.vpImages.adapter = AdapterImages(images)

    }

    private inner class AdapterImages(val list: ArrayList<String>): RecyclerView.Adapter<AdapterImages.ImageHolder>() {

        inner class ImageHolder(itemView: View, val dBinding: RowItemImageNotesInfoBinding) : RecyclerView.ViewHolder(itemView)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
            val dBinding = RowItemImageNotesInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ImageHolder(dBinding.root, dBinding)
        }

        override fun onBindViewHolder(holder: ImageHolder, position: Int) {
            holder.dBinding.imageView.load(list[position])
        }

        override fun getItemCount(): Int {
            return list.size
        }

    }

    private fun createOrderInCashFreeServer(amount: String, title : String) {

        val orderId = "order_id_".plus(System.currentTimeMillis())

        val jsonObjectRequest = object : StringRequest(
            Method.POST,"https://test.cashfree.com/api/v2/cftoken/order/",
            {
                try {
                    val obj = JSONObject(it)
                    val token = obj.get("cftoken").toString()
                    createOrderInFirestore(orderId, token, amount, title)
                } catch (e : Exception) {
                    loading.dismiss()
                    Toast.makeText(mContext, SOMETHING_WENT_WRONG, Toast.LENGTH_SHORT).show()
                }
            },   {
                loading.dismiss()
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val credentials = getCashFreeApi(mContext) + ":" + getCashFreeSecret(mContext)
                val base64EncodedCredentials: String =
                    Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
                val headers: HashMap<String, String> = HashMap()
                headers["Authorization"] = "Basic $base64EncodedCredentials"
                headers["Content-Type"] = "application/json"
                headers["x-client-id"] = getCashFreeApi(mContext)
                headers["x-client-secret"] =  getCashFreeSecret(mContext)
                return headers
            }

            override fun getBodyContentType(): String {
                return "application/json"
            }

            override fun getBody(): ByteArray {
                val params = JSONObject()
                params.put("orderAmount", amount.toInt())
                params.put("orderId", orderId)
                params.put("orderCurrency", "INR")
                return params.toString().toByteArray(charset = Charsets.UTF_8)
            }
        }

        val queue = Volley.newRequestQueue(mContext)
        queue.add(jsonObjectRequest)

    }

    private fun createOrderInFirestore(orderId: String, token : String, amount: String, title: String) {

        val map = HashMap<String, Any>()
        map[AMOUNT] = amount.toFloat()
        map[ORDER_ID] = orderId
        map["token"] = token
        map[THE_TITLE] = title
        map[PAYMENT_METHOD] = "CashFree"
        map[TIMESTAMP_FIELD] = FieldValue.serverTimestamp()
        map[USER_ID] = mUid(mContext)
        map[PAYMENT_STATUS] = PAYMENT_STATUS_PENDING

        db.collection(COLLECTION_PAYMENT_ORDERS)
            .document(orderId)
            .set(map, SetOptions.merge())
            .addOnSuccessListener {
                startCashFree(token, orderId, amount)
            }
    }

    private fun startCashFree(token: String, orderId: String, amount: String) {

        val params = HashMap<String,String>()
        params[CFPaymentService.PARAM_APP_ID] = getCashFreeApi(mContext)
        params[CFPaymentService.PARAM_ORDER_ID] = orderId
        params[CFPaymentService.PARAM_ORDER_CURRENCY] = "INR"
        params[CFPaymentService.PARAM_ORDER_AMOUNT] = amount
        params[CFPaymentService.PARAM_CUSTOMER_NAME] = getUserName(mContext)
        params[CFPaymentService.PARAM_CUSTOMER_EMAIL] = getEmail(mContext)
        params[CFPaymentService.PARAM_CUSTOMER_PHONE] = getPhone(mContext)

        val methods = arrayOf("UPI", "Other")

        AlertDialog.Builder(mContext)
            .setTitle("Choose Payment Method")
            .setItems(methods){_,i->
                when(i) {
                    0-> CFPaymentService.getCFPaymentServiceInstance().upiPayment(activity as AppCompatActivity, params, token, "PROD")
                    1-> CFPaymentService.getCFPaymentServiceInstance().doPayment(activity as AppCompatActivity, params, token, "PROD")
                }
            }.create().show()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CFPaymentService.REQ_CODE && data != null) {
            val bundle = data.extras
            if (bundle != null) {
                val json = JSONObject()
                val keys = bundle.keySet()
                for (key in keys) {
                    try {
                        json.put(key, JSONObject.wrap(bundle[key]))
                    } catch (e: JSONException) { }
                }
                try {

                    val status = json.get("txStatus").toString()
                    val orderId = json.get("orderId").toString()

                    if (status.equals("success", ignoreCase = true)) {

                        loading.show()
                        addToPurchaseList(orderId)

                        val map = HashMap<String,Any>()
                        map[PAYMENT_STATUS] = PAYMENT_STATUS_SUCCESS

                        db.collection(COLLECTION_PAYMENT_ORDERS)
                            .document(orderId)
                            .set(map, SetOptions.merge()).addOnSuccessListener {
                                try {
                                    loading.dismiss()
                                    Snackbar.make(binding.root, "Payment Added Successfully", Snackbar.LENGTH_SHORT).show()
                                } catch (e : Exception) {}
                            }
                    } else {
                        loading.dismiss()
                        db.collection(COLLECTION_PAYMENT_ORDERS)
                            .document(orderId).delete()
                        Toast.makeText(mContext, status, Toast.LENGTH_SHORT).show()
                    }
                } catch (e : Exception) {
                    Toast.makeText(mContext, SOMETHING_WENT_WRONG, Toast.LENGTH_SHORT).show()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun addToPurchaseList(orderId: String) {

        val mapKey = UUID.randomUUID().toString()

        val map = HashMap<String, Any>()
        map[MAP_KEY] = mapKey
        map[DOCUMENT_ID] = dId
        map[ITEM_TYPE] = "Notes"
        map[ORDER_ID] = orderId
        map[PRICE] = price
        map[TIMESTAMP_FIELD] = System.currentTimeMillis()

        val parentMap = HashMap<String, Any>()
        map[MY_ITEMS] = map

        db.collection(USER_COLLECTION)
            .document(mUid(mContext))
            .set(parentMap, SetOptions.merge())

        CoroutineScope(IO)
            .launch {
                try {
                    room.myNotes().insert(
                        MyNotesEntity(
                            mapKey,
                            dId,
                            "Notes",
                            orderId,
                            price,
                            System.currentTimeMillis()
                        )
                    )
                } catch (e: Exception) {
                    room.myNotes().update(
                        MyNotesEntity(
                            mapKey,
                            dId,
                            "Notes",
                            orderId,
                            price,
                            System.currentTimeMillis()
                        )
                    )
                }

            }

        updateUi()
    }

}