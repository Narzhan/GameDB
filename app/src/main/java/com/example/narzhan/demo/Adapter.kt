package com.example.narzhan.demo

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


class Adapter(val userList: ArrayList<Game>, val cliclListener: (Game) -> Unit) : RecyclerView.Adapter<Adapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.item_layout, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.txtName?.text = userList[position].name
        holder?.txtType?.text = userList[position].type
        (holder as ViewHolder).bind(userList[position], cliclListener)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtName = itemView.findViewById<TextView>(R.id.text_name)
        val txtType = itemView.findViewById<TextView>(R.id.text_title)


        fun bind(game: Game, clickListener: (Game) -> Unit) {
                itemView.setOnClickListener { clickListener(game)}
            }

    }

}
