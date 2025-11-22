package com.example.androidappfilmproject.view.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import com.example.androidappfilmproject.MainActivity
import com.example.androidappfilmproject.R
import com.example.androidappfilmproject.databinding.FragmentSplashBinding

// Создаем класс SplashFragment, который отвечает за отображение заставки при запуске приложения.
class SplashFragment : Fragment() {
    // Переменная для хранения экземпляра биндинга (nullable)
    private var _binding: FragmentSplashBinding? = null
    // Свойство для доступа к биндингу, которое гарантирует, что он не будет null после onCreateView
    private val binding get() = _binding!!

    // Метод для создания и возвращения View фрагмента
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Инициализируем биндинг
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Метод, вызываемый после создания View
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Устанавливаем изображение для заставки
        binding.splashView.setImageResource(R.drawable.logo)
        // Загружаем анимацию масштабирования
        val scaleAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_up)
        // Запускаем анимацию
        binding.splashView.startAnimation(scaleAnimation)

        // Используем Handler для задержки перехода на главный экран
        Handler(Looper.getMainLooper()).postDelayed({
            (requireActivity() as? MainActivity)?.let {
                // Заменяем SplashFragment на HomeFragment
                it.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_placeholder, HomeFragment())
                    .commit()
            }
        }, 2000) // Задержка в 2 секунды
    }

    // Метод, вызываемый при уничтожении View фрагмента
    override fun onDestroyView() {
        super.onDestroyView()
        // Очищаем ссылку на биндинг, чтобы избежать утечек памяти
        _binding = null
    }
}
