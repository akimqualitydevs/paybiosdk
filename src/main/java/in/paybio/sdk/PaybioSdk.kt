package `in`.paybio.sdk

import android.content.Context
import android.util.Log

object PaybioSdk {

    private const val TAG = "PaybioSdk"

    fun initialize(context: Context) {
        Log.d(TAG, "Paybio SDK initialized with app context: ${context.packageName}")
        // In real SDK, this could set up networking, config, etc.
    }

    fun getVersion(): String {
        return "1.0.1" // Update when you bump version
    }
}
