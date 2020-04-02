package com.nvnrdhn.happyface

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.preference.PreferenceManager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        PreferenceManager.getDefaultSharedPreferences(this).apply {
            if (!getBoolean(OnboardFragment.COMPLETED_ONBOARDING_PREF, false)) {
                startActivity(Intent(this@MainActivity, OnboardingActivity::class.java))
                finish()
            }
        }
    }
}
