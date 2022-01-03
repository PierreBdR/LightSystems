package net.barbierdereuille.lightsystem.data

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class Repository @Inject constructor(
  private val modelDao: ModelDao
) {
  fun allModels(): Flow<List<Model>> {
    return flow { emptyList<Model>() }
  }

  suspend fun clearAll() {
    modelDao.deleteAll()
  }

  suspend fun addModel(model: Model): Model {
    val id = modelDao.add(
      ModelDescription(
        id = 0L,
        name = model.name,
        //content = model.toProto(),
      )
    )
    return model.copy(id = id)
  }

  suspend fun updateModel(model: Model) {
    modelDao.update(
      ModelDescription(
        id = 0L,
        name = model.name,
      )
    )
  }

  suspend fun deleteModel(modelId: Long) {
    modelDao.deleteModel(modelId)
  }

}
