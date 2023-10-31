package biz.filmeroo.premier

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import biz.filmeroo.premier.api.ApiFilm
import biz.filmeroo.premier.databinding.FragmentFilmBinding
import biz.filmeroo.premier.detail.FilmDetailViewModel.Companion.FILM_ID
import biz.filmeroo.premier.main.FilmAdapter
import biz.filmeroo.premier.main.FilmViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FilmFragment : Fragment(R.layout.fragment_film) {

    @Inject
    internal lateinit var adapter: FilmAdapter

    private val filmViewModel: FilmViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val filmBinding = FragmentFilmBinding.bind(view)
        filmViewModel.filmState.observe(viewLifecycleOwner, ::updateState)
        setupView(filmBinding)
    }

    private fun setupView(filmBinding: FragmentFilmBinding) {
        filmBinding.apply {
            val linearLayoutManager = LinearLayoutManager(context)
            recycler.layoutManager = linearLayoutManager
            recycler.adapter = adapter
            val dividerItemDecoration =
                DividerItemDecoration(context, linearLayoutManager.orientation).apply {
                    setDrawable(
                        context?.let {
                            AppCompatResources.getDrawable(it, R.drawable.divider)
                        }!!
                    )
                }
            recycler.addItemDecoration(dividerItemDecoration)
            adapter.setOnClick { id ->
                val bundle = Bundle()
                bundle.putLong(FILM_ID, id)
                findNavController().navigate(R.id.action_filmFragment_to_filmDetailFragment, bundle)
            }
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
        Toast.makeText(context, R.string.connection_error, Toast.LENGTH_SHORT).show()
    }
}
