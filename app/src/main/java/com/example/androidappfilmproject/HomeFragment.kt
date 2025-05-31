package com.example.androidappfilmproject

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidappfilmproject.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var filmsAdapter: FilmListRecyclerAdapter

    private val filmsDataBase = listOf(
        Film(
            "Начало",
            R.drawable.nachalo,
            "Вору, который крадёт секреты с помощью технологии обмена снами, поручают задачу: внедрить идею в сознание гендиректора, но его трагическое прошлое может привести всё к катастрофе."
        ),
        Film(
            "Грешники",
            R.drawable.greshniki,
            "Пытаясь оставить позади свою неспокойную жизнь, братья-близнецы возвращаются в родной город, и убеждаются, что здесь их ждёт ещё большее зло."
        ),
        Film(
            "Опустошение",
            R.drawable.opustoshenie,
            "После неудачной сделки с наркотиками избитый детектив пробивается сквозь преступный мир, чтобы спасти сына политика и распутать сложную сеть коррупции и заговоров, в которую попал весь его город."
        ),
        Film(
            "До рассвета",
            R.drawable.untildawn,
            "Группа друзей, оказавшихся в ловушке временной петли, где таинственные враги убивают их ужасными способами, должна дожить до рассвета, чтобы выбраться оттуда."
        ),
        Film(
            "Под огнем",
            R.drawable.podognem,
            "Взвод «морских котиков» отправляется на опасную миссию в Ирак, и рассказывает о хаосе и братстве на войне, вспоминая об этом событии."
        ),
        Film(
            "Субстанция",
            R.drawable.substance,
            "Увядающая знаменитость принимает препарат с чёрного рынка, который воспроизводит клетки и создаёт более молодую и привлекательную версию самой себя."
        ),
        Film(
            "Запертый",
            R.drawable.locked,
            "Вор, забравшийся в роскошный внедорожник, понимает, что попал в изощрённую игру в психологический хоррор."
        ),
        Film(
            "Компаньон",
            R.drawable.companion,
            "Отдых на выходных с друзьями превращается в хаос после того, как выясняется, что один из гостей не тот, за кого себя выдает."
        ),
        Film(
            "Ущелье",
            R.drawable.ushelie,
            "Когда появляется зло, они должны действовать сообща, чтобы выжить и противостоять тому, что внутри."
        ),
        Film(
            "Анора",
            R.drawable.anora,
            "Молодая стриптизерша из Бруклина знакомится с сыном олигарха и выходит за него замуж. Как только новость доходит до его родителей, её сказке приходит конец."
        )
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       // Инициализация адаптера
        filmsAdapter =
            FilmListRecyclerAdapter(object : FilmListRecyclerAdapter.OnItemClickListener {
                override fun click(film: Film) {
                    (requireActivity() as MainActivity).launchDetailsFragment(film)
                }
            })

        // Настройка RecyclerView
        binding.mainRecycler.apply {
            adapter = filmsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        filmsAdapter.setItems(filmsDataBase)
    }
}
