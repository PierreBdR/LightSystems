package net.barbierdereuille.lightsystem.data

import androidx.annotation.IntRange

data class Model(
  val name: String,
  val start: List<ResolvedSymbols>,
  val symbols: List<Symbol>,
  val decompositions: List<DecompositionRule>,
  val rules: List<Rule>,
  val id: Long = NO_ID,
) {
  companion object {
    const val NO_ID: Long = 0
  }
}

data class Symbol(
  val id: Int,
  val name: String,
  @IntRange(from = 0) val arity: Int,
)

data class ResolvedSymbols(
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

data class DecompositionRule(
  val symbol: Symbol,
)

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
