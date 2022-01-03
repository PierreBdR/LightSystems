package net.barbierdereuille.lightsystem.data

import android.util.Log
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlin.math.min
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.single
import net.barbierdereuille.lightsystem.LightSystemsTag

@Dao
interface ModelDao {
  @Insert
  suspend fun add(model: ModelDescription): Long

  @Update
  suspend fun update(model: ModelDescription)

  @Delete
  suspend fun delete(model: ModelDescription)

  @Query("DELETE FROM models WHERE id = :modelId")
  suspend fun deleteModel(modelId: Long)

  @Query("DELETE FROM models")
  suspend fun deleteAll()

}
