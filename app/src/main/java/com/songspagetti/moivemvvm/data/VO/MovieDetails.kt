package com.songspagetti.moivemvvm.data.VO


import com.google.gson.annotations.SerializedName
//GSON : JSON 구조를 JAVA 객체로 직렬화, 역직렬화 해주는 라이브러리
//Retrofit2는 GSON을 제공하기 때문에 응답바디와 동일하게 data class를 만들어두면 응답 바디에 있는 json을 자동으로 파싱하여 객체로 받을 수 있다.
data class MovieDetails(
    val budget: Int,
    val homepage: String,
    val id: Int,
    val overview: String,
    val popularity: Double,
    @SerializedName("poster_path") // value : 객체를 직렬화/역직렬화 할때 이름으로 사용
    val posterPath: String, // posterPath 는 JSON 에서 poster_path로 표출된다.
    @SerializedName("release_date")
    val releaseDate: String,
    val revenue: Long,
    val runtime: Int,
    val status: String,
    val tagline: String,
    val title: String,
    val video: Boolean,
    @SerializedName("vote_average")
    val rating: Double
)