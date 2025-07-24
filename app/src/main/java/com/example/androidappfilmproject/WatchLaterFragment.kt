package com.example.androidappfilmproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.androidappfilmproject.databinding.FragmentWatchLaterBinding


class WatchLaterFragment : Fragment() {
    // Объявляем переменную для View Binding
    private var _binding: FragmentWatchLaterBinding? = null
    // Объявляем геттер для получения не-null значения binding и доступа к элементам интерфейса.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Инициализируем _binding с помощью View Binding,
        // создавая объект из XML-разметки фрагмента.
        _binding = FragmentWatchLaterBinding.inflate(inflater, container, false)
        // Возвращаем корневой View для отображения фрагмента.
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Выполняем анимацию кругового раскрытия фрагмента, используя root view из binding
        AnimationHelper.performFragmentCircularRevealAnimation(
            binding.watchLaterFragmentRoot,
            requireActivity(),
            3
        )
    }
}