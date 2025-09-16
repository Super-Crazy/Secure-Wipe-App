package com.example.securewipe
import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

/**
 * A broadcast receiver that handles device administration events.
 * This class is essential for the Device Administrator API to function.
 * It receives callbacks from the system when the admin status changes.
 */
class MyDeviceAdminReceiver : DeviceAdminReceiver() {

    companion object {
        private const val TAG = "MyDeviceAdminReceiver"
    }

    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        Log.d(TAG, "Device Admin: Enabled")
        Toast.makeText(context, "Device admin enabled", Toast.LENGTH_SHORT).show()
    }

    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
        Log.d(TAG, "Device Admin: Disabled")
        Toast.makeText(context, "Device admin disabled", Toast.LENGTH_SHORT).show()
    }
}
