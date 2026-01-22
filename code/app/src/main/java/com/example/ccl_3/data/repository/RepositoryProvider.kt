package com.example.ccl_3.data.repository

import android.content.Context
import com.example.ccl_3.data.db.DatabaseProvider

object RepositoryProvider {

    private var quizRepository: QuizRepository? = null
    private var roundRepository: RoundRepository? = null
    private var roundResultRepository: RoundResultRepository? = null

    fun provideQuizRepository(context: Context): QuizRepository {
        if (quizRepository == null) {
            quizRepository = QuizRepository(context.applicationContext)
        }
        return quizRepository!!
    }

    fun provideRoundRepository(context: Context): RoundRepository {
        if (roundRepository == null) {
            roundRepository = RoundRepository(
                DatabaseProvider.getDatabase(context).roundStateDao()
            )
        }
        return roundRepository!!
    }

    fun provideRoundResultRepository(context: Context): RoundResultRepository {
        if (roundResultRepository == null) {
            roundResultRepository = RoundResultRepository(
                DatabaseProvider.getDatabase(context).roundResultDao()
            )
        }
        return roundResultRepository!!
    }
}
