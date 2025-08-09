package `in`.paybio.sdk

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Utils {

    fun getCurrentTimestamp(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }
}
