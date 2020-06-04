package com.songspagetti.moivemvvm.data.api

import com.songspagetti.moivemvvm.data.vo.MovieDetails
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

/*
만약 아래와 같이 계층 구조로 URL이 되어 있으면
https://sample.com/members/jayden/address
아래와 같이 이용할 수 있다.
@GET("members/{user}/address")
fun getUserAddress(@Path("user") userName: String): Call<Address>

 */
interface TheMovieDBInterface {

    // to get the data from movie db api
    @GET("movie/{movie_id}") // 요청할 API의 URL 일부분
    fun getMovieDetails(@Path("movie_id") id:Int) : Single<MovieDetails> // Single : 결과가 유일한 서버API를 호출할 때주로 사용
                      // ㄴ tell retrofit that this ID is the movie-ID in {movie_id} by using annotation Path movie_id
                      //@Path : 해당 문자열을 URL 부분 일부에 바인딩하여 동적으로 URL을 만들 수 있도록 하는 Annotation


}