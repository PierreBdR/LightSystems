package net.barbierdereuille.lightsystem.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.barbierdereuille.lightsystem.workers.SeedDatabaseWorker

@Database(
  entities = [ModelDescription::class, RuleDefinition::class],
  version = 1,
  exportSchema = false
)
abstract class ModelDatabase : RoomDatabase() {
  internal abstract fun modelDao(): ModelDao

  fun addModel(model: Model): Model {
    requireNotNull(model.rules) { "To add a model, the rules need to be specified, even if empty." }
    val dao = modelDao()
    val modelId = dao.add(model.toDb())
    val ruleIds = dao.addAll(model.rules.map { it.toDb(modelId) })
    return model.copy(
      id = modelId,
      rules = model.rules.mapIndexed { index, rule -> rule.copy(id = ruleIds[index]) })
  }

  fun allModels(): Flow<List<Model>> =
    modelDao().allModels().map { it.toModels() }
}

private fun List<ModelDescription>.toModels(): List<Model> = map { it.toModel() }

@Module
@InstallIn(SingletonComponent::class)
private object ModelDatabaseModel {
  @Provides
  @Singleton
  fun providesDatabase(
    @ApplicationContext context: Context,
    workManager: WorkManager
  ): ModelDatabase =
    Room.databaseBuilder(context, ModelDatabase::class.java, "model-db")
      .addCallback(
        object : RoomDatabase.Callback() {
          override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            val request = OneTimeWorkRequestBuilder<SeedDatabaseWorker>().build()
            workManager.enqueue(request)
          }
        }
      ).build()
}
