package com.kolumbo.materialdesign.recyclerview

import android.content.Intent
import android.os.Bundle
import android.widget.RatingBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kolumbo.materialdesign.R
import com.kolumbo.materialdesign.databinding.RecyclerLayoutBinding
import com.kolumbo.materialdesign.view.MainActivity


class RecyclerViewActivity : AppCompatActivity() {

    lateinit var binding: RecyclerLayoutBinding
    lateinit var adapter: NoteRecyclerAdapter

    private val notes: MutableList<Pair<Note, Boolean>> = mutableListOf()

    private var filterUp = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RecyclerLayoutBinding.inflate(layoutInflater)

        setContentView(binding.root)

        initRecyclerView()

        initClickListeners()
    }

    private fun initRecyclerView() {
        binding.recyclerView.adapter = null

        fillNoteList()
        adapter = NoteRecyclerAdapter(notes)
        binding.recyclerView.adapter = adapter

        ItemTouchHelper(ItemTouchHelperCallback(adapter)).apply {
            attachToRecyclerView(binding.recyclerView)
        }

    }

    private fun initClickListeners() {

        binding.chipFilter.setOnClickListener { view ->
            val chip = view as Chip
            filterUp = !filterUp
            if (filterUp) {
                chip.chipIcon = ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_filter_up
                )

                notes.sortByDescending { it.first.priority }
                adapter.notifyDataSetChanged()

            } else {
                chip.chipIcon = ContextCompat.getDrawable(this, R.drawable.ic_filter_down)
                notes.sortBy { it.first.priority }
                adapter.notifyDataSetChanged()
            }

        }


        binding.fab.setOnClickListener {
            val layout =
                layoutInflater.inflate(R.layout.alert_layout_note_maker, binding.root, false)

            val nameNoteEditText =
                layout.findViewById<AppCompatEditText>(R.id.name_edittext)

            val descriptionEditText =
                layout.findViewById<AppCompatEditText>(R.id.descriptionAlert_edittext)

            val priorityRatingBar = layout.findViewById<RatingBar>(R.id.ratingBar)


            val alert = MaterialAlertDialogBuilder(this).setView(layout)
                .setPositiveButton("Сохранить") { alert, _ ->

                    notes.add(
                        Pair(
                            Note(
                                nameNoteEditText.text.toString(),
                                descriptionEditText.text.toString(),
                                priorityRatingBar.rating.toInt()
                            ), false
                        )
                    )
                    adapter.notifyItemChanged(if (notes.size > 0) notes.size - 1 else 0)
                    alert.dismiss()
                }
                .setNegativeButton("Отмена") { alert, _ ->
                    alert.dismiss()
                }

            alert.show()

        }

        binding.back.setOnClickListener {
            val backIntent = Intent(this, MainActivity::class.java)
            startActivity(backIntent)
        }

    }

    private fun fillNoteList() {

        for (i in 0..5) {
            notes.add(
                Pair(
                    Note(
                        "Дело номер: $i",
                        "Все мы давно и хорошо знакомы с RecyclerView. Мы постоянно используем его в своих приложениях и прекрасно знаем, как работают адаптер и ViewHolder: достаточно создать макет для каждого элемента списка, который будет «надуваться». ",
                        i
                    ), false
                )
            )

        }

    }
}