package com.songspagetti.moivemvvm.single_moive_details

import androidx.lifecycle.LiveData
import com.songspagetti.moivemvvm.data.VO.MovieDetails
import com.songspagetti.moivemvvm.data.api.TheMovieDBInterface
import com.songspagetti.moivemvvm.data.repository.MovieDetailsNetworkDataSource
import com.songspagetti.moivemvvm.data.repository.NetworkState
import io.reactivex.disposables.CompositeDisposable
// to cache the data in local storage
class MovieDetailsRepository (private val apiService : TheMovieDBInterface) {

    lateinit var movieDetailsNetworkDataSource: MovieDetailsNetworkDataSource

    fun fetchSingleMovieDetails (compositeDisposable: CompositeDisposable, movieId: Int) : LiveData<MovieDetails>{

        movieDetailsNetworkDataSource = MovieDetailsNetworkDataSource(apiService, compositeDisposable)
        movieDetailsNetworkDataSource.fetchMovieDetails(movieId)

        return movieDetailsNetworkDataSource.downloadedMovieResponse
    }

    fun getMovieDetailsNetworkState(): LiveData<NetworkState>{
        return movieDetailsNetworkDataSource.networkState
    }


}