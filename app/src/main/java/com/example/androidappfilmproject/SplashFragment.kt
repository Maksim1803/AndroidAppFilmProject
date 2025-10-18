package com.example.androidappfilmproject

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import com.example.androidappfilmproject.databinding.FragmentSplashBinding

class SplashFragment : Fragment() {
    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.splashView.setImageResource(R.drawable.logo)
        val scaleAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_up)
        binding.splashView.startAnimation(scaleAnimation)

        Handler(Looper.getMainLooper()).postDelayed({
            (requireActivity() as? MainActivity)?.let {
                it.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_placeholder, HomeFragment())
                    .commit()
            }
        }, 2000)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}