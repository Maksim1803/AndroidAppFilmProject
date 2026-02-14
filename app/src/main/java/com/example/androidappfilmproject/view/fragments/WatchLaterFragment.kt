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
    
    // Инициализируем ViewBinding для доступа к компонентам разметки
    private var _binding: FragmentWatchLaterBinding? = null
    private val binding get() = _binding!!

    // Создаем и возвращаем иерархию View из XML
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentWatchLaterBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Инициализируем UI после создания View
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Запускаем анимацию кругового появления (позиция 3 в нижнем меню)
        AnimationHelper.performFragmentCircularRevealAnimation(binding.root,  3)
    }

    // Очищаем биндинг при уничтожении фрагмента для предотвращения утечек памяти
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
