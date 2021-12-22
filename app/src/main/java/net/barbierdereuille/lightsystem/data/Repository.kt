package net.barbierdereuille.lightsystem.data

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class Repository @Inject constructor(
  private val modelDao: ModelDao
) {
  fun clearAll() {
    modelDao.deleteAllModels()
    modelDao.deleteAllRules()
    modelDao.deleteAllModelRules()
  }

  fun addModel(model: Model): Model {
    requireNotNull(model.rules) { "To add a model, the rules need to be specified, even if empty." }
    val modelId = modelDao.add(model.toDb())
    val ruleIds = modelDao.addAllRules(model.rules.map { it.toDb() })
    modelDao.addAllModelRules(model.rules.mapIndexed { index, _ ->
      ModelRules(modelId = modelId, ruleId = ruleIds[index], order = index)
    })
    return model.copy(
      id = modelId,
      rules = model.rules.mapIndexed { index, rule -> rule.copy(id = ruleIds[index]) })
  }

  fun allModels(): Flow<List<Model>> =
    modelDao.allModels().map { it.toModels() }

  fun resolveModel(modelId: Long): Flow<Model> {
    return modelDao.resolveModel(modelId).combine(modelDao.modelRules(modelId)) { model, rules ->
      model.toModel().copy(rules = rules.sortedBy { it.order }.map { it.toRule() })
    }
  }

}

private fun List<ModelDescription>.toModels(): List<Model> = map { it.toModel() }
