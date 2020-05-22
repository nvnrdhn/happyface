package com.nvnrdhn.happyface

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.TextView
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import kotlinx.android.synthetic.main.activity_history.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

class HistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        updateView()
    }

    private fun updateView() {
        lifecycleScope.launch {
            val list = getHistory()
            val total = DoubleArray(8)
            for (hist in list) {
                total[0] += hist.happy
                total[1] += hist.sad
                total[2] += hist.scared
                total[3] += hist.angry
                total[4] += hist.disgusted
                total[5] += hist.surprised
                total[6] += hist.neutral
                total[7] += hist.age.toDouble()
            }
            if (list.isNotEmpty()) {
                tv_happy.text = "Happy: ${(total[0]*100/list.size).roundToInt()}%"
                tv_sad.text = "Sad: ${(total[1]*100/list.size).roundToInt()}%"
                tv_scared.text = "Scared: ${(total[2]*100/list.size).roundToInt()}%"
                tv_angry.text = "Angry: ${(total[3]*100/list.size).roundToInt()}%"
                tv_disgusted.text = "Disgusted: ${(total[4]*100/list.size).roundToInt()}%"
                tv_surprised.text = "Surprised: ${(total[5]*100/list.size).roundToInt()}%"
                tv_neutral.text = "Neutral: ${(total[6]*100/list.size).roundToInt()}%"
                tv_age.text = "Age: ${(total[7]/list.size).roundToInt()}"
            }
            rv_history.apply {
                layoutManager = LinearLayoutManager(this@HistoryActivity, RecyclerView.HORIZONTAL, false)
                adapter = HistoryAdapter(this@HistoryActivity, list).apply {
                    onItemClick = {
                        val preview = Dialog(this@HistoryActivity)
                        preview.requestWindowFeature(Window.FEATURE_NO_TITLE)
                        preview.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
                        preview.setContentView(layoutInflater.inflate(R.layout.history_preview, null))
                        preview.findViewById<TextView>(R.id.tv_date).text = it.date
                        preview.findViewById<TextView>(R.id.tv_happy).text = "Happy: ${(it.happy * 100).roundToInt()}%"
                        preview.findViewById<TextView>(R.id.tv_sad).text = "Sad: ${(it.sad * 100).roundToInt()}%"
                        preview.findViewById<TextView>(R.id.tv_scared).text = "Scared: ${(it.scared * 100).roundToInt()}%"
                        preview.findViewById<TextView>(R.id.tv_angry).text = "Angry: ${(it.angry * 100).roundToInt()}%"
                        preview.findViewById<TextView>(R.id.tv_disgusted).text = "Disgusted: ${(it.disgusted * 100).roundToInt()}%"
                        preview.findViewById<TextView>(R.id.tv_surprised).text = "Surprised: ${(it.surprised * 100).roundToInt()}%"
                        preview.findViewById<TextView>(R.id.tv_neutral).text = "Neutral: ${(it.neutral * 100).roundToInt()}%"
                        preview.findViewById<TextView>(R.id.tv_age).text = "Age: ${it.age}"
                        Glide.with(this@HistoryActivity)
                            .load(it.file_uri.toUri())
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(preview.findViewById(R.id.iv_photo))
                        preview.show()
                    }
                }
            }
        }
    }

    companion object {
        suspend fun getHistory() = withContext(Dispatchers.IO) {
            db.historyDao().getHistory()
        }
    }
}
