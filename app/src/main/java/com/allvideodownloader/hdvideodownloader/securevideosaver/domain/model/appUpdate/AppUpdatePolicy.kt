package com.allvideodownloader.hdvideodownloader.securevideosaver.domain.model.appUpdate

data class AppUpdatePolicy(
    val immediateUpdatePriorityThreshold: Int = 4,
    val immediateUpdateStalenessDaysThreshold: Int = 3,
)