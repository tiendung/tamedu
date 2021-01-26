package tamedu.count

import android.content.Context
import android.content.SharedPreferences
import dev.tiendung.tamedu.helpers.PREFERENCE_FILE_KEY
import dev.tiendung.tamedu.helpers.THU_GIAN_COUNT_KEY

private var _sharedPref: SharedPreferences? = null
private var _thuGianCount: Int? = null

private fun getSharedPref(context: Context): SharedPreferences {
    if (_sharedPref == null)
        _sharedPref = context.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE)
    return _sharedPref!!
}

fun setThuGianCount(context: Context, v: Int) {
    _thuGianCount = v
    with(getSharedPref(context).edit()) {
        putInt(THU_GIAN_COUNT_KEY, v)
        apply()
    }
}

fun getThuGianCount(context: Context): Int {
    if (_thuGianCount == null)
        _thuGianCount = getSharedPref(context).getInt(THU_GIAN_COUNT_KEY, 0)
    return _thuGianCount!!
}

fun set(context: Context, k: String, v: Int) {
    with(getSharedPref(context).edit()) {
        putInt(k, v)
        apply()
    }
}

fun get(context: Context, k: String): Int {
    return getSharedPref(context).getInt(k, 0)
}