package net.barbierdereuille.lightsystem.data

import net.barbierdereuille.lightsystem.functions.Add
import net.barbierdereuille.lightsystem.functions.And
import net.barbierdereuille.lightsystem.functions.Cos
import net.barbierdereuille.lightsystem.functions.Different
import net.barbierdereuille.lightsystem.functions.Divide
import net.barbierdereuille.lightsystem.functions.Equal
import net.barbierdereuille.lightsystem.functions.Exp
import net.barbierdereuille.lightsystem.functions.GreaterOrEqual
import net.barbierdereuille.lightsystem.functions.GreaterThan
import net.barbierdereuille.lightsystem.functions.LessOrEqual
import net.barbierdereuille.lightsystem.functions.LessThan
import net.barbierdereuille.lightsystem.functions.Ln
import net.barbierdereuille.lightsystem.functions.Multiply
import net.barbierdereuille.lightsystem.functions.Not
import net.barbierdereuille.lightsystem.functions.Or
import net.barbierdereuille.lightsystem.functions.Power
import net.barbierdereuille.lightsystem.functions.Round
import net.barbierdereuille.lightsystem.functions.Sin
import net.barbierdereuille.lightsystem.functions.Subtract
import net.barbierdereuille.lightsystem.functions.Tan
import net.barbierdereuille.lightsystem.proto.ApplyExpression
import net.barbierdereuille.lightsystem.proto.BoundParameter
import net.barbierdereuille.lightsystem.proto.Function
import net.barbierdereuille.lightsystem.proto.applyExpression
import net.barbierdereuille.lightsystem.proto.assignment
import net.barbierdereuille.lightsystem.proto.boundParameter
import net.barbierdereuille.lightsystem.proto.boundSymbol
import net.barbierdereuille.lightsystem.proto.expression
import net.barbierdereuille.lightsystem.proto.matchingSymbol
import net.barbierdereuille.lightsystem.proto.model
import net.barbierdereuille.lightsystem.proto.resolvedSymbol
import net.barbierdereuille.lightsystem.proto.rule
import net.barbierdereuille.lightsystem.proto.symbol

typealias ModelProto = net.barbierdereuille.lightsystem.proto.Model
typealias ResolvedSymbolProto = net.barbierdereuille.lightsystem.proto.ResolvedSymbol
typealias SymbolProto = net.barbierdereuille.lightsystem.proto.Symbol
typealias RuleProto = net.barbierdereuille.lightsystem.proto.Rule
typealias MatchingSymbolProto = net.barbierdereuille.lightsystem.proto.MatchingSymbol
typealias ExpressionProto = net.barbierdereuille.lightsystem.proto.Expression
typealias CoreCase = net.barbierdereuille.lightsystem.proto.Expression.CoreCase
typealias BoundSymbolProto = net.barbierdereuille.lightsystem.proto.BoundSymbol
typealias AssignmentProto = net.barbierdereuille.lightsystem.proto.Assignment

fun Model.toProto(): ModelProto {
  val data = this
  return model {
    name = data.name
    symbols += data.symbols.map { it.toProto() }
    start += data.start.map { it.toProto() }
    rules += data.rules.map { it.toProto() }
    decompositions += data.decompositions.map { it.toProto() }
  }
}

fun ModelProto.toModel(id: Long = Model.NO_ID): Model {
  val symbols = symbolsList.map { it.toModel() }
  return Model(
    name = name,
    symbols = symbols,
    start = startList.map { it.toModel(symbols) },
    rules = rulesList.map { it.toModel(symbols) },
    decompositions = decompositionsList.map { it.toModel(symbols) },
    id = id,
  )
}

fun Symbol.toProto(): SymbolProto {
  val data = this
  return symbol {
    id = data.id
    arity = data.arity
    name = data.name
  }
}

fun SymbolProto.toModel(): Symbol =
  Symbol(
    id = id,
    name = name,
    arity = arity,
  )

fun ResolvedSymbol.toProto(): ResolvedSymbolProto {
  val data = this
  return resolvedSymbol {
    symbolId = data.symbol.id
    parameters += data.values
  }
}

fun ResolvedSymbolProto.toModel(symbols: List<Symbol>): ResolvedSymbol =
  ResolvedSymbol(
    symbol = symbols[symbolId],
    values = parametersList,
  )

fun Rule.toProto(): RuleProto {
  val data = this
  return rule {
    lhs += data.lhs.map { it.toProto() }
    leftContext += data.leftContext.map { it.toProto() }
    rightContext += data.rightContext.map { it.toProto() }
    variables += data.variables
    condition = data.condition.toProto()
    rhs += data.rhs.map { it.toProto() }
    block += data.block.map { it.toProto() }
  }
}

