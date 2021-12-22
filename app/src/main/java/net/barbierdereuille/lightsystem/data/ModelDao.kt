package net.barbierdereuille.lightsystem.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.PrimaryKey
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
  fun addAllRules(rule: List<RuleDefinition>): Array<Long>

  @Insert
  fun addAllModelRules(modelRule: List<ModelRules>)

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

  @Query("DELETE FROM model_rules")
  fun deleteAllModelRules()

  @Query(
    "SELECT `order`, rule_id as ruleId, rules.lhs, rules.rhs FROM model_rules " +
      "INNER JOIN rules ON rules.id = model_rules.rule_id " +
      "WHERE model_id = :modelId "
  )
  fun modelRules(modelId: Long): Flow<List<ResolvedRules>>

  @Query("SELECT * from models WHERE id = :modelId ")
  fun resolveModel(modelId: Long): Flow<ModelDescription>

  @Query("SELECT * from models")
  fun allModels(): Flow<List<ModelDescription>>
}

data class ResolvedRules(
  val order: Int,
  val ruleId: Long,
  val lhs: String,
  val rhs: String,
) {
  fun toRule() = Rule(
    id = ruleId,
    lhs = lhs,
    rhs = rhs,
  )
}
