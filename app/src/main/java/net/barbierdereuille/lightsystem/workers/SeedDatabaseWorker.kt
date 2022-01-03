package net.barbierdereuille.lightsystem.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import net.barbierdereuille.lightsystem.LightSystemsTag
import net.barbierdereuille.lightsystem.data.Repository

@HiltWorker
class SeedDatabaseWorker @AssistedInject constructor(
  @Assisted context: Context,
  @Assisted workerParameters: WorkerParameters,
  private val repository: Repository,
) : CoroutineWorker(context, workerParameters) {

  override suspend fun doWork(): Result {
    try {
      if(inputData.getBoolean(RESET_DB, false)) {
        repository.clearAll()
      }
    /*
      listOf(
        Model(
          name = "Anabeana",
          start = "R",
          rules = rules(
            "r" to "R",
            "l" to "L",
            "R" to "Lr",
            "L" to "lR",
          ),
        ),
        Model(
          name = "RuleTest",
          start = "SOME TexT",
          rules = rules(
            "sh" to "Some rules",
            "longer" to "fine",
            "norm" to "Some very long text that should fit in more than one line",
          )
        ),
      ).forEach {
        Log.i(LightSystemsTag, "Adding ${it.name}")
        repository.addModel(it)
      }*/
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
        .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
        .build()

    fun execute(context: Context, reset: Boolean = false) =
      WorkManager.getInstance(checkNotNull(context.applicationContext))
        .enqueueUniqueWork("resetDb", ExistingWorkPolicy.KEEP, createRequest(reset))
  }
}