package com.nvnrdhn.happyface

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class HistoryAdapter(private val ctx: Context, private val list: List<Model.History>):
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    var onItemClick:((Model.History) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater, parent)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val history: Model.History = list[position]
        holder.bind(history)
    }

    inner class ViewHolder(inflater: LayoutInflater, parent: ViewGroup)
        : RecyclerView.ViewHolder(inflater.inflate(R.layout.history_list, parent, false)) {
        private var mImage: ImageView? = null

        init {
            itemView.setOnClickListener { onItemClick?.invoke(list[adapterPosition]) }
            mImage = itemView.findViewById(R.id.iv_history)
        }

        fun bind(history: Model.History) {
            Glide.with(ctx)
                .load(history.file_uri.toUri())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(mImage)
        }
    }
}