package net.barbierdereuille.lightsystem.data

import androidx.annotation.IntRange

data class Model(
  val name: String,
  val start: List<ResolvedSymbol>,
  val symbols: List<Symbol>,
  val decompositions: List<Rule>,
  val rules: List<Rule>,
  val id: Long = NO_ID,
) {
  init {
    decompositions.forEach {
      require(
        it.leftContext.isEmpty() && it.rightContext.isEmpty() && it.lhs.size == 1
      ) {
        "A decomposition rule cannot have a context and must only decompose a single variable."
      }
    }
  }
  companion object {
    const val NO_ID: Long = 0
  }
}

data class Symbol(
  val id: Int,
  val name: String,
  @IntRange(from = 0) val arity: Int,
)

data class ResolvedSymbol(
  val symbol: Symbol,
  val values: List<Double> = emptyList()
) {
  constructor(symbol: Symbol, value: Double) : this(symbol, listOf(value))
}

data class BoundSymbol(
  val symbol: Symbol,
  val boundTo: List<SimpleExpression> = emptyList(),
) {
  constructor(symbol: Symbol, boundTo: SimpleExpression): this(symbol, listOf(boundTo))
}

data class Rule(
  val lhs: List<MatchingSymbol>,
  val rhs: List<BoundSymbol>,
  val variables: List<String> = emptyList(),
  val leftContext: List<MatchingSymbol> = emptyList(),
  val rightContext: List<MatchingSymbol> = emptyList(),
  val condition: Expression = Value(1.0),
  val block: List<Assignment> = emptyList(),
)

data class MatchingSymbol(
  val symbol: Symbol,
  val variables: List<Variable> = emptyList(),
) {
  constructor(symbol: Symbol, variable: Variable) : this(symbol, listOf(variable))
}

data class Assignment(
  val variable: Variable,
  val value: Expression,
)
