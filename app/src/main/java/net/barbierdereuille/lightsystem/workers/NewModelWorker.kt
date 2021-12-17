package net.barbierdereuille.lightsystem.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import net.barbierdereuille.lightsystem.LightSystemsTag
import net.barbierdereuille.lightsystem.data.Model
import net.barbierdereuille.lightsystem.data.Repository

@HiltWorker
class NewModelWorker @AssistedInject constructor(
  @Assisted context: Context,
  @Assisted workerParameters: WorkerParameters,
  private val repository: Repository,
) : CoroutineWorker(context, workerParameters) {

  override suspend fun doWork(): Result =
    try {
      val modelName = checkNotNull(inputData.getString(MODEL_NAME))
      val modelAxiom = checkNotNull(inputData.getString(MODEL_AXIOM))
      repository.addModel(Model(name = modelName, axiom = modelAxiom, rules = emptyList()))
      Result.success()
    } catch (t: Throwable) {
      Log.e(LightSystemsTag, "Error adding model to database", t)
      Result.failure()
    }

  companion object {
    const val MODEL_NAME = "MODEL_NAME"
    const val MODEL_AXIOM = "MODEL_AXIOM"

    private fun createRequest(name: String, axiom: String) =
      OneTimeWorkRequestBuilder<NewModelWorker>()
        .setInputData(workDataOf(MODEL_NAME to name, MODEL_AXIOM to axiom))
        .build()

    fun execute(context: Context, name: String, axiom: String) =
      WorkManager.getInstance(checkNotNull(context.applicationContext))
        .enqueue(createRequest(name, axiom))
  }
}