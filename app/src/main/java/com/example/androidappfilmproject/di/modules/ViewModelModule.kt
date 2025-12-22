package com.example.androidappfilmproject.di.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.androidappfilmproject.di.ViewModelKey
import com.example.androidappfilmproject.viewmodel.DemoFragmentViewModel
import com.example.androidappfilmproject.viewmodel.DetailsFragmentViewModel
import com.example.androidappfilmproject.viewmodel.FavoritesFragmentViewModel
import com.example.androidappfilmproject.viewmodel.HomeFragmentViewModel
import com.example.androidappfilmproject.viewmodel.SelectionsFragmentViewModel
import com.example.androidappfilmproject.viewmodel.ViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

// Класс для предоставления зависимостей ViewModel.
@Module
abstract class ViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(HomeFragmentViewModel::class)
    abstract fun bindHomeFragmentViewModel(viewModel: HomeFragmentViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SelectionsFragmentViewModel::class)
    abstract fun bindSelectionsFragmentViewModel(viewModel: SelectionsFragmentViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DemoFragmentViewModel::class)
    abstract fun bindDemoFragmentViewModel(viewModel: DemoFragmentViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DetailsFragmentViewModel::class)
    abstract fun bindDetailsFragmentViewModel(viewModel: DetailsFragmentViewModel): ViewModel

    // Добавляем FavoritesFragmentViewModel в Dagger-граф
    @Binds
    @IntoMap
    @ViewModelKey(FavoritesFragmentViewModel::class)
    abstract fun bindFavoritesFragmentViewModel(viewModel: FavoritesFragmentViewModel): ViewModel
}
