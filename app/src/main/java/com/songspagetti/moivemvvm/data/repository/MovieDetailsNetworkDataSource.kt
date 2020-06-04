package com.songspagetti.moivemvvm.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.songspagetti.moivemvvm.data.api.TheMovieDBInterface
import com.songspagetti.moivemvvm.data.vo.MovieDetails
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

// here let's call API by using RxJava and API will return the MovieDetails
// and then assign the MovieDetails in a LiveData

//CompositeDisposable : RxJava component we can use to dispose our API calls, so when we want to dispose a RxJava-thread, we can use CompositeDisposable.
class MovieDetailsNetworkDataSource (private val apiService : TheMovieDBInterface, private val compositeDisposable: CompositeDisposable) {
    //undercore means it is private
    private val _networkState = MutableLiveData<NetworkState>()
    val networkState: LiveData<NetworkState>
        get() = _networkState // with Get(), no need to implement get function to get networkState
    // now LiveData<NetworkState> will be called every time you access networkState variable.
    // so do not need to implement setter and getter
    //일반적으로 수정가능한 MutableLiveData는 ViewModel 내부에서 사용되고, ViewModel은 수정 불가능한 LiveData로 관찰자에게 노출.
    //setValue() / postValue() 메서드가 호출된 다음에는 관찰자를 트리거하여 UI를 갱신하도록 한다.

    private val _downloadedMovieDetailResponse = MutableLiveData<MovieDetails>()
    val downloadedMovieResponse: LiveData<MovieDetails>
        get() = _downloadedMovieDetailResponse

    fun fetchMovieDetails(movieId: Int){
        _networkState.postValue(NetworkState.LOADING)

        //try-catch block use RxJava-thread to make network calls
        // I want this thread to be disposable

        //subscribeOn()  :  Observable에서 구독자가 subscribe() 함수를 호출했을 때, 데이터 흐름을 발행하는 스레드를 지정, 처음 지정한 스레드를 고정시키므로 다시 subscribeOn()  함수를 호출해도 무시. (하지만, observeOn() 함수는 다름)
        //observeOn()     :  처리된 결과를 구독자에게 전달하는 스레드 지정. 여러번 호출할 수 있으며, 호출되면 그 다음부터 동작하는 스레드를 바꿀 수 있다.
        try{
            //Schedulers.io() : 네트워크상의 요청을 처리하거나 각종 입,출력(네트워크상의 요청, 파일 입출력, DB 쿼리 등) 작업을 실행하기 위한 스케줄러,필요할 때마다 스레드를 계속 생성함.
            compositeDisposable.add(
                apiService.getMovieDetails(movieId) // returns a single observable
                    .subscribeOn(Schedulers.io()) //Schedulers.io() is a thread pool, subscriber are subscribing on Scheduler.io() thread pool to observe the network call
                    .subscribe( // two parameters, for suceess and trouble
                        {
                            _downloadedMovieDetailResponse.postValue(it)
                            _networkState.postValue(NetworkState.LOADED)
                        },
                        {
                            _networkState.postValue(NetworkState.ERROR)
                            Log.e("MovieDetailsDataSource1", it.message)
                        }
                    )
            )
        }
        catch (e:Exception){
            Log.e("MovieDetailsDataSource2", e.message)
        }




    }

}