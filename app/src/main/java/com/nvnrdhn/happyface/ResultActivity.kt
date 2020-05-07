package com.nvnrdhn.happyface

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_result.*
import kotlin.math.roundToInt
import kotlin.reflect.full.memberProperties

class ResultActivity : AppCompatActivity() {

    private var faceData: Model.FaceData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        faceData = intent.extras?.getSerializable(RESULT_KEY) as Model.FaceData?
        if (faceData == null) Toast.makeText(this, "null face data", Toast.LENGTH_LONG).show()
        else showResult()
    }

    private fun showResult() {
        tv_age.text = "Age: ${faceData!!.faceAttributes.age}"
        var list = mutableListOf<Model.EmotionList>()
        val emotion = faceData!!.faceAttributes.emotion
        emotion::class.memberProperties.forEach {
            val value = (it.getter.call(emotion) as Double * 100).roundToInt()
            if (value > 0) list.add(Model.EmotionList(it.name, value))
        }
        rv_mood.apply {
            layoutManager = LinearLayoutManager(this@ResultActivity, RecyclerView.VERTICAL, false)
            adapter = FaceAdapter(list).apply {
                onItemClick = {
                    Toast.makeText(this@ResultActivity, "${it.name}: ${it.value}%", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
