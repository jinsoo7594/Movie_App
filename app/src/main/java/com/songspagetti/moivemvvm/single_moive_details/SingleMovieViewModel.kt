package com.songspagetti.moivemvvm.single_moive_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.songspagetti.moivemvvm.data.VO.MovieDetails
import com.songspagetti.moivemvvm.data.repository.NetworkState
import io.reactivex.disposables.CompositeDisposable

class SingleMovieViewModel(private val movieRepository : MovieDetailsRepository, movieId: Int) : ViewModel(){
    // CompositeDisposable : 여러 Disposable을 모아 disposable 메서드를 호출함으로써 가지고 있는 모든 Disposable의 dispose 메서드를 호출할 수 있는 클래스, 즉 복수의 구독을 원하는 시점에 동시에 해제 가능.
    private val compositeDisposable = CompositeDisposable()

    val movieDetails : LiveData<MovieDetails> by lazy {
        movieRepository.fetchSingleMovieDetails(compositeDisposable, movieId)
    }

    val networkState : LiveData<NetworkState> by lazy {
        movieRepository.getMovieDetailsNetworkState()
    }
    // onCleared() is called when the activity or fragment get destroyed
    // It will dispose compositeDisposable, so there won't be any memory leaks.
    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

}