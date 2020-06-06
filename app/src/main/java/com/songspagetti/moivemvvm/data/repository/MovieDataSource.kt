package com.songspagetti.moivemvvm.data.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.songspagetti.moivemvvm.data.api.FIRST_PAGE
import com.songspagetti.moivemvvm.data.api.TheMovieDBInterface
import com.songspagetti.moivemvvm.data.vo.Movie
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

//load data based on page number
class MovieDataSource (private val apiService : TheMovieDBInterface, private val compositeDisposable: CompositeDisposable) : PageKeyedDataSource<Int, Movie>(){

    private var page = FIRST_PAGE

    val networkState: MutableLiveData<NetworkState> = MutableLiveData()


    //request frist page
    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Movie>) {
        networkState.postValue(NetworkState.LOADING)

        compositeDisposable.add(
            apiService.getPopularMovie(page) // this will return a single observable
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        //use Android Paging Library
                        callback.onResult(it.movieList, null, page+1)
                        networkState.postValue(NetworkState.LOADED)
                    },
                    {
                        networkState.postValue(NetworkState.ERROR)
                        Log.e("MovieDataSource", it.message)

                    }
                )
        )
    }
    //request next page. this will be called when user scrolls down
    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) {
        networkState.postValue(NetworkState.LOADING)

        compositeDisposable.add(
            apiService.getPopularMovie(params.key) // this is the next page number and it will be incremented automatically
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        Log.d("here", "subscribe!!!!!!!")
                        if(it.totalPages > params.key){
                            // it means we have more pages to load
                            Log.d("here", "LOADED!!!!!!!!")
                            callback.onResult(it.movieList, params.key+1)
                            networkState.postValue(NetworkState.LOADED)
                        }else{
                            //it means we do not have any more pages
                            Log.d("here", "endOfList!!!!!!!!")
                            networkState.postValue(NetworkState.ENDOFLIST)

                        }
                    },
                    {
                        networkState.postValue(NetworkState.ERROR)
                        Log.e("MovieDataSource", it.message)

                    }
                )
        )
    }
    //load previous page, this will be called when user scrolls up.
    //But we do not have to do anything else here because our recyclerview will hold the previous data
    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) {

    }
}