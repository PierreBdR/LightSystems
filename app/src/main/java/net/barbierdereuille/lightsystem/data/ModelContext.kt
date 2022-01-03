package net.barbierdereuille.lightsystem.data

class ModelContext(
  val binding: Array<Double> = arrayOf(),
) {
  fun resolveVariable(variable: Variable): Double = binding[variable.index]
}
