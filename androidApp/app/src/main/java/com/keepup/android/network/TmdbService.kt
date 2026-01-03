package com.keepup.android.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbService {
    @GET("search/multi")
    suspend fun search(@Query("query") query: String): TmdbSearchResponse

    @GET("movie/{id}")
    suspend fun movie(
        @Path("id") id: String,
        @Query("append_to_response") append: String = "credits,videos,watch/providers"
    ): TmdbMovieDetail

    @GET("tv/{id}")
    suspend fun tv(
        @Path("id") id: String,
        @Query("append_to_response") append: String = "credits,videos,watch/providers"
    ): TmdbTvDetail

    @GET("movie/{id}/watch/providers")
    suspend fun movieProviders(@Path("id") id: String): TmdbWatchProvidersResponse

    @GET("tv/{id}/watch/providers")
    suspend fun tvProviders(@Path("id") id: String): TmdbWatchProvidersResponse
    
    @GET("movie/{id}/release_dates")
    suspend fun movieReleaseDates(@Path("id") id: String): TmdbReleaseDatesResponse
    
    @GET("tv/{id}/season/{season_number}")
    suspend fun tvSeason(
        @Path("id") id: String,
        @Path("season_number") seasonNumber: Int
    ): TmdbSeasonDetail
    
    @GET("movie/{id}/videos")
    suspend fun movieVideos(@Path("id") id: String): TmdbVideosResponse
    
    @GET("tv/{id}/videos")
    suspend fun tvVideos(@Path("id") id: String): TmdbVideosResponse
    
    @GET("search/movie")
    suspend fun searchMovies(@Query("query") query: String): TmdbSearchResponse
    
    @GET("search/tv")
    suspend fun searchTv(@Query("query") query: String): TmdbSearchResponse
}
