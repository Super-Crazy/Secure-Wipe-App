package com.example.securewipe

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.securewipe.MyDeviceAdminReceiver
import com.example.securewipe.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var devicePolicyManager: DevicePolicyManager
    private lateinit var adminComponent: ComponentName

    companion object {
        private const val REQUEST_CODE_ENABLE_ADMIN = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the Device Policy Manager and the ComponentName for our admin receiver
        devicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as
                DevicePolicyManager
        adminComponent = ComponentName(this, MyDeviceAdminReceiver::class.java)

        setupClickListeners()
    }

    override fun onResume() {
        super.onResume()
        // Update the UI state every time the activity is resumed
        updateUiState()
    }

    private fun updateUiState() {
        val isAdminActive = devicePolicyManager.isAdminActive(adminComponent)
        if (isAdminActive) {
            binding.statusValueTextView.text = "Admin Active"
            binding.statusValueTextView.setTextColor(ContextCompat.getColor(this,
                android.R.color.holo_green_dark))
            binding.activateAdminButton.isEnabled = false // No need to activate again
            binding.initiateWipeButton.isEnabled = true
        } else {
            binding.statusValueTextView.text = "Admin Inactive"
            binding.statusValueTextView.setTextColor(ContextCompat.getColor(this,
                android.R.color.holo_red_dark))
            binding.activateAdminButton.isEnabled = true
            binding.initiateWipeButton.isEnabled = false // Cannot wipe without admin rights
        }
    }

    private fun setupClickListeners() {
        binding.activateAdminButton.setOnClickListener {
            // Create an intent to launch the system's device admin activation screen
            val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
                putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent)
                putExtra(
                    DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    "This permission is required to perform a secure data wipe of the device."
                )
            }
            startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN)
        }

        binding.initiateWipeButton.setOnClickListener {
            showWipeConfirmationDialog()
        }
    }

    private fun showWipeConfirmationDialog() {
        val editText = EditText(this)
        MaterialAlertDialogBuilder(this)
            .setTitle("Confirm Data Deletion")
            .setMessage("This action is irreversible and will permanently erase all data on this device. To proceed, type 'ERASE'into the box below.")
                .setView(editText)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Confirm Erase") { _, _ ->
                    if (editText.text.toString() == "ERASE") {
                        performSecureWipe()
                    } else {
                        MaterialAlertDialogBuilder(this)
                            .setTitle("Incorrect Confirmation")
                            .setMessage("The wipe operation was not initiated.")
                            .setPositiveButton("OK", null)
                            .show()
                    }
                }
                .show()
    }

    private fun performSecureWipe() {
        // This is the final, critical call.
        // The '0' flag indicates a standard wipe, which on modern devices
        // triggers a Cryptographic Erase.
        // Before calling this, we would integrate the backend communication logic.

        val generateCertificate = binding.generateCertificateSwitch.isChecked
        if (generateCertificate) {
            // TODO: Implement backend communication logic here (Part II)
            // For now, we simulate a log message.
            println("Certificate generation requested. Sending data to backend...")
        }

        println("Initiating device wipe...")

        // Uncomment the following line to enable the actual wipe functionality
        devicePolicyManager.wipeData(0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ENABLE_ADMIN) {
            // The user has returned from the admin activation screen.
            // onResume() will be called next, which will update the UI state.
        }
    }
}
