package com.nvnrdhn.happyface

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FaceAdapter(private val list: List<Model.EmotionList>): RecyclerView.Adapter<FaceAdapter.MoodViewHolder>() {

    var onItemClick:((Model.EmotionList) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MoodViewHolder(inflater, parent)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MoodViewHolder, position: Int) {
        val mood: Model.EmotionList = list[position]
        holder.bind(mood)
    }

    inner class MoodViewHolder(inflater: LayoutInflater, parent: ViewGroup)
        : RecyclerView.ViewHolder(inflater.inflate(R.layout.emotion_list, parent, false)) {

        private var mEmotion: TextView? = null

        init {
            itemView.setOnClickListener { onItemClick?.invoke(list[adapterPosition]) }
            mEmotion = itemView.findViewById(R.id.tv_emotion)
        }

        fun bind(emotion: Model.EmotionList) {
            mEmotion?.text = "${emotion.value}% ${emotion.name}"
        }
    }

}