package net.barbierdereuille.lightsystem.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ModelDao {
  @Insert
  fun insertAll(vararg models: ModelDescription)

  @Insert
  fun add(model: ModelDescription): Long

  @Insert
  fun addAll(rule: List<RuleDefinition>): Array<Long>

  @Update
  fun update(model: ModelDescription)

  @Update
  fun update(rule: RuleDefinition)

  @Delete
  fun delete(model: ModelDescription)

  @Delete
  fun delete(rule: RuleDefinition)

  @Query("DELETE FROM models")
  fun deleteAllModels()

  @Query("DELETE FROM rules")
  fun deleteAllRules()

//  @Query("SELECT * from models WHERE id = :modelId ")
//  fun findModel(modelId: Long): Map<ModelDescription, List<RuleDefinition>>

  @Query("SELECT * from models")
  fun allModels(): Flow<List<ModelDescription>>
}
