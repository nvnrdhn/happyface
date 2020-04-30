package com.nvnrdhn.happyface

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class ResultActivity : AppCompatActivity() {

    private var faceData: Array<Model.FaceData>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        faceData = intent.extras?.getSerializable(RESULT_KEY) as Array<Model.FaceData>?
    }
}
