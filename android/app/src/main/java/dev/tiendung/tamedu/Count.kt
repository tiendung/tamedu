package tamedu.count
import dev.tiendung.tamedu.helpers.*

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.IOException
import com.google.gson.Gson
import android.view.View

import android.content.Intent
import android.net.Uri

private var _sharedPref: SharedPreferences? = null
var _todayReseted: Boolean = false
private fun getSharedPref(context: Context): SharedPreferences {
    if (_sharedPref == null) {
        _sharedPref = context.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE)
    }
    if (!_todayReseted) {
        val today = dateStr()
        if (today != _sharedPref!!.getString("today", "")) { reset() }
        _todayReseted = true
    }
    return _sharedPref!!
}

fun reset() {
    val stats = _sharedPref!!.getAll()
    try {
        var dir = File(Environment.getExternalStorageDirectory(), "Documents"); dir.mkdir()
        File(dir, "tamedu_stats.json").appendText(",\n${Gson().toJson(stats)}")
    } catch (e: IOException) {
        Log.w("ExternalStorage", "Error writing stats file", e)
    }

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

private var _resetPressedCount = 0
private var _currentCountKey: String = ""
private var _currentCountAdded: Int = 0
private var _showHabitsBar = true
private val _habitsCountVisibilities = arrayOf(View.GONE, View.VISIBLE)

fun hideOrShow(i: Int): Int {
    return if (_showHabitsBar) _habitsCountVisibilities[i] else _habitsCountVisibilities[1-i]
}

fun countHabit(habitCountKey: String) {
    _currentCountKey = habitCountKey
    _currentCountAdded = 0
    _showHabitsBar = false
    _resetPressedCount = 0
}

private fun openUrl(context: Context, url: String) {
    val openURL = Intent(Intent.ACTION_VIEW)
    openURL.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    openURL.data = Uri.parse(url)
    context.startActivity(openURL)
}

fun countUpdateTotal(context: Context) {
    if (_currentCountAdded == 0 && _resetPressedCount == 0) {
        if (_currentCountKey == PULL_COUNT_KEY) { context.broadcastUpdateWidget(NGHE_VQDD) }
        if (_currentCountKey == ABS_COUNT_KEY) {
            tamedu.phap.skipAutoPlay = !tamedu.phap.skipAutoPlay
            toast(context, "Auto play is ${if (!tamedu.phap.skipAutoPlay) "on" else "off"}")
        }
    }
    tamedu.count.inc(context, _currentCountKey, _currentCountAdded)
    _showHabitsBar = true
    _resetPressedCount = 0
}

fun countReset(context: Context) {
    if (_currentCountAdded == 0 && _resetPressedCount == 0) {
        if (_currentCountKey == ABS_COUNT_KEY) {
            openUrl(context, "https://tiendung.github.io/?save#${tamedu.reminder.currentId()}")
        }
        if (_currentCountKey == PULL_COUNT_KEY) { context.broadcastUpdateWidget(THU_NOI) }

        _showHabitsBar = true
        _resetPressedCount = 0
        return
    }

    _resetPressedCount += 1
    if (_resetPressedCount == 3) toast(context, "Press \"Reset\" one more to reset all counters")
    if (_resetPressedCount  > 3) {
        tamedu.count.reset()
        _showHabitsBar = true
    }
    _currentCountAdded = 0
}

fun addCurrentCount(x: Int) { 
    _currentCountAdded +=  x; 
    _resetPressedCount = 0 
}

fun countLabel(context: Context, countKey: String): String {
    return "${COUNT_KEY_TO_LABEL[countKey]} ${get(context, countKey)}"
}

fun currentCountLabel(): String {
    return "${COUNT_KEY_TO_LABEL[_currentCountKey]} + $_currentCountAdded"
}
