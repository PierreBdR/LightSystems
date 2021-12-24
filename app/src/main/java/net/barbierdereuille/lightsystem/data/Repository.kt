package net.barbierdereuille.lightsystem.data

import android.util.Log
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import net.barbierdereuille.lightsystem.LightSystemsTag

class Repository @Inject constructor(
  private val modelDao: ModelDao
) {
  suspend fun clearAll() {
    modelDao.deleteAll()
  }

  suspend fun addModel(model: Model): Model {
    return modelDao.addModel(model)
  }

  suspend fun updateModel(model: Model) {
    modelDao.updateModel(model)
  }

  suspend fun deleteModel(modelId: Long) {
    modelDao.deleteModel(modelId)
  }

  fun allModels(): Flow<List<Model>> =
    modelDao.allModels().map { it.toModels() }

  fun resolveModel(modelId: Long): Flow<Model?> {
    return modelDao.resolveModel(modelId).combine(modelDao.resolvedModelRules(modelId)) { model, rules ->
      model?.toModel()?.copy(rules = rules.sortedBy { it.order }.map { it.toRule() })
    }
  }

}

private fun List<ModelDescription>.toModels(): List<Model> = map { it.toModel() }
