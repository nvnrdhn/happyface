package com.nvnrdhn.happyface

import com.google.gson.annotations.SerializedName
import java.io.Serializable

object Model {
    data class FaceData(val faceAttributes: FaceAttributes): Serializable
    data class FaceAttributes(
        val emotion: Emotion,
        val age: Int
    ): Serializable
    data class Emotion(
        @SerializedName("anger")
        val angry: Double,
        @SerializedName("disgust")
        val disgusted: Double,
        @SerializedName("fear")
        val scared: Double,
        @SerializedName("happiness")
        val happy: Double,
        val neutral: Double,
        @SerializedName("sadness")
        val sad: Double,
        @SerializedName("surprise")
        val surprised: Double
    ): Serializable
    data class EmotionList(
        val name: String,
        val value: Int
    )
}