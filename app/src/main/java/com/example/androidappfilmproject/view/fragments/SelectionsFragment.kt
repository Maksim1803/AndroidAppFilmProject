package com.example.androidappfilmproject.view.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.androidappfilmproject.App
import com.example.androidappfilmproject.R
import com.example.androidappfilmproject.databinding.FragmentSelectionsBinding
import com.example.androidappfilmproject.utils.AnimationHelper
import com.example.androidappfilmproject.viewmodel.SelectionsFragmentViewModel
import javax.inject.Inject

// Класс, отвечающий за работу экрана "Подборки", где пользователь может выбрать категорию
// фильмов для отображения.
// Взаимодействует с [SelectionsFragmentViewModel] для сохранения и получения выбранной категории.
class SelectionsFragment : Fragment() {
    
    // Инициализируем ViewBinding
    private var _binding: FragmentSelectionsBinding? = null
    private val binding get() = _binding!!

    // Внедряем фабрику ViewModel через Dagger
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    // Получаем ViewModel для работы с логикой выбора категорий
    private val viewModel: SelectionsFragmentViewModel by viewModels { viewModelFactory }

    // Вызывается при прикреплении фрагмента к Activity
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Выполняем инъекцию зависимостей
        (requireActivity().application as App).dagger.inject(this)
    }

    // Создаем иерархию представлений из XML
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Инициализируем UI и настраиваем слушатели после создания View
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Запускаем анимацию кругового появления (позиция 4 в нижнем меню - Selections)
        AnimationHelper.performFragmentCircularRevealAnimation(binding.root,  4)

        // Наблюдаем за изменением выбранной категории в LiveData для обновления состояния кнопок
        viewModel.categoryPropertyLifeData.observe(viewLifecycleOwner, Observer<String> { cat ->
            val checkedId = when (cat) {
                POPULAR_CATEGORY -> R.id.radio_popular
                TOP_RATED_CATEGORY -> R.id.radio_top_rated
                UPCOMING_CATEGORY -> R.id.radio_upcoming
                NOW_PLAYING_CATEGORY -> R.id.radio_now_playing
                else -> -1
            }
            // Устанавливаем выбор только если он отличается от текущего, чтобы избежать бесконечного цикла обновлений
            if (checkedId != -1 && binding.radioGroup.checkedRadioButtonId != checkedId) {
                binding.radioGroup.check(checkedId)
            }
        })

        // Слушатель изменения выбора в группе кнопок
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                // Сохраняем выбранную категорию в настройки через ViewModel
                R.id.radio_popular -> viewModel.putCategoryProperty(POPULAR_CATEGORY)
                R.id.radio_top_rated -> viewModel.putCategoryProperty(TOP_RATED_CATEGORY)
                R.id.radio_upcoming -> viewModel.putCategoryProperty(UPCOMING_CATEGORY)
                R.id.radio_now_playing -> viewModel.putCategoryProperty(NOW_PLAYING_CATEGORY)
            }
        }
    }

    // Очистка биндинга при уничтожении View
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Константы для идентификации категорий фильмов в API и настройках
    companion object {
        private const val POPULAR_CATEGORY = "popular"
        private const val TOP_RATED_CATEGORY = "top_rated"
        private const val UPCOMING_CATEGORY = "upcoming"
        private const val NOW_PLAYING_CATEGORY = "now_playing"
    }
}
