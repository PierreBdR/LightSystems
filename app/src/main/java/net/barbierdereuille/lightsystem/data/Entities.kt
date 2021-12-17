package net.barbierdereuille.lightsystem.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "models")
data class ModelDescription(
  @PrimaryKey(autoGenerate = true) val id: Long,
  val name: String?,
  val axiom: String?,
) {
  fun toModel() = Model(
    id = id,
    name = checkNotNull(name),
    axiom = checkNotNull(axiom),
  )
}

fun Model.toDb() = ModelDescription(id = id, name = name, axiom = axiom)

@Entity(
  tableName = "rules",
//  foreignKeys = [ForeignKey(
//    entity = ModelDescription::class,
//    parentColumns = ["id"],
//    childColumns = ["model_id"]
//  )]
)
data class RuleDefinition(
  @PrimaryKey(autoGenerate = true) val id: Long,
  @ColumnInfo(name = "model_id") val modelId: Long,
  val lhs: String?,
  val rhs: String?,
) {
  fun toRule() =
    Rule(
      id = id,
      lhs = checkNotNull(lhs),
      rhs = checkNotNull(rhs),
    )
}

fun Rule.toDb(modelId: Long) = RuleDefinition(id = id, modelId = modelId, lhs = lhs, rhs = rhs)