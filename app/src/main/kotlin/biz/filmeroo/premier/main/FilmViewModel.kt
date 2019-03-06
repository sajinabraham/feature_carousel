package biz.filmeroo.premier.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import biz.filmeroo.premier.api.ApiFilm
import biz.filmeroo.premier.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
internal class FilmViewModel @Inject constructor(
    filmRepository: FilmRepository
) : BaseViewModel() {

    private val _filmState = MutableLiveData<FilmState>()

    val filmState: LiveData<FilmState>
        get() = _filmState

    init {
        addSubscription(
            filmRepository.fetchTopRated()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { _filmState.value = FilmState.Success(it) },
                    { _filmState.value = FilmState.Error }
                )
        )
    }

    sealed interface FilmState {
        data class Success(val films: List<ApiFilm>) : FilmState
        object Error : FilmState
    }
}
