package net.barbierdereuille.lightsystem.functions

import kotlin.math.pow
import net.barbierdereuille.lightsystem.data.Expression
import net.barbierdereuille.lightsystem.data.Operator

abstract class UnaryOperator(
  val name: String,
  override val precedence: Int,
) : Operator {
  override val arity: Int = 1

  override fun show(parameters: List<Expression>): String =
    "$name${showChild(precedence, parameters[0])}"

  override fun apply(parameters: List<Double>): Double =
    apply(parameters[0])

  abstract fun apply(parameter: Double): Double
}

abstract class BinaryOperator(
  val name: String,
  override val precedence: Int,
) : Operator {
  override val arity: Int = 2

  override fun show(parameters: List<Expression>): String =
    "${showChild(precedence, parameters[0])} $name ${showChild(precedence, parameters[1])}"

  override fun apply(parameters: List<Double>): Double =
    apply(parameters[0], parameters[1])

  abstract fun apply(v1: Double, v2: Double): Double
}

/** Binary operator grouped left first, so (a + b + c) = (a + b) + c. */
abstract class LeftBinaryOperator(
  name: String,
  precedence: Int,
) : BinaryOperator(name, precedence) {
  override fun show(parameters: List<Expression>): String =
    "${showChild(precedence, parameters[0])} $name ${showChild(precedence + 1, parameters[1])}"
}

/** Binary operator grouped right first, so (a + b + c) = a + (b + c). */
abstract class RightBinaryOperator(
  name: String,
  precedence: Int,
) : BinaryOperator(name, precedence) {
  override fun show(parameters: List<Expression>): String =
    "${showChild(precedence + 1, parameters[0])} $name ${showChild(precedence, parameters[1])}"
}

object Equal : BinaryOperator("==", precedence = 0) {
  override fun apply(v1: Double, v2: Double): Double = if (v1 == v2) 1.0 else 0.0
}

object Different : BinaryOperator("!=", precedence = 0) {
  override fun apply(v1: Double, v2: Double): Double = if (v1 == v2) 0.0 else 1.0
}

object GreaterThan : BinaryOperator(">", precedence = 0) {
  override fun apply(v1: Double, v2: Double): Double = if (v1 > v2) 1.0 else 0.0
}

object LessThan : BinaryOperator("<", precedence = 0) {
  override fun apply(v1: Double, v2: Double): Double = if (v1 < v2) 1.0 else 0.0
}

object GreaterOrEqual : BinaryOperator(">=", precedence = 0) {
  override fun apply(v1: Double, v2: Double): Double = if (v1 >= v2) 1.0 else 0.0
}

object LessOrEqual : BinaryOperator("<=", precedence = 0) {
  override fun apply(v1: Double, v2: Double): Double = if (v1 <= v2) 1.0 else 0.0
}

object And : BinaryOperator("&&", precedence = 1) {
  override fun apply(v1: Double, v2: Double): Double = if (v1 * v2 == 0.0) 0.0 else 1.0
}

object Or : BinaryOperator("||", precedence = 2) {
  override fun apply(v1: Double, v2: Double): Double = if (v1 != 0.0 || v2 != 0.0) 1.0 else 0.0
}

object Add : BinaryOperator("+", precedence = 3) {
  override fun apply(v1: Double, v2: Double): Double = v1 + v2
}

object Subtract : LeftBinaryOperator("-", precedence = 3) {
  override fun apply(v1: Double, v2: Double): Double = v1 - v2
}

object Multiply : BinaryOperator("*", precedence = 4) {
  override fun apply(v1: Double, v2: Double): Double = v1 * v2
}

object Divide : LeftBinaryOperator("/", precedence = 4) {
  override fun apply(v1: Double, v2: Double): Double = v1 / v2
}

object Not : UnaryOperator("!", precedence = 5) {
  override fun apply(parameter: Double): Double = if (parameter == 0.0) 1.0 else 0.0
}

object Power : RightBinaryOperator("^", precedence = 6) {
  override fun apply(v1: Double, v2: Double): Double = v1.pow(v2)
}

