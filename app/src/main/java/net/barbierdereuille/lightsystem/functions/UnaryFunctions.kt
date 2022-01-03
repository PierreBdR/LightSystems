package net.barbierdereuille.lightsystem.functions

import kotlin.math.round
import kotlin.math.sin
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.tan
import net.barbierdereuille.lightsystem.data.Expression
import net.barbierdereuille.lightsystem.data.Func

abstract class UnaryFunction(val name: String) : Func {
  override val arity: Int = 1

  override fun show(parameters: List<Expression>): String =
    "$name(${parameters[0].show()}"

  override fun apply(parameters: List<Double>): Double = apply(parameters[1])

  abstract fun apply(value: Double): Double
}

object Round : UnaryFunction("round") {
  override fun apply(value: Double): Double = round(value)
}

object Sin : UnaryFunction("sin") {
  override fun apply(value: Double): Double = sin(value)
}

object Cos : UnaryFunction("cos") {
  override fun apply(value: Double): Double = cos(value)
}

object Tan : UnaryFunction("tan") {
  override fun apply(value: Double): Double = tan(value)
}

object Exp : UnaryFunction("exp") {
  override fun apply(value: Double): Double = exp(value)
}

object Ln : UnaryFunction("ln") {
  override fun apply(value: Double): Double = ln(value)
}
