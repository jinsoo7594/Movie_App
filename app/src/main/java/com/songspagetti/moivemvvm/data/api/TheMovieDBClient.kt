package com.songspagetti.moivemvvm.data.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

const val API_KEY = "801da95f4fd4406b2eaa668892f32971"
const val BASE_URL = "https://api.themoviedb.org/3/"
// POSETER_BASE_URL + "poster_path" => image url  레트로핏에는 base URL 이 필요하다.
const val POSTER_BASE_URL = "https://image.tmdb.org/t/p/w342/"
//Send request like this : https://api.themoviedb.org/3/movie/{movie_id}?api_key=<<api_key>>
//                         https://api.themoviedb.org/3/movie/299534?api_key=801da95f4fd4406b2eaa668892f32971
const val FIRST_PAGE = 1  // if you want to check whether ENDOFLIST works or not, set FIRST_PAGE = 497
const val POST_PER_PAGE = 20


// singleton
object TheMovieDBClient {

    fun getClient() : TheMovieDBInterface {
        // Interceptor : 네트워크 통신을 하는 중간에 무언가를 공통적으로 실어 보내거나 받아 써야할 때 사용한다.(Application Interceptor/ Network Interceptor)
        //Interceptor 개념은 OkHttpClient 라이브러리가 적용된 프로젝트에만 국한된 개념이다.
        //Application Interceptor
        // create an interceptor to put API key in the URL
        val requestInterceptor = Interceptor { chain ->
            // put API key in the URL
            val url = chain.request()
                .url()
                .newBuilder()
                .addQueryParameter("api_key", API_KEY)
                .build()
            // build the request with URL
            val request = chain.request()
                .newBuilder()
                .url(url)
                .build()

            return@Interceptor chain.proceed(request) // response 반환
        }
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(requestInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .build()
            //Retrofit을 이용한 REST API 통신을 위해서는 2가지의 선행작업이 필요하다.
            //1. Retrofit 객체 생성
            //2. REST API 명세에 맞는 Interface 선언
            //Retrofit객체 설정 -> API 규격에 맞는 Interface 선언 -> Retrofit객체와 Interface를 이용한 클라이언트 객체 생성
        return Retrofit.Builder()
            .client(okHttpClient) // Connection 에 가장 큰 영향을 준다.  - read/write/connect Timeout 설정 - ping 인터벌 설정 - proxy와 proxy Selector설정 - 기본 / SSL용 Socket Factory 설정 - Authenticator 설정 - Dispatcher 설정 - Interceptor 설정 - 네트워크 Interceptor 설정 - 캐시설정 - Cookie Jar 설정
            .baseUrl(BASE_URL) // 어떤 서버로 네트워크 통신을 요청할 것인지 설정
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // Call<T> 대신 RxJava에서 사용되는 observables 객체를 리턴할 수 있도록 해준다.(Observable<T>, Flowable<T>, Single<T>, Maybe<T>, Completable 사용 가능)
            .addConverterFactory(GsonConverterFactory.create()) // 통신 완료 후, 어떤 converter를 이용해 데이터를 파싱할 것인지 설정(Json 응답을 객체로 변환하기 위한 과정 )
            .build() //  Retrofit Client 객체를 생성해 반환한다.
            .create(TheMovieDBInterface::class.java) // Retrofit Client를 이용하여 Http API 명세가 담긴 Interface의 구현체를 생성해서 반환 (TheMovieDBInterface 타입) --> 요청에 필요한 Full URL이 완성된다.
            // 이렇게 생성된 클라이언트 객체를 이용하여 요청을 보내고 받아온 응답을 가지고 특정 작업을 하고나면 해당 Retrofit 객체의 사용이 끝난다.
    }

}