package mysoftstudio.stocksupporttool

import android.content.Context
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class Preferences<T>(private val name: String, private val default: T) : ReadWriteProperty<Any?, T> {
    private val sharedPreferences by lazy { MyApplication.instance.getSharedPreferences("StockSupportTool", Context.MODE_PRIVATE) }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return findPreferenceByName(name, default)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        putPreference(name, value)
    }

    private fun findPreferenceByName(name: String, default: T): T = with(sharedPreferences) {
        val result = when (default) {
            is Boolean -> getBoolean(name, default)
            is Int -> getInt(name, default)
            is Float -> getFloat(name, default)
            is Long -> getLong(name, default)
            is String -> getString(name, default)
            else -> throw IllegalArgumentException("This type is not support")
        }
        return result as T
    }

    private fun putPreference(name: String, value: T) = with(sharedPreferences.edit()) {
        when (value) {
            is Boolean -> putBoolean(name, value)
            is Int -> putInt(name, value)
            is Float -> putFloat(name, value)
            is Long -> putLong(name, value)
            is String -> putString(name, value)
            else -> throw IllegalArgumentException("This type is not support")
        }.apply()
    }
}