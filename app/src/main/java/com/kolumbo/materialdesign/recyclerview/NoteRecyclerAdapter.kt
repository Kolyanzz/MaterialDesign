package com.kolumbo.materialdesign.recyclerview

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.kolumbo.materialdesign.R

class NoteRecyclerAdapter(private val notesList: MutableList<Pair<Note, Boolean>>) :
    RecyclerView.Adapter<NoteRecyclerAdapter.NoteViewHolder>(), ItemTouchHelperAdapter {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.note_recycler_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(notesList[position].first)
    }

    override fun getItemCount(): Int {
        return notesList.size
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        notesList.removeAt(fromPosition).apply {
            notesList.add(if (toPosition > fromPosition) toPosition - 1 else toPosition, this)
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onItemDismiss(position: Int) {
        notesList.removeAt(position)
        notifyItemRemoved(position)
    }


    //   ===================   ViewHolder    ===================

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        ItemTouchHelperViewHolder {

        val nameNoteTxtView = itemView.findViewById<MaterialTextView>(R.id.nameNote_editText)
        val describeNoteTxtView =
            itemView.findViewById<MaterialTextView>(R.id.descriptionNote_editText)
        val priorityNoteTxtView = itemView.findViewById<RatingBar>(R.id.ratingBar)

        val container =
            itemView.findViewById<ConstraintLayout>(R.id.itemRootElementConstraintLayout)

        val descriptionTextView =
            itemView.findViewById<MaterialTextView>(R.id.descriptionNote_editText)

        fun bind(note: Note) {
            nameNoteTxtView.text = note.name
            describeNoteTxtView.text = note.description
            priorityNoteTxtView.rating = note.priority.toFloat()
            descriptionTextView.visibility =
                if (notesList[layoutPosition].second) View.VISIBLE else View.GONE

            container.setOnClickListener {
                toggleText()
            }
        }

        private fun toggleText() {
            notesList[layoutPosition] = notesList[layoutPosition].let {
                it.first to !it.second
            }
            notifyItemChanged(layoutPosition)
        }

        override fun onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY)
        }

        override fun onItemClear() {
            itemView.setBackgroundColor(Color.WHITE)
        }

    }

}