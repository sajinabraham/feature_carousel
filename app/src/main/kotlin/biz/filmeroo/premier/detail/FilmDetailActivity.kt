package biz.filmeroo.premier.detail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import biz.filmeroo.premier.R
import biz.filmeroo.premier.api.ApiFilm
import biz.filmeroo.premier.api.FilmService
import biz.filmeroo.premier.databinding.ActivityDetailBinding
import biz.filmeroo.premier.detail.FilmDetailViewModel.FilmDetailState
import biz.filmeroo.premier.main.FilmAdapter
import biz.filmeroo.premier.main.MainActivity
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FilmDetailActivity : AppCompatActivity() {

    @Inject
    internal lateinit var picasso: Picasso

    @Inject
    internal lateinit var filmAdapter: FilmAdapter

    private lateinit var detailsBinding: ActivityDetailBinding

    private val filmDetailViewModel by viewModels<FilmDetailViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detailsBinding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(detailsBinding.root)
        setSupportActionBar(detailsBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        detailsBinding.toolbar.setNavigationOnClickListener { finish() }
        initView()
        filmDetailViewModel.filmDetailState.observe(this, ::updateState)
        filmDetailViewModel.filmPopularState.observe(this, ::updatePopularState)
    }

    private fun initView() {
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        detailsBinding.apply {
            recycler.apply {
                layoutManager = linearLayoutManager
                adapter = filmAdapter
            }
            imageviewArrow.setOnClickListener {
                val activity = Intent(this@FilmDetailActivity, MainActivity::class.java)
                startActivity(activity)
            }
        }
    }

    private fun updateState(filmDetailState: FilmDetailState) {
        when (filmDetailState) {
            is FilmDetailState.Success -> displayMovie(filmDetailState.film)
            FilmDetailState.Error -> displayError()
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
        Toast.makeText(this, R.string.connection_error, Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun start(origin: Activity, id: Long) {
            origin.startActivity(
                Intent(origin, FilmDetailActivity::class.java).apply {
                    putExtra(FilmDetailViewModel.FILM_ID, id)
                }
            )
        }
    }
}
