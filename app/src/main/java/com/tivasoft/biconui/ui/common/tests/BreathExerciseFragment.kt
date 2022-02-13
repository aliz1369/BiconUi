package com.tivasoft.biconui.ui.common.tests

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.tivasoft.biconui.ui.MainActivity
import com.tivasoft.biconui.R
import com.tivasoft.biconui.databinding.FragmentBreathBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BreathExerciseFragment : Fragment() {
    private var _binding: FragmentBreathBinding? = null
    private val binding get() = _binding!!

    private var currentIndex = 0
    private val gifs = arrayOf(
        R.raw.breath_stage_1,
        R.raw.breath_stage_2,
        R.raw.breath_stage_3
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBreathBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadGif()
        binding.apply {
            next.setOnClickListener {
                when (currentIndex) {
                    in gifs.indices -> loadGif()
                    gifs.size -> {
                        next.text = getString(R.string.finish)
                        breathImage.visibility = View.GONE
                        instruction.visibility = View.GONE
                        repeatExercise.visibility = View.VISIBLE
                        currentIndex++
                    }
                    else -> (requireActivity() as MainActivity).setBottomSheetState(false)
                }
            }
            repeatExercise.setOnClickListener {
                currentIndex = 0
                breathImage.visibility = View.VISIBLE
                instruction.visibility = View.VISIBLE
                repeatExercise.visibility = View.GONE
                loadGif()
            }
        }
    }

    private fun loadGif() {
        binding.apply {
            Glide.with(requireContext())
                .asGif()
                .load(gifs[currentIndex++])
                .listener(object : RequestListener<GifDrawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<GifDrawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return true
                    }

                    override fun onResourceReady(
                        resource: GifDrawable?,
                        model: Any?,
                        target: Target<GifDrawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        resource?.setLoopCount(1)
                        return false
                    }
                })
                .into(breathImage)
            next.text = getString(
                R.string.test_next,
                currentIndex,
                gifs.size
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}