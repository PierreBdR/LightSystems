package net.barbierdereuille.lightsystem.data

data class Model(
  val name: String,
  val axiom: String,
  val rules: List<Rule>? = null,
  val id: Long = NO_ID,
) {
  companion object {
    const val NO_ID: Long = 0
  }
}