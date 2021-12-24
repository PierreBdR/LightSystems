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
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@HiltWorker
class SaveModelWorker @AssistedInject constructor(
  @Assisted context: Context,
  @Assisted workerParameters: WorkerParameters,
  private val repository: Repository,
) : CoroutineWorker(context, workerParameters) {

  override suspend fun doWork(): Result =
    try {
      val jsonModel = checkNotNull(inputData.getString(MODEL_JSON))
      val model = Json.decodeFromString<Model>(jsonModel)
      if(model.id == 0L) {
        Log.i(LightSystemsTag, "Adding new model: $model")
        repository.addModel(model)
      } else {
        Log.i(LightSystemsTag, "Updating model $model")
        repository.updateModel(model)
      }
      Log.i(LightSystemsTag, "Update done for model ${model.id}")
      Result.success()
    } catch (t: Throwable) {
      Log.e(LightSystemsTag, "Error adding model to database", t)
      Result.failure()
    }

  companion object {
    const val MODEL_JSON = "MODEL_JSON"

    private fun createRequest(model: Model) =
      OneTimeWorkRequestBuilder<SaveModelWorker>()
        .setInputData(workDataOf(MODEL_JSON to Json.encodeToString(model)))
        .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
        .build()

    fun execute(context: Context, model: Model) =
      WorkManager.getInstance(checkNotNull(context.applicationContext))
        .enqueueUniqueWork(
          "updateModel-${model.id}",
          ExistingWorkPolicy.REPLACE,
          createRequest(model)
        )
  }
}