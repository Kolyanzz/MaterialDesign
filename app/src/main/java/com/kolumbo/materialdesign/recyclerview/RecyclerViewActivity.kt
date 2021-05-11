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

    private var filterUp = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RecyclerLayoutBinding.inflate(layoutInflater)

        title = getString(R.string.note_list)

        setContentView(binding.root)

        initRecyclerView()

        initClickListeners()

    }

    private fun initRecyclerView() {
        binding.recyclerView.adapter = null

        adapter = NoteRecyclerAdapter()
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
                chip.chipIcon = ContextCompat.getDrawable(this, R.drawable.ic_filter_up)
                adapter.sortDataByDescending()
            } else {
                chip.chipIcon = ContextCompat.getDrawable(this, R.drawable.ic_filter_down)
                adapter.sortData()
            }

        }

        binding.back.setOnClickListener {
            val backIntent = Intent(this, MainActivity::class.java)
            startActivity(backIntent)
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

                    adapter.addNoteInList(
                        Note(
                            nameNoteEditText.text.toString(),
                            descriptionEditText.text.toString(),
                            priorityRatingBar.rating.toInt()
                        )
                    )
                    alert.dismiss()
                }
                .setNegativeButton("Отмена") { alert, _ ->
                    alert.dismiss()
                }

            alert.show()

        }
    }

}