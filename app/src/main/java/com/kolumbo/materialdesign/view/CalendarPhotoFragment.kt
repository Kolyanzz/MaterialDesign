package com.kolumbo.materialdesign.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import coil.api.load
import com.kolumbo.materialdesign.R
import com.kolumbo.materialdesign.databinding.CalendarPhotoFragmentBinding
import com.kolumbo.materialdesign.model.PODServerResponseData
import com.kolumbo.materialdesign.model.PictureOfTheDayData
import com.kolumbo.materialdesign.recyclerview.RecyclerViewActivity
import com.kolumbo.materialdesign.view_model.CalendarPhotoViewModel
import java.util.*

private const val REQUEST_DATE = 0
private const val DIALOG_DATE = "DialogDate"
private const val DAY_PATTERN_TO_REQUEST = "yyyy-MM-dd"
private const val DAY_PATTERN_NORMAL = "dd-MM-yyyy"


class CalendarPhotoFragment : AppCompatDialogFragment(), DatePickerFragment.Callbacks {

    private var _binding: CalendarPhotoFragmentBinding? = null
    private val binding get() = _binding!!
    private var responseData: PODServerResponseData? = null

    private val model: CalendarPhotoViewModel by lazy {
        ViewModelProvider(this).get(CalendarPhotoViewModel::class.java)
    }

    private val observer = Observer<PictureOfTheDayData> {
        renderData(it, null)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CalendarPhotoFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.inputLayout.setEndIconOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data =
                    Uri.parse("https://en.wikipedia.org/wiki/${binding.inputEditText.text.toString()}")
            })
        }

        binding.fab.setOnClickListener {

            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, responseData?.url)
            }.also {
                startActivity(Intent.createChooser(it, getString(R.string.where_send)))
            }

        }

        binding.fab

        binding.btnDatePicker.setOnClickListener {
            DatePickerFragment.newInstance(Date()).apply {
                setTargetFragment(this@CalendarPhotoFragment, REQUEST_DATE)
                show(this@CalendarPhotoFragment.parentFragmentManager, DIALOG_DATE)
            }
        }

        binding.spaceImageView.setOnClickListener {
            if (responseData?.getMediaType() == DataTypeContentResponse.Video()) {

                AlertDialog.Builder(requireContext()).setTitle(getString(R.string.video))
                    .setMessage(getString(R.string.description_intent_to_watch_video_on_youtube))
                    .setNegativeButton(getString(R.string.stay_here), null)
                    .setPositiveButton(
                        getString(R.string.ok_go)
                    ) { _, _ ->
                        startActivity(Intent(Intent.ACTION_VIEW).apply {
                            this.data = Uri.parse(responseData?.url)
                        })
                    }.show()

            }
        }

        binding.next.setOnClickListener {
            val nextIntent = Intent(context, RecyclerViewActivity::class.java)
            startActivity(nextIntent)
        }

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        model.getData(Calendar.getInstance().time.getDateToRequest())
            .observe(viewLifecycleOwner, observer)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        fun getInstance(): Fragment {
            return CalendarPhotoFragment()
        }

    }

    private fun renderData(data: PictureOfTheDayData, date: Date?) {
        when (data) {
            is PictureOfTheDayData.Success -> {
                responseData = data.serverResponseData
                val url = responseData?.url

                if (url.isNullOrEmpty()) {
                    toast(getString(R.string.empty_link))
                } else {
                    loadImage(url)
                    binding.bottomSheet.bottomSheetDescriptionHeader.text = responseData?.title
                    binding.bottomSheet.bottomSheetDescription.text = responseData?.explanation
                }
            }
            is PictureOfTheDayData.Loading -> {
                //showLoading()
            }
            is PictureOfTheDayData.Error -> {
                toast(data.error.message)
            }
        }
    }

    private fun loadImage(url: String) {
        binding.spaceImageView.load(url) {
            placeholder(R.drawable.ic_loading)
            error(if (responseData?.getMediaType() == DataTypeContentResponse.Image()) R.drawable.ic_error else R.drawable.ic_video)
        }
    }

    private fun Fragment.toast(string: String?) {
        Toast.makeText(context, string, Toast.LENGTH_SHORT).apply {
            setGravity(Gravity.BOTTOM, 0, 250)
            show()
        }
    }

    override fun onDateSelected(date: Date) {
        val dateConvert = date.getNormalDate()
        binding.btnDatePicker.text = dateConvert
        model.getData(date.getDateToRequest())
            .observe(viewLifecycleOwner, observer)
    }

    private fun Date.getDateToRequest() =
        android.text.format.DateFormat.format(DAY_PATTERN_TO_REQUEST, this).toString()

    private fun Date.getNormalDate() =
        android.text.format.DateFormat.format(DAY_PATTERN_NORMAL, this).toString()

}