package com.dr.coursemate.utils

import android.content.Context
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.dr.coursemate.utils.AppConstant.AUTH_STATUS
import com.dr.coursemate.utils.AppConstant.AUTH_STATUS_DONE
import com.dr.coursemate.utils.AppConstant.CASHFREE_API_KEY
import com.dr.coursemate.utils.AppConstant.CASHFREE_SECRET_KEY
import com.dr.coursemate.utils.AppConstant.COLLECTION_CONTROLS
import com.dr.coursemate.utils.AppConstant.CURRENT_AFFAIRS
import com.dr.coursemate.utils.AppConstant.EMAIL
import com.dr.coursemate.utils.AppConstant.GENDER
import com.dr.coursemate.utils.AppConstant.NEWS
import com.dr.coursemate.utils.AppConstant.PHONE
import com.dr.coursemate.utils.AppConstant.PROFILE_URL
import com.dr.coursemate.utils.AppConstant.URLS
import com.dr.coursemate.utils.AppConstant.USER_NAME
import com.dr.coursemate.room.MyDatabase
import com.dr.coursemate.room.WordPressEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.text.SimpleDateFormat
import java.util.*

class Utils {

    companion object {

        fun formatTimer (sec : Long) : String {

            val s = String.format("%02d",sec%60)
            val m = String.format("%02d",(sec/60)%60)

            return "$m : $s"
        }

        fun hideKeyBoard(view : View, context: Context) : Boolean {
            try {
                val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                return inputMethodManager.hideSoftInputFromWindow(view.windowToken,0)
            } catch (e : RuntimeException) { }
            return false
        }

        fun mUid(mContext : Context) = run {
           SharedPref.getData(mContext, PHONE).toString()
        }

        fun getAuthStatus(mContext: Context) = run {
            SharedPref.getData(mContext, AUTH_STATUS).toString()
        }

        fun isAuthDone(mContext: Context) = run {
            SharedPref.getData(mContext, AUTH_STATUS).toString() == AUTH_STATUS_DONE
        }

        fun getCashFreeApi(mContext: Context) : String {
            return SharedPref.getData(mContext, CASHFREE_API_KEY).toString()
        }

        fun setCashFreeApi(mContext: Context, mc : String)  {
            SharedPref.setData(mContext, CASHFREE_API_KEY, mc)
        }

        fun getCashFreeSecret(mContext: Context) : String {
            return SharedPref.getData(mContext, CASHFREE_SECRET_KEY).toString()
        }

        fun setCashFreeSecret(mContext: Context, mc : String)  {
            SharedPref.setData(mContext, CASHFREE_SECRET_KEY, mc)
        }

        fun setAuthDone(mContext: Context) {
            SharedPref.setData(mContext, AUTH_STATUS, AUTH_STATUS_DONE)
        }


        // get UserInformation

        fun getUserName(mContext: Context) : String {
            return SharedPref.getData(mContext, USER_NAME).toString()
        }

        fun getEmail(mContext: Context) : String {
            return SharedPref.getData(mContext, EMAIL).toString()
        }

        fun getPhone(mContext: Context) : String {
            return SharedPref.getData(mContext, PHONE).toString()
        }

        fun setUserName(mContext: Context, name : String)  {
            SharedPref.setData(mContext, USER_NAME, name)
        }

        fun setEmail(mContext: Context, email : String)   {
            SharedPref.setData(mContext, EMAIL, email)
        }

        fun setPhone(mContext: Context, phone : String)   {
            SharedPref.setData(mContext, PHONE, phone)
        }

        fun setProfileUrl(mContext: Context, profileUrl : String)  {
            SharedPref.setData(mContext, PROFILE_URL, profileUrl)
        }

        fun getProfileUrl(mContext: Context) : String {
            return SharedPref.getData(mContext, PROFILE_URL).toString()
        }

        fun returnTimeAgo(timestamp : Long) = run {



            var time = ""

            val cal = GregorianCalendar.getInstance()

            val currentSeconds = cal.get(Calendar.SECOND)
            val currentMinutes = cal.get(Calendar.MINUTE)
            val currentYear = cal.get(Calendar.YEAR)
            val currentTimestamp = cal.time.time

            cal.time = Date(timestamp)

            val mSeconds = cal.get(Calendar.SECOND)
            val mMinutes = cal.get(Calendar.MINUTE)
            val mYear = cal.get(Calendar.YEAR)
            val mTimestamp = cal.time.time

            time = if (currentTimestamp - mTimestamp < 60*1000) {
                (currentSeconds - mSeconds).toString() + " Sec ago"
            } else if (currentTimestamp - mTimestamp <= 60*60*1000) {
                (currentMinutes - mMinutes).toString() + "Min ago"
            } else {
                SimpleDateFormat("dd MMM hh:mm a", Locale.ENGLISH).format(Date(timestamp))
            }

            if (mYear != currentYear) {
                time = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).format(Date(timestamp))
            }

            time
        }

