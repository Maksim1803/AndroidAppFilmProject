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
    private var _binding: FragmentSelectionsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: SelectionsFragmentViewModel by viewModels { viewModelFactory }

    // Метод вызывается при присоединении фрагмента к контексту.
    // Внедряет зависимости через Dagger.
    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as App).dagger.inject(this)
    }

    // Метод создает и возвращает иерархию представлений, связанную с фрагментом.
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Метод вызывается сразу после onCreateView().
    // Здесь инициализируются UI-компоненты, настраиваются наблюдатели и слушатели.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        AnimationHelper.performFragmentCircularRevealAnimation(
            binding.root,
            requireActivity(),
            5
        )

        // Наблюдение за LiveData с выбранной категорией для обновления UI (RadioGroup)
        viewModel.categoryPropertyLifeData.observe(viewLifecycleOwner, Observer<String> { cat ->
            when (cat) {
                POPULAR_CATEGORY -> binding.radioGroup.check(R.id.radio_popular)
                TOP_RATED_CATEGORY -> binding.radioGroup.check(R.id.radio_top_rated)
                UPCOMING_CATEGORY -> binding.radioGroup.check(R.id.radio_upcoming)
                NOW_PLAYING_CATEGORY -> binding.radioGroup.check(R.id.radio_now_playing)
            }
        })

        // Слушатель для RadioGroup для сохранения выбранной категории
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_popular -> viewModel.putCategoryProperty(POPULAR_CATEGORY)
                R.id.radio_top_rated -> viewModel.putCategoryProperty(TOP_RATED_CATEGORY)
                R.id.radio_upcoming -> viewModel.putCategoryProperty(UPCOMING_CATEGORY)
                R.id.radio_now_playing -> viewModel.putCategoryProperty(NOW_PLAYING_CATEGORY)
            }
        }
    }

    // Метод вызывается при уничтожении представления фрагмента.
    // Очищает ссылку на binding во избежание утечек памяти.
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val POPULAR_CATEGORY = "popular"
        private const val TOP_RATED_CATEGORY = "top_rated"
        private const val UPCOMING_CATEGORY = "upcoming"
        private const val NOW_PLAYING_CATEGORY = "now_playing"
    }
}
