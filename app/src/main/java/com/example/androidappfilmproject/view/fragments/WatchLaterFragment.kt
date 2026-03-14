package com.example.androidappfilmproject.view.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidappfilmproject.App
import com.example.androidappfilmproject.databinding.FragmentWatchLaterBinding
import com.example.androidappfilmproject.MainActivity
import com.example.androidappfilmproject.utils.AnimationHelper
import com.example.androidappfilmproject.view.notifications.NotificationHelper
import com.example.androidappfilmproject.view.rv_adapters.WatchLaterRecyclerAdapter
import com.example.androidappfilmproject.viewmodel.WatchLaterViewModel
import com.example.database_module.entity.Film
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javax.inject.Inject

// Создаем класс WatchLaterFragment, который отвечает за отображение
// экрана "Посмотреть позже".
class WatchLaterFragment : Fragment() {

    private var _binding: FragmentWatchLaterBinding? = null
    private val binding get() = _binding!!

    private lateinit var watchLaterAdapter: WatchLaterRecyclerAdapter
    private val compositeDisposable = CompositeDisposable()

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: WatchLaterViewModel by viewModels { viewModelFactory }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as App).dagger.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWatchLaterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Запуск анимации появления
        AnimationHelper.performFragmentCircularRevealAnimation(binding.root, 3)

        initRecycler()

        val disposable = viewModel.getWatchLaterFilms()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ list ->
                watchLaterAdapter.submitList(list)
                binding.emptyText.isVisible = list.isEmpty()
            }, {
                it.printStackTrace()
            })
        compositeDisposable.add(disposable)
    }

    // Метод для настройки RecyclerView и обработка кликов
    private fun initRecycler() {
        watchLaterAdapter = WatchLaterRecyclerAdapter(object : WatchLaterRecyclerAdapter.OnItemClickListener {
            override fun click(film: Film) {
                (activity as MainActivity).launchDetailsFragment(film)
            }

            override fun removeClick(film: Film) {
                NotificationHelper.cancelNotification(requireContext(), film)
            }
        })

        binding.watchLaterRecycler.apply {
            adapter = watchLaterAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
    // Метод для очистки ресурсов и отписки от RxJava при уничтожении View
    override fun onDestroyView() {
        super.onDestroyView()
        // Очистка подписок во избежание утечек
        compositeDisposable.clear()
        _binding = null
    }
}
