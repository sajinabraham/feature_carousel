package biz.filmeroo.premier.main

import biz.filmeroo.premier.api.ApiFilm
import biz.filmeroo.premier.api.FilmService
import io.reactivex.Single
import javax.inject.Inject

internal class FilmRepository @Inject constructor(private val filmService: FilmService) {

    fun fetchTopRated(): Single<List<ApiFilm>> {
        return filmService.topRated()
            .map { it.results }
    }

    fun fetchMovie(id: Long): Single<ApiFilm> {
        return filmService.movie(id)
    }
}
