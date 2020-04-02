package com.nvnrdhn.happyface

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.leanback.app.OnboardingSupportFragment
import androidx.preference.PreferenceManager

class OnboardFragment : OnboardingSupportFragment() {
    companion object {
        const val COMPLETED_ONBOARDING_PREF = "COMPLETED_ONBOARDING_PREF"
    }

    private lateinit var titles: List<String>
    private lateinit var descriptions: List<String>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        titles = resources.getStringArray(R.array.onboarding_titles).asList()
        descriptions = resources.getStringArray(R.array.onboarding_descriptions).asList()
    }

    override fun getPageTitle(pageIndex: Int): CharSequence {
        return titles[pageIndex]
    }

    override fun getPageDescription(pageIndex: Int): CharSequence {
        return descriptions[pageIndex]
    }

    override fun onCreateForegroundView(inflater: LayoutInflater?, container: ViewGroup?): View? {
        return null
    }

    override fun onCreateBackgroundView(inflater: LayoutInflater?, container: ViewGroup?): View? {
        return inflater?.inflate(R.layout.onboarding_background, container, false)
    }

    override fun getPageCount(): Int {
        return titles.size
    }

    override fun onCreateContentView(inflater: LayoutInflater?, container: ViewGroup?): View? {
        return ImageView(context).apply {
            scaleType = ImageView.ScaleType.FIT_CENTER
            setImageResource(R.drawable.ic_launcher_foreground)
        }
    }

    override fun onFinishFragment() {
        super.onFinishFragment()
        PreferenceManager.getDefaultSharedPreferences(context).edit().apply {
            putBoolean(COMPLETED_ONBOARDING_PREF, true)
            apply()
        }
        startActivity(Intent(context, MainActivity::class.java))
        activity?.finish()
    }

    override fun onProvideTheme(): Int = R.style.Theme_Leanback_Onboarding
}
