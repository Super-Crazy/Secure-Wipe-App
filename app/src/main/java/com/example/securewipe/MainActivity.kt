package com.example.securewipe

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.securewipe.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // When the app starts, load the main "Wipe" page by default.
        replaceFragment(MainFragment())

        // Set up the listener for the navigation tabs.
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                // When a tab is tapped, show the corresponding page (Fragment).
                when (tab?.position) {
                    0 -> replaceFragment(MainFragment())
                    1 -> replaceFragment(TutorialFragment())
                    2 -> replaceFragment(PrivacyPolicyFragment())
                    3 -> replaceFragment(AboutAppFragment())
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Not needed for this app
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Not needed for this app
            }
        })
    }

    // This helper function handles swapping the pages in the container.
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}