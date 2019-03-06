package biz.filmeroo.premier.main

import biz.filmeroo.premier.api.ApiFilm
import biz.filmeroo.premier.api.FilmService
import biz.filmeroo.premier.support.Fixtures
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Single
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class FilmRepositoryTest {

    private val service: FilmService = mock()

    private lateinit var repository: FilmRepository

    @Before
    fun setUp() {
        repository = FilmRepository(service)
    }

    @Test
    fun `fetches top rated films and returns list single`() {
        whenever(service.topRated()).thenReturn(Single.just(Fixtures.filmResponse()))

        val observer = repository.fetchTopRated().test()
        val results = observer.values()[0]

        assertThat(results).hasSize(2)
        assertThat(results[0].id).isEqualTo(123)
    }

    @Test
    fun `should fetch movie details`() {
        val movie = mock<ApiFilm>()
        whenever(service.movie(2)).thenReturn(Single.just(movie))

        val result = repository.fetchMovie(2).test()

        verify(service).movie(2)
        assertThat(result.values()[0]).isEqualTo(movie)
    }
}
