package biz.filmeroo.premier.main

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import biz.filmeroo.premier.R
import biz.filmeroo.premier.api.ApiFilm
import biz.filmeroo.premier.databinding.ActivityMainBinding
import biz.filmeroo.premier.detail.FilmDetailActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    internal lateinit var adapter: FilmAdapter

    private val filmViewModel by viewModels<FilmViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setupView(binding)
        filmViewModel.filmState.observe(this, ::updateState)
    }

    private fun setupView(binding: ActivityMainBinding) {
        binding.apply {
            setContentView(root)
            val linearLayoutManager = LinearLayoutManager(this@MainActivity)
            recycler.layoutManager = linearLayoutManager
            recycler.adapter = adapter
            val dividerItemDecoration = DividerItemDecoration(this@MainActivity, linearLayoutManager.orientation).apply {
                setDrawable(AppCompatResources.getDrawable(this@MainActivity, R.drawable.divider)!!)
            }
            recycler.addItemDecoration(dividerItemDecoration)
            adapter.setOnClick { id -> FilmDetailActivity.start(this@MainActivity, id) }
        }
    }

    private fun updateState(filmState: FilmViewModel.FilmState) {
        when (filmState) {
            is FilmViewModel.FilmState.Success -> displayResults(filmState.films)
            FilmViewModel.FilmState.Error -> displayError()
        }
    }

    private fun displayResults(results: List<ApiFilm>) {
        adapter.submitList(results)
    }

    private fun displayError() {
        Toast.makeText(this, R.string.connection_error, Toast.LENGTH_SHORT).show()
    }
}
