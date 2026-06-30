package com.allvideodownloader.hdvideodownloader.securevideosaver.framework.push

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging

interface FcmTopicManager {
    fun subscribeToDefaultTopics()
}

class FirebaseFcmTopicManager(
    private val firebaseMessaging: FirebaseMessaging
) : FcmTopicManager {

    override fun subscribeToDefaultTopics() {
        firebaseMessaging
            .subscribeToTopic(TOPIC_ANDROID_USERS)
            .addOnSuccessListener {
                Log.d(TAG, "Subscribed to topic: $TOPIC_ANDROID_USERS")
            }
            .addOnFailureListener { error ->
                Log.e(TAG, "Failed to subscribe to topic: $TOPIC_ANDROID_USERS", error)
            }
    }

    companion object {
        private const val TAG = "FcmTopicManager"
        const val TOPIC_ANDROID_USERS = "android_users"
    }
}