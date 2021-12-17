package net.barbierdereuille.lightsystem.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import net.barbierdereuille.lightsystem.LightSystemsTag
import net.barbierdereuille.lightsystem.data.Model
import net.barbierdereuille.lightsystem.data.ModelDatabase
import net.barbierdereuille.lightsystem.data.rules

class SeedDatabaseWorker(
  private val context: Context,
  workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {
  @EntryPoint
  @InstallIn(SingletonComponent::class)
  interface HiltEntryPoint {
    val modelDb: ModelDatabase
  }

  override suspend fun doWork(): Result {
    try {
      val appContext =
        checkNotNull(context.applicationContext) { "The context must have an application context" }
      val modelDb =
        EntryPointAccessors.fromApplication(appContext, HiltEntryPoint::class.java).modelDb
      modelDb.addModel(
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
}