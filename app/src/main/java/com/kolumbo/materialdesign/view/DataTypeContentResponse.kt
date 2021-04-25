package com.kolumbo.materialdesign.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import coil.api.load
import com.kolumbo.materialdesign.R
import com.kolumbo.materialdesign.databinding.CurrentDayPhotoFragmentBinding
import com.kolumbo.materialdesign.model.Days
import com.kolumbo.materialdesign.model.PODServerResponseData
import com.kolumbo.materialdesign.model.PictureOfTheDayData
import com.kolumbo.materialdesign.view_model.CurrentDayPhotoViewModel

sealed class DataTypeContentResponse {
    data class Video(val type: String = "video") : DataTypeContentResponse()
    data class Image(val type: String = "image") : DataTypeContentResponse()
}

class CurrentDayPhotoFragment : AppCompatDialogFragment() {

    private var _binding: CurrentDayPhotoFragmentBinding? = null
    private val binding get() = _binding!!
    private var responseData: PODServerResponseData? = null
    private var themeCallback: ThemeCallback? = null

    interface ThemeCallback {
        fun setMyTheme(themeId: Int)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        themeCallback = context as? ThemeCallback
    }

    private val model: CurrentDayPhotoViewModel by lazy {
        ViewModelProvider(this).get(CurrentDayPhotoViewModel::class.java)
    }

    private val observer = Observer<PictureOfTheDayData> {
        renderData(it)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CurrentDayPhotoFragmentBinding.inflate(inflater, container, false)
        setBottomAppBar()
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

        binding.bottomAppBar.setNavigationOnClickListener {
            loadImage(responseData?.hdurl ?: "404")
        }

        binding.fab.setOnClickListener {

            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, responseData?.url)
            }.also {
                startActivity(Intent.createChooser(it, "Куда отправим?"))
            }

        }

        binding.chipGroupChooseDay.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.chip_today -> {
                    model.getData(Days.Today).observe(viewLifecycleOwner, observer)
                }
                R.id.chip_yesterday -> {
                    model.getData(Days.Yesterday).observe(viewLifecycleOwner, observer)
                }
                R.id.chip_beforeYesterday -> {
                    model.getData(Days.BeforeYesterday).observe(viewLifecycleOwner, observer)
                }
                else -> {

                }

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

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_bottom, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.day_theme -> {
                themeCallback?.setMyTheme(R.style.Theme_MyMaterialDesign)
                activity?.recreate()
            }
            R.id.space_theme -> {
                themeCallback?.setMyTheme(R.style.ThemeSpace_MyMaterialDesign)
                activity?.recreate()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setBottomAppBar() {
        val context = activity as MainActivity
        context.setSupportActionBar(binding.root.findViewById(R.id.bottom_app_bar))
        setHasOptionsMenu(true)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        model.getData(Days.Today).observe(viewLifecycleOwner, observer)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        fun getInstance(): Fragment {
            return CurrentDayPhotoFragment()
        }

    }

    private fun renderData(data: PictureOfTheDayData) {
        when (data) {
            is PictureOfTheDayData.Success -> {
                responseData = data.serverResponseData
                val url = responseData?.url

                if (url.isNullOrEmpty()) {
                    toast("Ссылка пуста :(")
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

}