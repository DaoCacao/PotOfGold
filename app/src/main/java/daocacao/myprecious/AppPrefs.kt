package daocacao.myprecious

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.core.content.edit

class AppPrefs(context: Context) {

    private val prefs by lazy { context.getSharedPreferences("v1", MODE_PRIVATE) }

    var dayLimit
        set(value) = prefs.edit { putFloat("DAY_LIMIT", value) }
        get() = prefs.getFloat("DAY_LIMIT", 300f)
}