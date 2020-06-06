package com.songspagetti.moivemvvm.data.api

import com.songspagetti.moivemvvm.data.vo.MovieDetails
import com.songspagetti.moivemvvm.data.vo.MovieResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/*
만약 아래와 같이 계층 구조로 URL이 되어 있으면
https://sample.com/members/jayden/address
아래와 같이 이용할 수 있다.
@GET("members/{user}/address")
fun getUserAddress(@Path("user") userName: String): Call<Address>
 */
// Retrofit은 Interface에 기술된 명세를 Http API로 전환해 준다. 우선 요청할 API들에 대한 명세만을 Interface에 기술해두면 된다.(추후에 Retrofit Client 객체를 통해 HTTP API 인터페이스의 구현체를 생성할 때 쓰인다.)
interface TheMovieDBInterface {
    // to get the data from movie db api ( https://api.themoviedb.org/3/movie/popular?api_key=801da95f4fd4406b2eaa668892f32971&page=1 )
    @GET("movie/popular")
    fun getPopularMovie(@Query("page") page: Int): Single<MovieResponse>

    // to get the data from movie db api (https://api.themoviedb.org/3/movie/{movie_id}?api_key=801da95f4fd4406b2eaa668892f32971)
    @GET("movie/{movie_id}") // 요청할 API의 URL 일부분, {movie_id}는 @Path와 매칭되는 동적인 변수
    fun getMovieDetails(@Path("movie_id") id:Int) : Single<MovieDetails> // Single : 결과가 유일한 서버API를 호출할 때주로 사용
                      // ㄴ tell retrofit that this ID is the movie-ID in {movie_id} by using annotation Path movie_id
                      //@Path : 해당 문자열을 URL 부분 일부에 바인딩하여 동적으로 URL을 만들 수 있도록 하는 Annotation
                      //@Query, @QueryMap - @GET 에서 사용하며 조건 파라미터를 설정
                      //@Field, @FieldMap - @POST 에서 사용하며 조건 파라미터를 설정
                      //@Body - 객체를 이용하여 조건 파라미터를 설정
                      //@Header - 해더 설정


}