package com.example.Part_2_OPC7311.adapter

import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.Part_2_OPC7311.R
import com.example.Part_2_OPC7311.entities.Notes
import kotlinx.android.synthetic.main.item_rv_notes.view.*

// get data
class NotesAdapter() :
    RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {

    var listener:OnItemClickListener? = null
    var arrList = ArrayList<Notes>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        return NotesViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_rv_notes, parent, false)
        )
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        holder.itemView.tvTitle1.text = arrList[position].title
        holder.itemView.tvDesc.text = arrList[position].noteText
        holder.itemView.tvDateTime.text = arrList[position].dateTime

        if (arrList[position].color != null) {
            holder.itemView.cvNotes.setCardBackgroundColor(Color.parseColor(arrList[position].color))
        } else {
            holder.itemView.cvNotes.setCardBackgroundColor(Color.parseColor("#171C26"))
        }

        if (arrList[position].imgPath != null) {
            holder.itemView.ivNoteCard.setImageBitmap(BitmapFactory.decodeFile(arrList[position].imgPath))
            holder.itemView.ivNoteCard.visibility = View.VISIBLE
        } else {
            holder.itemView.ivNoteCard.visibility = View.GONE
        }

        if (arrList[position].webLink != null) {
            holder.itemView.tvWebLink.text = arrList[position].webLink
            holder.itemView.tvWebLink.visibility = View.VISIBLE
        } else {
            holder.itemView.tvWebLink.visibility = View.GONE
        }

        holder.itemView.cvNotes.setOnClickListener {
            listener!!.onClicked(arrList[position].id!!)
        }
    }

    override fun getItemCount(): Int {
        return arrList.size
    }

    fun setData(arrNotesList: List<Notes>) {
        arrList = arrNotesList as ArrayList<Notes>
    }

    fun setOnClickListener(listener1: OnItemClickListener) {
        listener = listener1
    }

    class NotesViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }

    interface OnItemClickListener {
        fun onClicked(noteId:Int)
    }
}