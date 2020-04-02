package com.nvnrdhn.happyface

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class OnboardingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
        supportFragmentManager.beginTransaction()
            .replace(R.id.root, OnboardFragment())
            .commit()
    }
}
