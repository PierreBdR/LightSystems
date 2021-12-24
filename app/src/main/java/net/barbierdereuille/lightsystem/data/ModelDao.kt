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
  suspend fun insertAll(vararg models: ModelDescription)

  @Insert
  suspend fun add(model: ModelDescription): Long

  @Insert
  suspend fun add(model: RuleDefinition): Long

  @Insert
  suspend fun addRules(rule: List<RuleDefinition>): Array<Long>

  @Insert
  suspend fun addModelRules(modelRule: List<ModelRule>)

  @Query("DELETE FROM model_rules WHERE model_id = :modelId")
  suspend fun removeModelRules(modelId: Long)

  @Update
  suspend fun update(model: ModelDescription)

  @Update
  suspend fun update(rule: RuleDefinition)

  @Update
  suspend fun updateRules(rules: List<RuleDefinition>)

  @Update
  suspend fun update(modelRule: ModelRule)

  @Delete
  suspend fun delete(model: ModelDescription)

  @Delete
  suspend fun delete(rule: RuleDefinition)

  @Delete
  suspend fun deleteRules(rules: List<RuleDefinition>)

  @Delete
  suspend fun delete(modelRule: ModelRule)

  @Delete
  suspend fun deleteModelRules(modelRules: List<ModelRule>)

  @Query("DELETE FROM models")
  suspend fun deleteAllModels()

  @Query("DELETE FROM rules")
  suspend fun deleteAllRules()

  @Query("DELETE FROM model_rules")
  suspend fun deleteAllModelRules()

  @Query(
    "SELECT `order`, rule_id as ruleId, rules.lhs, rules.rhs FROM model_rules " +
      "INNER JOIN rules ON rules.id = model_rules.rule_id " +
      "WHERE model_id = :modelId "
  )
  fun resolvedModelRules(modelId: Long): Flow<List<ResolvedRules>>

  @Query("SELECT * FROM model_rules where model_id = :modelId")
  fun modelRules(modelId: Long): Flow<List<ModelRule>>

  @Query("SELECT * from models WHERE id = :modelId ")
  fun resolveModel(modelId: Long): Flow<ModelDescription?>

  @Query("SELECT * from models")
  fun allModels(): Flow<List<ModelDescription>>

  @Transaction
  suspend fun addModel(model: Model): Model {
    requireNotNull(model.rules) { "To add a model, the rules need to be specified, even if empty." }
    Log.i(LightSystemsTag, "Adding model ${model.name}")
    val modelId = add(model.toDb())
    Log.i(LightSystemsTag, "Created modelId ${modelId} for model ${model.name}")
    val ruleIds = addRules(model.rules.map { it.toDb() })
    Log.i(LightSystemsTag, "Created rule ids $ruleIds for rules ${model.rules}")
    addModelRules(model.rules.mapIndexed { index, _ ->
      ModelRule(modelId = modelId, ruleId = ruleIds[index], order = index)
    })
    return model.copy(
      id = modelId,
      rules = model.rules.mapIndexed { index, rule -> rule.copy(id = ruleIds[index]) })
  }

  @Transaction
  suspend fun deleteAll() {
    deleteAllModels()
    deleteAllRules()
    deleteAllModelRules()
  }

  @Transaction
  suspend fun updateModel(model: Model): Model {
    require(model.id != 0L) { "Only a model with an id can be updated" }
    requireNotNull(model.rules) { "To update a model, the rules must be defined" }
    update(model.toDb())
    Log.i(LightSystemsTag , "Update model - 1")
    val currentModelRules = modelRules(model.id).first()
    val newRules = model.rules.map { it.toDb() }
    val commonCount = min(currentModelRules.size, model.rules.size)
    val extraRuleCount = model.rules.size - currentModelRules.size
    Log.i(LightSystemsTag , "Update model - 2")
    if (commonCount > 0) {
      updateRules(newRules.take(commonCount))
    }
    Log.i(LightSystemsTag , "Update model - 3")
    return when {
      extraRuleCount > 0 -> {
        Log.i(LightSystemsTag , "Update model - extra rules")
        val extraIds = addRules(newRules.drop(commonCount))
        addModelRules(extraIds.mapIndexed { index, ruleId ->
          ModelRule(
            modelId = model.id,
            ruleId = ruleId,
            order = index + commonCount
          )
        })
        model.copy(
          rules = model.rules.take(commonCount) + model.rules.drop(commonCount)
            .mapIndexed { index, rule -> rule.copy(id = extraIds[index]) })
      }
      extraRuleCount < 0 -> {
        Log.i(LightSystemsTag , "Update model - missing rules")
        deleteRules(currentModelRules.drop(commonCount).map { RuleDefinition(id = it.ruleId) })
        deleteModelRules(currentModelRules.drop(commonCount))
        model.copy(rules = model.rules.take(commonCount))
      }
      else -> model
    }
  }

  @Transaction
  suspend fun deleteModel(modelId: Long) {
    val rules = modelRules(modelId).first()
    Log.i(LightSystemsTag, "Rules to delete: $rules")
    if (rules.isNotEmpty()) {
      deleteModelRules(rules)
      deleteRules(rules.map { RuleDefinition(id = it.ruleId) })
    }
    delete(ModelDescription(id = modelId))
  }
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
