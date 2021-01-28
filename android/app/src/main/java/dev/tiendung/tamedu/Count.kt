package tamedu.count

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Environment
import com.google.gson.Gson
import dev.tiendung.tamedu.helpers.*
import java.io.File

private var _sharedPref: SharedPreferences? = null
private var _todayReseted: Boolean = false
private fun getSharedPref(context: Context): SharedPreferences {
    if (_sharedPref == null) {
        _sharedPref = context.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE)
    }
    if (!_todayReseted) {
        val today = dateStr()
        if (today != _sharedPref!!.getString("today", "")) { reset(context) }
        _todayReseted = true
    }
    return _sharedPref!!
}

fun reset(context: Context) {
    val stats = _sharedPref!!.getAll()
    val fileName = "${_sharedPref!!.getString("today", dateStr(-1))}.json"
    var dir = File(Environment.getExternalStorageDirectory(), "Documents"); dir.mkdir()
    dir = File(dir, "tamedu"); dir.mkdir()
    dir = File(dir, "stats"); dir.mkdir()
    File(dir, fileName).writeText(Gson().toJson(stats))

    with(_sharedPref!!.edit()) {
        COUNT_KEYS.forEach { putInt(it, 0) }
        putString("today", dateStr())
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