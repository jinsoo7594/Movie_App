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

// singleton
object TheMovieDBClient {

    fun getClient() : TheMovieDBInterface {
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

            return@Interceptor chain.proceed(request)
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
            .client(okHttpClient)
            .baseUrl(BASE_URL) // 어떤 서버로 네트워크 통신을 요청할 것인지 설정
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) // RxJava 2.0 을 사용하기 위해 추가
            .addConverterFactory(GsonConverterFactory.create()) // 통신 완료 후, 어떤 converter를 이용해 데이터를 파싱할 것인지 설정
            .build() // Retrofit.Builder 객체에 설정한 정보를 이용하여 실질적으로 Retrofit 객체를 생성해 반환한다.
            .create(TheMovieDBInterface::class.java) // 실질적으로 사용할 클라이언트 객체 생성해서 반환 (TheMovieDBInterface 타입) --> 요청에 필요한 Full URL이 완성된다.
            // 이렇게 생성된 클라이언트 객체를 이용하여 요청을 보내고 받아온 응답을 가지고 특정 작업을 하고나면 해당 Retrofit 객체의 사용이 끝난다.
    }

}