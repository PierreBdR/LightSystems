package net.barbierdereuille.lightsystem.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "models")
data class ModelDescription(
  @PrimaryKey(autoGenerate = true) val id: Long,
  val name: String? = null,
  val content: String? = null
) {
  constructor(model: Model) : this(
    id = model.id,
    name = model.name,
    content = "", //model.toProto(),
  )
}

