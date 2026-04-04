package com.example.androidappfilmproject.view.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.example.androidappfilmproject.databinding.MergePromoBinding
import com.example.remote_module.entity.ApiConstants

// Класс для отображения промо-акции на главном экране
class PromoView(context: Context, attributeSet: AttributeSet?) : FrameLayout(context, attributeSet) {
    val binding = MergePromoBinding.inflate(LayoutInflater.from(context), this)
    val watchButton = binding.watchButton
    val poster = binding.poster
    val closeButton = binding.closeButton

    // Метод для установки ссылки на постер
    fun setLinkForPoster(link: String) {
        Glide.with(binding.root)
            .load(ApiConstants.IMAGES_URL  + "w500" + link)
            .apply(RequestOptions().transform(CenterCrop()))
            .into(binding.poster)
    }
}