        fun loadNewsAndCurrentAffairs(mContext: Context) {

            val db = FirebaseFirestore.getInstance()

            db.collection(COLLECTION_CONTROLS)
                .document(URLS)
                .get().addOnSuccessListener {
                    try {

                        val currentAffairs = it[CURRENT_AFFAIRS].toString()
                        val news = it[NEWS].toString()

                        retrieveWordPressPosts(currentAffairs, mContext, CURRENT_AFFAIRS)
                        retrieveWordPressPosts(news, mContext, NEWS)

                    } catch (e: Exception) { }
                }
        }

        private fun retrieveWordPressPosts(url: String, mContext: Context, categoryName: String) {

            val room = MyDatabase.getDatabase(mContext)

            val queue: RequestQueue = Volley.newRequestQueue(mContext)

            val request =
                JsonArrayRequest(Request.Method.GET, url, null, {

                    room.wpDao().deleteByCategory(categoryName)

                    for (i in 0 until it.length()) {
                        try {

                            val jsonObject = it.getJSONObject(i)
                            val date = jsonObject.getString("date")
                            val id = jsonObject.getString("id")
                            val titleObject = jsonObject.getJSONObject("title")
                            val title = titleObject.getString("rendered")
                            val contentObj = jsonObject.getJSONObject("content")
                            val content = contentObj.getString("rendered")
                            var imageUrl = ""

                            try {
                                val doc: Document = Jsoup.parse(content)
                                val image: Elements = doc.getElementsByTag("img")
                                val el: Element = image[0]
                                imageUrl = el.absUrl("src")
                            } catch (e: IndexOutOfBoundsException) { }

                            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
                            val d = sdf.parse(date)

                            CoroutineScope(IO)
                                .launch {
                                    try {
                                        if (d != null) {
                                            room.wpDao().insert(
                                                WordPressEntity(
                                                    randomId= UUID.randomUUID().toString(),
                                                    id = id,
                                                    title = title,
                                                    imageUrl = imageUrl,
                                                    category = categoryName,
                                                    content = content,
                                                    timestamp = d.time
                                                )
                                            )
                                        }
                                    } catch (e: Exception) {
                                        if (d != null) {
                                            room.wpDao().update(
                                                WordPressEntity(
                                                    randomId= UUID.randomUUID().toString(),
                                                    id = id,
                                                    title = title,
                                                    imageUrl = imageUrl,
                                                    category = categoryName,
                                                    content = content,
                                                    timestamp = d.time
                                                )
                                            )
                                        }
                                    }
                                }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                },  {
                    Toast.makeText(mContext, "Something went wrong", Toast.LENGTH_SHORT).show()
                })
            queue.add(request)

        }
        
        fun getFormattedTime(timestamp: Long): String = run {

            SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.ENGLISH).format(Date(timestamp))

        }

    }

}