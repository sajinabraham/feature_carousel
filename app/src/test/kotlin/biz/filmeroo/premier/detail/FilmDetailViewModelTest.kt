package biz.filmeroo.premier.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import biz.filmeroo.premier.api.ApiFilm
import biz.filmeroo.premier.detail.FilmDetailViewModel.Companion.FILM_ID
import biz.filmeroo.premier.detail.FilmDetailViewModel.FilmDetailState
import biz.filmeroo.premier.main.FilmRepository
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class FilmDetailViewModelTest {

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    private val repository: FilmRepository = mock()
    private val savedStateHandle: SavedStateHandle = mock()

    private lateinit var viewModel: FilmDetailViewModel

    @Before
    fun setUp() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
    }

    @Test
    fun `on success show film details`() {
        val film = mock<ApiFilm>()
        val filmId = 2L
        whenever(savedStateHandle.get<Long>(FILM_ID)).thenReturn(filmId)
        whenever(repository.fetchMovie(filmId)).thenReturn(Single.just(film))

        viewModel = FilmDetailViewModel(savedStateHandle, repository)

        assertThat(viewModel.filmDetailState.value).isEqualTo(FilmDetailState.Success(film))
    }
}
