package net.barbierdereuille.lightsystem.data

data class Rule(
  val lhs: String,
  val rhs: String,
  val id: Long = NO_ID,
) {
  companion object {
    const val NO_ID: Long = 0
  }
}

fun rules(vararg ruleList: Pair<String, String>) =
  ruleList.map { Rule(it.first, it.second) }
