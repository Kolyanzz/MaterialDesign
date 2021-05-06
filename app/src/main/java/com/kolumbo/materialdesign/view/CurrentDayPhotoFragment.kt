package com.kolumbo.materialdesign.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.transition.ChangeBounds
import androidx.transition.ChangeImageTransform
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import coil.api.load
import com.kolumbo.materialdesign.databinding.CurrentDayPhotoFragmentBinding
import com.kolumbo.materialdesign.model.PODServerResponseData
import com.kolumbo.materialdesign.model.PictureOfTheDayData
import com.kolumbo.materialdesign.view_model.CurrentDayPhotoViewModel
import com.kolumbo.materialdesign.R

sealed class DataTypeContentResponse {
    data class Video(val type: String = "video") : DataTypeContentResponse()
    data class Image(val type: String = "image") : DataTypeContentResponse()
}

class CurrentDayPhotoFragment : AppCompatDialogFragment() {

    private var _binding: CurrentDayPhotoFragmentBinding? = null
    private val binding get() = _binding!!
    private var responseData: PODServerResponseData? = null
    private var fabPressed = false
    private var imagePressed = false

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

        startAnimationFab()

        return binding.root
    }

    private fun startAnimationFab() {
        ValueAnimator.ofFloat(0f, 360f).apply {
            startDelay = 1500
            duration = 3000
            addUpdateListener { animator ->
                binding.fabWiki.rotation = -(animator.animatedValue as Float)
            }
        }.start()
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

            if (fabPressed) {
                collapseFab()
            } else {
                expandFAB()
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


        binding.spaceImageView.setOnClickListener {
            imagePressed = !imagePressed

            TransitionManager.beginDelayedTransition(
                binding.constraintLayout, TransitionSet()
                    .addTransition(ChangeBounds())
                    .addTransition(ChangeImageTransform())
            )

            val params: ViewGroup.LayoutParams = binding.spaceImageView.layoutParams
            params.height =
                if (imagePressed) ViewGroup.LayoutParams.MATCH_PARENT else ViewGroup.LayoutParams.WRAP_CONTENT
            binding.spaceImageView.layoutParams = params
            binding.spaceImageView.scaleType =
                if (imagePressed) ImageView.ScaleType.CENTER_CROP else ImageView.ScaleType.FIT_CENTER

        }


    }


    private fun expandFAB() {
        fabPressed = true

        binding.shareSdQuality.visibility = View.VISIBLE
        binding.shareHdQuality.visibility = View.VISIBLE

        ObjectAnimator.ofFloat(binding.fab, "rotation", 0f, -360f).start()
        ObjectAnimator.ofFloat(binding.shareSdQuality, "translationY", -330f).start()
        ObjectAnimator.ofFloat(binding.shareHdQuality, "translationY", -450f).start()

        binding.shareSdQuality.animate()
            .alpha(1f)
            .setDuration(600)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    binding.shareSdQuality.isClickable = true
                    binding.shareSdQuality.setOnClickListener {
                        sendPhoto(false)
                    }
                }
            })

        binding.shareHdQuality.animate()
            .alpha(1f)
            .setDuration(600)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    binding.shareHdQuality.isClickable = true
                    binding.shareHdQuality.setOnClickListener {
                        sendPhoto(true)
                    }
                }
            })

    }

    /**
     * true - hd
     * false - sd
     * */
    private fun sendPhoto(quality: Boolean) {
        Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, if (quality) responseData?.hdurl else responseData?.url)
        }.also {
            startActivity(Intent.createChooser(it, getString(R.string.where_send)))
        }
    }

    private fun collapseFab() {
        fabPressed = false
        ObjectAnimator.ofFloat(binding.fab, "rotation", 0f, 360f).start()
        ObjectAnimator.ofFloat(binding.shareSdQuality, "translationY", 0f).start()
        ObjectAnimator.ofFloat(binding.shareHdQuality, "translationY", 0f).start()

        binding.shareSdQuality.animate()
            .alpha(0f)
            .setDuration(300)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    binding.shareSdQuality.isClickable = false
                    binding.shareSdQuality.setOnClickListener(null)
                }
            })
        binding.shareHdQuality.animate()
            .alpha(0f)
            .setDuration(300)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    binding.shareHdQuality.isClickable = false
                    binding.shareHdQuality.setOnClickListener(null)
                }
            })

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        model.getData().observe(viewLifecycleOwner, observer)
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
            listener { data, source ->
                setAnimation()
            }
        }

    }

    private fun setAnimation() {
        binding.spaceImageView.alpha = 0f
        binding.spaceImageView.animate().setDuration(1000).alpha(1f)
    }

    private fun Fragment.toast(string: String?) {
        Toast.makeText(context, string, Toast.LENGTH_SHORT).apply {
            setGravity(Gravity.BOTTOM, 0, 250)
            show()
        }
    }

}