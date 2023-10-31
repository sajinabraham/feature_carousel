package biz.filmeroo.premier

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import biz.filmeroo.premier.api.ApiFilm
import biz.filmeroo.premier.api.FilmService
import biz.filmeroo.premier.databinding.FragmentFilmDetailBinding
import biz.filmeroo.premier.detail.FilmDetailViewModel
import biz.filmeroo.premier.main.FilmAdapter
import biz.filmeroo.premier.main.MainActivity
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FilmDetailFragment : Fragment(R.layout.fragment_film_detail) {

    private val filmDetailViewModel: FilmDetailViewModel by viewModels()

    private lateinit var detailsBinding: FragmentFilmDetailBinding

    @Inject
    internal lateinit var picasso: Picasso

    @Inject
    internal lateinit var filmAdapter: FilmAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        detailsBinding = FragmentFilmDetailBinding.bind(view)
        filmDetailViewModel.filmDetailState.observe(viewLifecycleOwner, ::updateState)
        filmDetailViewModel.filmPopularState.observe(viewLifecycleOwner, ::updatePopularState)
        initView()
    }

    private fun initView() {
        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        detailsBinding.apply {
            recycler.apply {
                layoutManager = linearLayoutManager
                adapter = filmAdapter
            }
            imageviewArrow.setOnClickListener {
                val activity = Intent(context, MainActivity::class.java)
                startActivity(activity)
            }
        }
    }

    private fun updateState(filmDetailState: FilmDetailViewModel.FilmDetailState) {
        when (filmDetailState) {
            is FilmDetailViewModel.FilmDetailState.Success -> displayMovie(filmDetailState.film)
            FilmDetailViewModel.FilmDetailState.Error -> displayError()
        }
    }

    private fun displayMovie(movie: ApiFilm) {
        detailsBinding.apply {
            if (movie.backdropPath != null) {
                picasso.load(FilmService.buildImageUrl(movie.backdropPath)).into(filmImage)
            }
            filmTitle.text = movie.title
            filmOverview.text = movie.overview
        }
    }

    private fun updatePopularState(filmPopularState: FilmDetailViewModel.FilmPopularState) {
        when (filmPopularState) {
            is FilmDetailViewModel.FilmPopularState.Success -> displayResults(filmPopularState.films)
            FilmDetailViewModel.FilmPopularState.Error -> displayError()
        }
    }

    private fun displayResults(results: List<ApiFilm>) {
        filmAdapter.submitList(results)
    }

    private fun displayError() {
        Toast.makeText(context, R.string.connection_error, Toast.LENGTH_SHORT).show()
    }
}
