package net.barbierdereuille.lightsystem.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import net.barbierdereuille.lightsystem.workers.SeedDatabaseWorker

@Database(
  entities = [ModelDescription::class, RuleDefinition::class, ModelRules::class],
  version = 1,
  exportSchema = false
)
abstract class ModelDatabase : RoomDatabase() {
  internal abstract fun modelDao(): ModelDao
}

@Module
@InstallIn(SingletonComponent::class)
private object ModelDatabaseModel {
  @Provides
  @Singleton
  fun providesDatabase(@ApplicationContext context: Context): ModelDatabase =
    Room.databaseBuilder(context, ModelDatabase::class.java, "model-db")
      .addCallback(
        object : RoomDatabase.Callback() {
          override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            SeedDatabaseWorker.execute(context)
          }
        }
      ).build()

  @Provides
  fun providesModelDao(modelDb: ModelDatabase) = modelDb.modelDao()
}