fun RuleProto.toModel(symbols: List<Symbol>): Rule {
  val variables = variablesList.mapIndexed { index, name -> Variable(name = name, index = index) }
  return Rule(
    variables = variablesList,
    lhs = lhsList.map { it.toModel(symbols, variables) },
    rhs = rhsList.map { it.toModel(symbols, variables) },
    leftContext = leftContextList.map { it.toModel(symbols, variables) },
    rightContext = rightContextList.map { it.toModel(symbols, variables) },
    condition = condition.toModel(variables),
    block = blockList.map { it.toModel(variables) }
  )
}

fun Assignment.toProto(): AssignmentProto {
  val data = this
  return assignment {
    variable = data.variable.index
    value = data.value.toProto()
  }
}

fun AssignmentProto.toModel(variables: List<Variable>): Assignment =
  Assignment(
    variable = variables[variable],
    value = value.toModel(variables),
  )

fun BoundSymbol.toProto(): BoundSymbolProto {
  val data = this
  return boundSymbol {
    symbolId = data.symbol.id
    parameters += data.boundTo.map { it.toBoundParameter() }
  }
}

fun BoundSymbolProto.toModel(symbols: List<Symbol>, variables: List<Variable>) =
  BoundSymbol(
    symbol = symbols[symbolId],
    boundTo = parametersList.map { it.toModel(variables) }
  )

fun SimpleExpression.toBoundParameter(): BoundParameter {
  val data = this
  return boundParameter {
    when (data) {
      is Value -> value = data.value
      is Variable -> variable = data.index
      is Placeholder -> isPlaceholder = true
    }
  }
}

fun BoundParameter.toModel(variables: List<Variable>) =
  when (bindingCase) {
    BoundParameter.BindingCase.VALUE -> Value(value)
    BoundParameter.BindingCase.VARIABLE -> variables[variable]
    BoundParameter.BindingCase.IS_PLACEHOLDER -> Placeholder
    else -> error("Unknown bindingCase for BoundParameter: $bindingCase")
  }

fun MatchingSymbol.toProto(): MatchingSymbolProto {
  val data = this
  return matchingSymbol {
    symbolId = data.symbol.id
    variables += data.variables.map { it.index }
  }
}

fun MatchingSymbolProto.toModel(symbols: List<Symbol>, variables: List<Variable>) =
  MatchingSymbol(
    symbol = symbols[symbolId],
    variables = variablesList.map { variables[it] },
  )

fun Expression.toProto(): ExpressionProto {
  return expression {
    when (this@toProto) {
      is Variable -> variable = index
      is Value -> value = this@toProto.value
      is Apply -> apply = toApplyProto()
      is Placeholder -> isPlaceholder = true
    }
  }
}

fun ExpressionProto.toModel(variables: List<Variable>): Expression =
  when(coreCase) {
    CoreCase.APPLY -> apply.toModel(variables)
    CoreCase.VALUE -> Value(value)
    CoreCase.VARIABLE -> variables[variable]
    CoreCase.IS_PLACEHOLDER -> Placeholder
    else -> error("Unknown Expression core case: $coreCase")
  }

private val FuncToFuncProto = mapOf(
  Equal to Function.EQUAL,
  Different to Function.DIFFERENT,
  GreaterThan to Function.GREATER_THAN,
  LessThan to Function.LESS_THAN,
  GreaterOrEqual to Function.GREATER_OR_EQUAL,
  LessOrEqual to Function.LESS_OR_EQUAL,
  And to Function.AND,
  Or to Function.OR,
  Not to Function.NOT,
  Add to Function.ADD,
  Subtract to Function.SUBTRACT,
  Multiply to Function.MULTIPLY,
  Divide to Function.DIVIDE,
  Power to Function.POWER,
  Round to Function.ROUND,
  Sin to Function.SIN,
  Cos to Function.COS,
  Tan to Function.TAN,
  Exp to Function.EXP,
  Ln to Function.LN,
)

private val FuncProtoToFunc = FuncToFuncProto.entries.associate { it.value to it.key }

fun Func.toFuncProto(): Function = FuncToFuncProto[this] ?: error("Unknown function: $this")

fun Function.toFunc(): Func = FuncProtoToFunc[this] ?: error("Unknown function: $this")

fun Apply.toApplyProto(): ApplyExpression {
  val data = this
  return applyExpression {
    function = data.function.toFuncProto()
    parameters += data.parameters.map { it.toProto() }
  }
}

fun ApplyExpression.toModel(variables: List<Variable>) =
  Apply(
    function = function.toFunc(),
    parameters = parametersList.map { it.toModel(variables) },
  )
