package com.example.ccl_3.ui.navigation

object Routes {
    const val MAIN = "main"

    const val REGION = "region/{regionName}/{isGlobal}"

    const val DIFFICULTY = "difficulty/{regionName}/{isGlobal}/{gameMode}"

    const val QUIZ = "quiz/{regionName}/{isGlobal}/{gameMode}/{difficulty}"

    const val BOOKMARK_QUIZ = "bookmarkQuiz/{contentType}"

    const val SUMMARY = "summary"

    const val NOTEBOOK = "notebook"
    const val HISTORY = "history"
}
