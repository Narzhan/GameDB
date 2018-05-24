package com.example.narzhan.demo

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView


class Adapter(val userList: ArrayList<Game>, val cliclListener: (Game) -> Unit) : RecyclerView.Adapter<Adapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.item_layout, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val images = hashMapOf("běhací" to R.drawable.ic_baseline_directions_run_24px, "přemýšlecí" to R.drawable.ic_baseline_book_24px, "malá" to R.drawable.ic_baseline_access_alarm_24px, "noční" to R.drawable.ic_baseline_highlight_24px)
        holder?.txtName?.text = userList[position].name
        holder?.txtType?.text = userList[position].type
        if (images.containsKey(userList[position].type)) {
            holder?.picType?.setImageResource(images.getValue(userList[position].type))
        } else {
            holder?.picType?.setImageResource(R.drawable.ic_baseline_note_add_24px)
        }
        (holder as ViewHolder).bind(userList[position], cliclListener)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtName = itemView.findViewById<TextView>(R.id.text_name)
        val txtType = itemView.findViewById<TextView>(R.id.text_title)
        val picType = itemView.findViewById<ImageView>(R.id.text_picture)

        fun bind(game: Game, clickListener: (Game) -> Unit) {
            itemView.setOnClickListener { clickListener(game) }
        }
    }

}
