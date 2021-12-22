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
  tableName = "model_rules",
  primaryKeys = ["model_id", "order"],
  foreignKeys = [
    ForeignKey(
      entity = ModelDescription::class,
      parentColumns = ["id"],
      childColumns = ["model_id"],
    ),
    ForeignKey(
      entity = RuleDefinition::class,
      parentColumns = ["id"],
      childColumns = ["rule_id"],
    ),
  ]
)
data class ModelRules(
  @ColumnInfo(name = "model_id") val modelId: Long,
  @ColumnInfo(name = "rule_id") val ruleId: Long,
  val order: Int,
)

@Entity(tableName = "rules")
data class RuleDefinition(
  @PrimaryKey(autoGenerate = true) val id: Long,
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

fun Rule.toDb() = RuleDefinition(id = id, lhs = lhs, rhs = rhs)