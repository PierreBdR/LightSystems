package net.barbierdereuille.lightsystem.data

import kotlin.math.exp
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
typealias BoundSymbolProto = net.barbierdereuille.lightsystem.proto.BoundSymbol
typealias AssignmentProto = net.barbierdereuille.lightsystem.proto.Assignment

fun Model.toProto(): ModelProto {
  val data = this
  return model {
    name = data.name
    symbols += data.symbols.map { it.toProto() }
    start += data.start.map { it.toProto() }
    rules += data.rules.map { it.toProto() }
  }
}

fun Symbol.toProto(): SymbolProto {
  val data = this
  return symbol {
    id = data.id
    arity = data.arity
    name = data.name
  }
}

fun ResolvedSymbols.toProto(): ResolvedSymbolProto {
  val data = this
  return resolvedSymbol {
    symbolId = data.symbol.id
    parameters += data.values
  }
}

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

fun Assignment.toProto(): AssignmentProto {
  val data = this
  return assignment {
    variable = data.variable.index
    value = data.value.toProto()
  }
}

fun BoundSymbol.toProto(): BoundSymbolProto {
  val data = this
  return boundSymbol {
    symbolId = data.symbol.id
    parameters += data.boundTo.map { it.toBoundParameter() }
  }
}

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

fun MatchingSymbol.toProto(): MatchingSymbolProto {
  val data = this
  return matchingSymbol {
    symbolId = data.symbol.id
    variables += data.variables.map { it.index }
  }
}

fun Expression.toProto(): ExpressionProto {
  return expression {
    when (this@toProto) {
      is Variable -> variable = index
      is Value -> value = this@toProto.value
      is Func -> function = toFuncProto()
      is Apply -> apply = toApplyProto()
      is Placeholder -> isPlaceholder = true
    }
  }
}

fun Func.toFuncProto(): Function =
  when (this) {
    is Equal -> Function.EQUAL
    is Different -> Function.DIFFERENT
    is GreaterThan -> Function.GREATER_THAN
    is LessThan -> Function.LESS_THAN
    is GreaterOrEqual -> Function.GREATER_OR_EQUAL
    is LessOrEqual -> Function.LESS_OR_EQUAL
    is And -> Function.AND
    is Or -> Function.OR
    is Not -> Function.NOT
    is Add -> Function.ADD
    is Subtract -> Function.SUBTRACT
    is Multiply -> Function.MULTIPLY
    is Divide -> Function.DIVIDE
    is Power -> Function.POWER
    is Round -> Function.ROUND
    is Sin -> Function.SIN
    is Cos -> Function.COS
    is Tan -> Function.TAN
    is Exp -> Function.EXP
    is Ln -> Function.LN
    else -> error("Unknown function: $this")
  }

fun Apply.toApplyProto(): ApplyExpression {
  val data = this
  return applyExpression {
    function = data.function.toFuncProto()
    parameters += data.parameters.map { it.toProto() }
  }
}
