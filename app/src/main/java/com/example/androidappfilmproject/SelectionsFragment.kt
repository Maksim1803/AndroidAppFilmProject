package com.example.androidappfilmproject

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.androidappfilmproject.databinding.FragmentSelectionsBinding

class SelectionsFragment : Fragment() {
    // Объявляем приватную переменную _binding, которая может быть null.
    // Она хранит ссылку на объект View Binding для доступа к элементам интерфейса.
    private var _binding: FragmentSelectionsBinding? = null

    // Объявляем геттер для получения не-null значения binding и доступа к элементам интерфейса.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Инициализируем _binding с помощью View Binding,
        // создавая объект из XML-разметки фрагмента.
        _binding = FragmentSelectionsBinding.inflate(inflater, container, false)

        // Возвращаем корневой View для отображения фрагмента.
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Выполняем анимацию кругового раскрытия для корневого View фрагмента.
        // Используем метод AnimationHelper для анимации.
        AnimationHelper.performFragmentCircularRevealAnimation(
            binding.selectionsFragmentRoot, requireActivity(), 4)
    }
}



