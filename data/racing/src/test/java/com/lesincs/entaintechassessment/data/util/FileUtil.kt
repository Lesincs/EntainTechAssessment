package com.lesincs.entaintechassessment.data.util

fun loadResponseFromFile(
    filePath: String
): String =
    ClassLoader.getSystemResourceAsStream(filePath)?.bufferedReader()?.use { it.readText() }
        ?: throw IllegalArgumentException("Could not find file $filePath.")
