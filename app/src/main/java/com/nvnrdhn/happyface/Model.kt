package com.nvnrdhn.happyface

import androidx.room.Entity
import androidx.room.PrimaryKey
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
    @Entity
    data class History(
        @PrimaryKey(autoGenerate = true) val id: Int,
        val age: Int,
        val angry: Double,
        val disgusted: Double,
        val scared: Double,
        val happy: Double,
        val neutral: Double,
        val sad: Double,
        val surprised: Double,
        val date: String,
        val file_uri: String
    )
}