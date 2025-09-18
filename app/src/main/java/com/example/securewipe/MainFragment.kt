package com.example.securewipe

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.securewipe.databinding.FragmentMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var devicePolicyManager: DevicePolicyManager
    private lateinit var adminComponent: ComponentName

    private val adminEnableLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        devicePolicyManager = requireActivity().getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        adminComponent = ComponentName(requireContext(), MyDeviceAdminReceiver::class.java)
        setupClickListeners()
    }

    override fun onResume() {
        super.onResume()
        updateUiState()
    }

    private fun setupClickListeners() {
        binding.activateAdminButton.setOnClickListener {
            val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
                putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent)
                putExtra(
                    DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    "Permission needed to perform a secure wipe."
                )
            }
            adminEnableLauncher.launch(intent)
        }
        binding.initiateWipeButton.setOnClickListener {
            showWipeConfirmationDialog()
        }
    }

    private fun updateUiState() {
        val isAdminActive = devicePolicyManager.isAdminActive(adminComponent)
        if (isAdminActive) {
            binding.activationCard.visibility = View.GONE
            binding.activeStateLayout.visibility = View.VISIBLE
        } else {
            binding.activationCard.visibility = View.VISIBLE
            binding.activeStateLayout.visibility = View.GONE
        }
    }

    private fun showWipeConfirmationDialog() {
        val editText = EditText(requireContext())
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Confirm Data Deletion")
            .setMessage("This action is irreversible. Type 'ERASE' to confirm.")
            .setView(editText)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Confirm Erase") { _, _ ->
                if (editText.text.toString() == "ERASE") {
                    performSecureWipe()
                } else {
                    Toast.makeText(requireContext(), "Incorrect confirmation.", Toast.LENGTH_SHORT).show()
                }
            }
            .show()
    }

    private fun performSecureWipe() {
        Log.d("SecureWipe", "Attempting direct factory reset...")
        try {
            devicePolicyManager.lockNow()
            devicePolicyManager.wipeData(0)
        } catch (e: SecurityException) {
            Log.e("WipeError", "Failed to wipe data: Permission denied.", e)
            Toast.makeText(requireContext(), "Wipe failed: Permission not granted.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}