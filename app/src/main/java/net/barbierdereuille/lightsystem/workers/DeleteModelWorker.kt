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
import net.barbierdereuille.lightsystem.data.Model
import net.barbierdereuille.lightsystem.data.Repository

@HiltWorker
class DeleteModelWorker @AssistedInject constructor(
  @Assisted context: Context,
  @Assisted workerParameters: WorkerParameters,
  private val repository: Repository,
) : CoroutineWorker(context, workerParameters) {

  override suspend fun doWork(): Result =
    try {
      val modelId = checkNotNull(inputData.getLong(MODEL_ID, Model.NO_ID))
      Log.i(LightSystemsTag, "Deleting model $modelId")
      repository.deleteModel(modelId)
      Result.success()
    } catch (t: Throwable) {
      Log.e(LightSystemsTag, "Error adding model to database", t)
      Result.failure()
    }

  companion object {
    const val MODEL_ID = "MODEL_ID"

    private fun createRequest(model: Model) =
      OneTimeWorkRequestBuilder<DeleteModelWorker>()
        .setInputData(workDataOf(MODEL_ID to model.id))
        .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
        .build()

    fun execute(context: Context, model: Model) =
      WorkManager.getInstance(checkNotNull(context.applicationContext))
        .enqueueUniqueWork("deleteModel-${model.id}", ExistingWorkPolicy.KEEP, createRequest(model))
  }
}