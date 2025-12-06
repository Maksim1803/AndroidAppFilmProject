package com.example.androidappfilmproject.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.androidappfilmproject.utils.AnimationHelper
import com.example.androidappfilmproject.databinding.FragmentWatchLaterBinding

// Создаем класс WatchLaterFragment, который отвечает за отображение
// экрана "Посмотреть позже".
class WatchLaterFragment : Fragment() {
    // Переменная для хранения экземпляра биндинга (nullable)
    private var _binding: FragmentWatchLaterBinding? = null
    // Свойство для доступа к биндингу, которое гарантирует,
    // что он не будет null после onCreateView
    private val binding get() = _binding!!

    // Метод для создания и возвращения View фрагмента
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Инициализируем биндинг
        _binding = FragmentWatchLaterBinding.inflate(inflater, container, false)
        // Возвращаем корневой View для отображения фрагмента.
        return binding.root
    }

    // Метод, вызываемый после создания View
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Выполняем анимацию кругового раскрытия фрагмента
        AnimationHelper.performFragmentCircularRevealAnimation(
            binding.watchLaterFragmentRoot,
            requireActivity(),
            3
        )
    }
}