package net.barbierdereuille.lightsystem.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Rule(
  val lhs: String,
  val rhs: String,
  val id: Long = NO_ID,
) : Parcelable {
  enum class Part {
    LHS,
    RHS,
  }

  fun update(part: Part, value: String) =
    when (part) {
      Part.LHS -> copy(lhs = value)
      Part.RHS -> copy(rhs = value)
    }

  companion object {
    const val NO_ID: Long = 0
  }
}

fun rules(vararg ruleList: Pair<String, String>) =
  ruleList.map { Rule(it.first, it.second) }
