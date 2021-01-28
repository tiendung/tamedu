package tamedu.count

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import dev.tiendung.tamedu.helpers.*
import java.util.*

private var _sharedPref: SharedPreferences? = null
private var _todayReseted: Boolean = false
private fun getSharedPref(context: Context): SharedPreferences {
    if (_sharedPref == null)
        _sharedPref = context.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE)

    if (!_todayReseted) {
        val todayOfMonth = Calendar.getInstance()[Calendar.DAY_OF_MONTH]
        if (todayOfMonth == _sharedPref!!.getInt("todayOfMonth", -1)) {
            reset(context, todayOfMonth)
            _todayReseted = true
        }
    }
    return _sharedPref!!
}

fun reset(context: Context, todayOfMonth: Int) {
    val tdom: Int = if (todayOfMonth > 0 || todayOfMonth <= 31) todayOfMonth
        else Calendar.getInstance()[Calendar.DAY_OF_MONTH]
    with(getSharedPref(context).edit()) {
        COUNT_KEYS.forEach { putInt(it, 0) }
        putInt("todayOfMonth", tdom)
        commit()
    }
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

fun inc(context: Context, k: String, v: Int) {
    set(context, k, get(context, k) + v)
}

private val Y = Color.parseColor("#635D19")
private val G = Color.parseColor("#16504B")
private val B = Color.parseColor("#000000")

fun color(context: Context, k: String): Int {
    val v = get(context, k)
    val g = COUNT_KEY_TO_GOAL.get(k)
    if (v >= g!!) return G
    if (v > 0) return Y
    return B
}