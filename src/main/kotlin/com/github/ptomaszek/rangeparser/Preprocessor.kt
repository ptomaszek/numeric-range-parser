package com.github.ptomaszek.rangeparser

interface Preprocessor {
    fun process(processedInput: String): String
}

class SpacesRemovalPreprocessor : Preprocessor {
    override fun process(processedInput: String): String {
        return processedInput.replace(" ", "")
    }
}