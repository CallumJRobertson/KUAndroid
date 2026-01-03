package com.keepup.android.firestore

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.keepup.android.data.Review
import com.keepup.android.data.ReviewSummary
import com.keepup.android.data.Show
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Firestore manager for cloud sync and data storage
 */
class FirestoreManager {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    
    private val currentUserId: String?
        get() = auth.currentUser?.uid
    
    /**
     * Sync tracked shows to cloud
     */
    suspend fun syncTrackedShows(shows: List<Show>): Result<Unit> {
        val userId = currentUserId ?: return Result.failure(Exception("Not signed in"))
        return try {
            val showIds = shows.map { it.id }
            val data = hashMapOf(
                "showIds" to showIds,
                "updatedAt" to System.currentTimeMillis()
            )
            firestore.collection("users")
                .document(userId)
                .collection("tracked")
                .document("shows")
                .set(data)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get tracked shows from cloud
     */
    suspend fun getTrackedShows(): Result<List<String>> {
        val userId = currentUserId ?: return Result.failure(Exception("Not signed in"))
        return try {
            val doc = firestore.collection("users")
                .document(userId)
                .collection("tracked")
                .document("shows")
                .get()
                .await()
            
            @Suppress("UNCHECKED_CAST")
            val showIds = doc.get("showIds") as? List<String> ?: emptyList()
            Result.success(showIds)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Submit a review
     */
    suspend fun submitReview(review: Review): Result<Unit> {
        return try {
            firestore.collection("reviews")
                .document(review.id)
                .set(review)
                .await()
            
            // Update review summary
            updateReviewSummary(review.showId)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get reviews for a show
     */
    fun getReviews(
        showId: String,
        sortBy: String = "createdAt"
    ): Flow<List<Review>> = callbackFlow {
        val query = firestore.collection("reviews")
            .whereEqualTo("showId", showId)
            .orderBy(sortBy, Query.Direction.DESCENDING)
        
        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            
            val reviews = snapshot?.documents?.mapNotNull { doc ->
                try {
                    doc.toObject(Review::class.java)
                } catch (e: Exception) {
                    null
                }
            } ?: emptyList()
            
            trySend(reviews)
        }
        
        awaitClose { listener.remove() }
    }
    
    /**
     * Get review summary for a show
     */
    suspend fun getReviewSummary(showId: String): Result<ReviewSummary?> {
        return try {
            val doc = firestore.collection("reviewSummaries")
                .document(showId)
                .get()
                .await()
            
            val summary = doc.toObject(ReviewSummary::class.java)
            Result.success(summary)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Vote on a review (helpful/not helpful)
     */
    suspend fun voteOnReview(reviewId: String, isHelpful: Boolean): Result<Unit> {
        val userId = currentUserId ?: return Result.failure(Exception("Not signed in"))
        return try {
            val reviewRef = firestore.collection("reviews").document(reviewId)
            val voteRef = reviewRef.collection("votes").document(userId)
            
            firestore.runTransaction { transaction ->
                val review = transaction.get(reviewRef)
                val existingVote = transaction.get(voteRef)
                
                val currentHelpful = review.getLong("helpfulCount")?.toInt() ?: 0
                val currentNotHelpful = review.getLong("notHelpfulCount")?.toInt() ?: 0
                val wasHelpful = existingVote.getBoolean("isHelpful")
                
                // Update counts based on previous vote
                var newHelpful = currentHelpful
                var newNotHelpful = currentNotHelpful
                
                if (wasHelpful != null) {
                    // Remove old vote
                    if (wasHelpful) newHelpful-- else newNotHelpful--
                }
                
                // Add new vote
                if (isHelpful) newHelpful++ else newNotHelpful++
                
                // Update review
                transaction.update(reviewRef, mapOf(
                    "helpfulCount" to newHelpful,
                    "notHelpfulCount" to newNotHelpful
                ))
                
                // Update vote record
                transaction.set(voteRef, mapOf("isHelpful" to isHelpful))
            }.await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Submit a bug report
     */
    suspend fun submitBugReport(
        title: String,
        description: String,
        deviceInfo: String
    ): Result<Unit> {
        val userId = currentUserId
        return try {
            val report = hashMapOf(
                "userId" to userId,
                "title" to title,
                "description" to description,
                "deviceInfo" to deviceInfo,
                "createdAt" to System.currentTimeMillis(),
                "status" to "open"
            )
            
            firestore.collection("bugReports")
                .add(report)
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Update review summary for a show
     */
    private suspend fun updateReviewSummary(showId: String) {
        try {
            val reviews = firestore.collection("reviews")
                .whereEqualTo("showId", showId)
                .get()
                .await()
            
            val reviewsList = reviews.documents.mapNotNull { 
                it.toObject(Review::class.java) 
            }
            
            if (reviewsList.isEmpty()) return
            
            val avgRating = reviewsList.map { it.rating }.average().toFloat()
            val totalReviews = reviewsList.size
            
            // Calculate rating distribution
            val distribution = reviewsList
                .groupBy { it.rating.toInt() }
                .mapValues { it.value.size }
            
            val summary = ReviewSummary(
                showId = showId,
                averageRating = avgRating,
                totalReviews = totalReviews,
                ratingDistribution = distribution
            )
            
            firestore.collection("reviewSummaries")
                .document(showId)
                .set(summary)
                .await()
        } catch (e: Exception) {
            // Log but don't fail
            e.printStackTrace()
        }
    }
    
    /**
     * Save watch progress for a TV show
     */
    suspend fun saveWatchProgress(showId: String, seasonNumber: Int, episodeNumber: Int): Result<Unit> {
        val userId = currentUserId ?: return Result.failure(Exception("Not signed in"))
        return try {
            val progressRef = firestore.collection("users")
                .document(userId)
                .collection("watchProgress")
                .document(showId)
            
            val doc = progressRef.get().await()
            val existingEpisodes = doc.get("watchedEpisodes") as? List<Map<String, Any>> ?: emptyList()
            
            val newEpisode = mapOf(
                "showId" to showId,
                "seasonNumber" to seasonNumber,
                "episodeNumber" to episodeNumber,
                "watchedAt" to System.currentTimeMillis()
            )
            
            val updatedEpisodes = existingEpisodes.toMutableList()
            updatedEpisodes.removeAll { 
                val s = it["seasonNumber"] as? Long
                val e = it["episodeNumber"] as? Long
                s == seasonNumber.toLong() && e == episodeNumber.toLong()
            }
            updatedEpisodes.add(newEpisode)
            
            progressRef.set(mapOf(
                "showId" to showId,
                "watchedEpisodes" to updatedEpisodes,
                "lastWatchedSeason" to seasonNumber,
                "lastWatchedEpisode" to episodeNumber,
                "updatedAt" to System.currentTimeMillis()
            )).await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Get watch progress for a TV show
     */
    suspend fun getWatchProgress(showId: String): Result<Map<String, Any>?> {
        val userId = currentUserId ?: return Result.failure(Exception("Not signed in"))
        return try {
            val doc = firestore.collection("users")
                .document(userId)
                .collection("watchProgress")
                .document(showId)
                .get()
                .await()
            
            Result.success(doc.data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
