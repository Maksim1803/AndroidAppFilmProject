package com.example.androidappfilmproject.view.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidappfilmproject.App
import com.example.androidappfilmproject.MainActivity
import com.example.androidappfilmproject.databinding.FragmentDemoBinding
import com.example.androidappfilmproject.domain.Film
import com.example.androidappfilmproject.view.rv_adapters.FilmListRecyclerAdapter
import com.example.androidappfilmproject.view.rv_adapters.TopSpacingItemDecoration
import com.example.androidappfilmproject.viewmodel.DemoFragmentViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class DemoFragment : Fragment() {
    private var _binding: FragmentDemoBinding? = null
    private val binding get() = _binding!!

    // Внедряем нашу единую фабрику для ViewModel
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    // Получаем ViewModel с помощью Dagger-фабрики
    private val viewModel: DemoFragmentViewModel by viewModels { viewModelFactory }

    private lateinit var filmsAdapter: FilmListRecyclerAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Выполняем Dagger-инъекцию, чтобы получить viewModelFactory
        (requireActivity().application as App).dagger.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDemoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        filmsAdapter = FilmListRecyclerAdapter(object : FilmListRecyclerAdapter.OnItemClickListener {
            override fun click(film: Film) {
                (requireActivity() as MainActivity).launchLocalDetailsFragment(film)
            }

            override fun onFavoriteClick(film: Film) {
                // В демо-режиме эта функция не нужна
            }
        })

        binding.demoRecycler.apply {
            adapter = filmsAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(TopSpacingItemDecoration(8))
        }

        lifecycleScope.launch {
            viewModel.films.collectLatest { films ->
                filmsAdapter.submitData(PagingData.from(films))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
