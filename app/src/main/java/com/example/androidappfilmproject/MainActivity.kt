package com.example.androidappfilmproject

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        }
    fun onClickToast(view: View) {
        Toast.makeText(this, "Меню кинофильмов", Toast.LENGTH_SHORT).show()
    }
    fun onClickToast2(view: View) {
        Toast.makeText(this, "Избранные кинофильмы", Toast.LENGTH_SHORT).show()
    }
    fun onClickToast3(view: View) {
        Toast.makeText(this, "Кинофильмы для просмотра попозже", Toast.LENGTH_SHORT).show()
    }
    fun onClickToast4(view: View) {
        Toast.makeText(this, "Кинофильмы в подборке", Toast.LENGTH_SHORT).show()
    }
    fun onClickToast5(view: View) {
        Toast.makeText(this, "Настройки для корректной работы", Toast.LENGTH_SHORT).show()
    }
}


