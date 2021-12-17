package net.barbierdereuille.lightsystem.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import dagger.Lazy
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import net.barbierdereuille.lightsystem.LightSystemsTag
import net.barbierdereuille.lightsystem.data.Model
import net.barbierdereuille.lightsystem.data.Repository
import net.barbierdereuille.lightsystem.data.rules

@HiltWorker
class SeedDatabaseWorker @AssistedInject constructor(
  @Assisted context: Context,
  @Assisted workerParameters: WorkerParameters,
  private val repository: Lazy<Repository>,
) : CoroutineWorker(context, workerParameters) {

  override suspend fun doWork(): Result {
    try {
      val repo = repository.get()
      if(inputData.getBoolean(RESET_DB, false)) {
        repo.clearAll()
      }
      repo.addModel(
        Model(
          name = "Anabeana",
          axiom = "R",
          rules = rules(
            "r" to "R",
            "l" to "L",
            "R" to "Lr",
            "L" to "lR",
          ),
        )
      )
    } catch (t: Throwable) {
      Log.e(LightSystemsTag, "Error seeding database", t)
      return Result.failure()
    }
    return Result.success()
  }

  companion object {
    private const val RESET_DB = "RESET_DB"
    private fun createRequest(reset: Boolean) =
      OneTimeWorkRequestBuilder<SeedDatabaseWorker>()
        .setInputData(workDataOf(RESET_DB to reset))
        .build()

    fun execute(context: Context, reset: Boolean = false) =
      WorkManager.getInstance(checkNotNull(context.applicationContext))
        .enqueue(createRequest(reset))
  }
}