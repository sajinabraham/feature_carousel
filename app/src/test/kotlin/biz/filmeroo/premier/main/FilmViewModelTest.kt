package biz.filmeroo.premier.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import biz.filmeroo.premier.main.FilmViewModel.FilmState
import biz.filmeroo.premier.support.Fixtures
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

class FilmViewModelTest {

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    private val repository: FilmRepository = mock()

    private lateinit var viewModel: FilmViewModel

    @Before
    fun setUp() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
    }

    @Test
    fun `on success show success state with films`() {
        val results = Fixtures.filmList()
        whenever(repository.fetchTopRated()).thenReturn(Single.just(results))

        viewModel = FilmViewModel(repository)

        assertThat(viewModel.filmState.value).isEqualTo(FilmState.Success(results))
    }

    @Test
    fun `on error show error state`() {
        whenever(repository.fetchTopRated()).thenReturn(Single.error(Exception()))

        viewModel = FilmViewModel(repository)

        assertThat(viewModel.filmState.value).isEqualTo(FilmState.Error)
    }
}
