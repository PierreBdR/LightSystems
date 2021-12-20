package net.barbierdereuille.lightsystem.data

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class Repository @Inject constructor(
  private val modelDao: ModelDao
) {
  fun clearAll() {
    modelDao.deleteAllModels()
    modelDao.deleteAllRules()
  }

  fun addModel(model: Model): Model {
    requireNotNull(model.rules) { "To add a model, the rules need to be specified, even if empty." }
    val modelId = modelDao.add(model.toDb())
    val ruleIds = modelDao.addAll(model.rules.map { it.toDb(modelId) })
    return model.copy(
      id = modelId,
      rules = model.rules.mapIndexed { index, rule -> rule.copy(id = ruleIds[index]) })
  }

  fun allModels(): Flow<List<Model>> =
    modelDao.allModels().map { it.toModels() }

}

private fun List<ModelDescription>.toModels(): List<Model> = map { it.toModel() }
