package com.keepup.android.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ShowType {
    @SerialName("movie") MOVIE,
    @SerialName("series") SERIES,
    @SerialName("episode") EPISODE,
    @SerialName("other") OTHER;

    val displayName: String
        get() = when (this) {
            MOVIE -> "Movie"
            SERIES -> "TV Show"
            EPISODE -> "Episode"
            OTHER -> "Other"
        }
}

@Serializable
data class WatchProvider(
    val id: Int,
    val name: String,
    @SerialName("logo_path") val logoPath: String? = null
) {
    val logoUrl: String?
        get() = logoPath?.let { "https://image.tmdb.org/t/p/original$it" }
}

@Serializable
data class Source(
    val title: String? = null,
    val url: String? = null
)

@Serializable
data class CastMember(
    val id: Int,
    val name: String,
    val character: String? = null,
    val profilePath: String? = null,
    val order: Int = 0
) {
    val profileUrl: String?
        get() = profilePath?.let { "https://image.tmdb.org/t/p/w185$it" }
}

@Serializable
data class WatchedEpisode(
    val showId: String,
    val seasonNumber: Int,
    val episodeNumber: Int,
    val watchedAt: Long = System.currentTimeMillis()
)

@Serializable
data class WatchProgress(
    val showId: String,
    val watchedEpisodes: List<WatchedEpisode> = emptyList(),
    val lastWatchedSeason: Int? = null,
    val lastWatchedEpisode: Int? = null,
    val updatedAt: Long = System.currentTimeMillis()
)

@Serializable
enum class ReviewTag {
    @SerialName("amazing") AMAZING,
    @SerialName("good") GOOD,
    @SerialName("okay") OKAY,
    @SerialName("disappointing") DISAPPOINTING,
    @SerialName("terrible") TERRIBLE,
    @SerialName("funny") FUNNY,
    @SerialName("emotional") EMOTIONAL,
    @SerialName("thrilling") THRILLING,
    @SerialName("boring") BORING;

    val displayName: String
        get() = when (this) {
            AMAZING -> "Amazing"
            GOOD -> "Good"
            OKAY -> "Okay"
            DISAPPOINTING -> "Disappointing"
            TERRIBLE -> "Terrible"
            FUNNY -> "Funny"
            EMOTIONAL -> "Emotional"
            THRILLING -> "Thrilling"
            BORING -> "Boring"
        }
}

@Serializable
data class Review(
    val id: String,
    val showId: String,
    val userId: String,
    val userName: String,
    val rating: Float, // 0-5
    val text: String,
    val tags: List<ReviewTag> = emptyList(),
    val isSpoiler: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val helpfulCount: Int = 0,
    val notHelpfulCount: Int = 0,
    val userVotedHelpful: Boolean? = null
)

@Serializable
data class ReviewSummary(
    val showId: String,
    val averageRating: Float,
    val totalReviews: Int,
    val ratingDistribution: Map<Int, Int> = emptyMap() // star rating to count
)

@Serializable
data class SeasonInfo(
    val id: Int,
    val seasonNumber: Int,
    val name: String,
    val overview: String? = null,
    val posterPath: String? = null,
    val airDate: String? = null,
    val episodeCount: Int = 0
) {
    val posterUrl: String?
        get() = posterPath?.let { "https://image.tmdb.org/t/p/w300$it" }
}

@Serializable
data class EpisodeInfo(
    val id: Int,
    val episodeNumber: Int,
    val seasonNumber: Int,
    val name: String,
    val overview: String? = null,
    val stillPath: String? = null,
    val airDate: String? = null,
    val runtime: Int? = null,
    val voteAverage: Float? = null
) {
    val stillUrl: String?
        get() = stillPath?.let { "https://image.tmdb.org/t/p/w300$it" }
}

@Serializable
data class Show(
    val id: String,
    val title: String,
    val year: String,
    val type: ShowType,
    val posterUrl: String? = null,
    val backdropUrl: String? = null,
    val plot: String? = null,
    val actors: String? = null,
    val director: String? = null,
    val runtime: String? = null,
    val genre: String? = null,
    val rating: String? = null,
    val trailerKey: String? = null,
    val castMembers: List<CastMember>? = null,
    val watchProviders: List<WatchProvider>? = null,
    val watchProgress: WatchProgress? = null,
    val aiStatus: String? = null,
    val aiSummary: String? = null,
    val aiSources: List<Source>? = null,
    val isCached: Boolean? = null,
    val isNotificationEnabled: Boolean? = null,
    val tmdbStatus: String? = null,
    val lastEpisodeAirDate: String? = null,
    val nextEpisodeAirDate: String? = null,
    val totalSeasons: Int? = null,
    val totalEpisodes: Int? = null,
    val seasons: List<SeasonInfo>? = null,
    val theatricalReleaseDate: String? = null
) {
    val nextAirDate: String?
        get() = nextEpisodeAirDate ?: aiSummary?.substringAfter("on ")
    
    val isInTheaters: Boolean
        get() {
            if (type != ShowType.MOVIE || theatricalReleaseDate == null) return false
            // Simple check: if released within last 90 days
            return true // Simplified for now
        }
}
