package com.nvnrdhn.happyface

import java.io.Serializable

object Model {
    data class FaceData(val faceAttributes: FaceAttributes): Serializable
    data class FaceAttributes(
        val emotion: Emotion,
        val age: Int
    ): Serializable
    data class Emotion(
        val anger: Double,
        val disgust: Double,
        val fear: Double,
        val happiness: Double,
        val neutral: Double,
        val sadness: Double,
        val surprise: Double
    ): Serializable
}