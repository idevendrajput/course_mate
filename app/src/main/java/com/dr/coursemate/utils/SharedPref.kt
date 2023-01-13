package com.dr.coursemate.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPref {

    companion object {

        var prefs: SharedPreferences? = null

        private fun getPrefs(context: Context): SharedPreferences {
            return context.getSharedPreferences(prefs.toString(), Context.MODE_PRIVATE)
        }

        @JvmStatic
        fun setData(context: Context, key: String?, value: String?) {
            val editor = getPrefs(context).edit()
            editor.putString(key, value)
            editor.apply()
        }

        fun setInt(context: Context, key: String?, value: Int) {
            val editor = getPrefs(context).edit()
            editor.putInt(key, value)
            editor.apply()
        }

        fun setFloat(context: Context, key: String?, value: Float) {
            val editor = getPrefs(context).edit()
            editor.putFloat(key, value)
            editor.apply()
        }

        fun insertStringSet(context: Context, key: String?, value: Set<String?>?) {
            getPrefs(context).edit().putStringSet(key, value).apply()
        }

        @JvmStatic
        fun getData(context: Context, key: String?): String? {
            return getPrefs(context).getString(key, "")
        }

        fun getInt(context: Context, key: String?): Int {
            return getPrefs(context).getInt(key, 0)
        }

        fun getFloat(context: Context, key: String?): Float {
            return getPrefs(context).getFloat(key, 0.0f)
        }

        //    public static Set<String> getStringSet(Context context, String key){
        //        return getPrefs(context).getStringSet(key,null);
        //    }
        @JvmStatic
        fun deleteKey(context: Context, key: String?) {
            val editor = getPrefs(context).edit()
            editor.remove(key)
            editor.apply()
        }

    }

}