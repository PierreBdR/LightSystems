package net.barbierdereuille.lightsystem.data

import java.text.DecimalFormat
import kotlin.math.absoluteValue
import net.barbierdereuille.lightsystem.functions.Exp

// Expression that resolves into a floating point value
sealed interface Expression {
  fun apply(context: ModelContext): Double
  fun show(): String
}

sealed interface SimpleExpression : Expression

data class Variable(
  val name: String,
  val index: Int,
) : SimpleExpression {
  override fun apply(context: ModelContext): Double =
    context.resolveVariable(this)

  override fun show(): String = name
}

data class Value(
  val value: Double
) : SimpleExpression {
  override fun apply(context: ModelContext): Double = value
  override fun show(): String {
    if (value == 0.0) return "0"
    val size = value.absoluteValue
    return if(size >= 1e6 || size <= 1e-6) {
      scientificFormat.format(value)
    } else {
      floatFormat.format(value)
    }
  }

  companion object {
    val scientificFormat = DecimalFormat("0.#########E0")
    val floatFormat = DecimalFormat("0.############")
  }
}

data class Apply(
  val function: Func,
  val parameters: List<Expression>,
) : Expression {
  init {
    require(function.arity == parameters.size) {
      "Apply invalid: expected ${function.arity} parameters, got ${parameters.size}."
    }
  }

  override fun apply(context: ModelContext): Double =
    function.apply(parameters.map { it.apply(context) })

  override fun show(): String = function.show(parameters)
}

class PlaceholderApplyException(cause: Throwable? = null) :
  Exception("Trying to evaluate an expression with a placeholder", cause)

object Placeholder : SimpleExpression {
  override fun apply(context: ModelContext): Double {
    throw PlaceholderApplyException()
  }

  override fun show(): String = "\u30ed"
}

interface Func {
  val arity: Int
  fun show(parameters: List<Expression>): String
  fun apply(parameters: List<Double>): Double
}

interface Operator : Func {
  val precedence: Int

  fun showChild(precedence: Int, expression: Expression): String {
    if (expression is Apply && expression.function is Operator) {
      if (expression.function.precedence < precedence) {
        return "(${expression.show()})"
      }
    }
    return expression.show()
  }
}
