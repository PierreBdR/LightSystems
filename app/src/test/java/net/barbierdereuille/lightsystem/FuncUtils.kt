package net.barbierdereuille.lightsystem

import net.barbierdereuille.lightsystem.data.Apply
import net.barbierdereuille.lightsystem.data.Expression
import net.barbierdereuille.lightsystem.functions.Add
import net.barbierdereuille.lightsystem.functions.BinaryOperator
import net.barbierdereuille.lightsystem.functions.Exp
import net.barbierdereuille.lightsystem.functions.Multiply
import net.barbierdereuille.lightsystem.functions.Power
import net.barbierdereuille.lightsystem.functions.Subtract
import net.barbierdereuille.lightsystem.functions.UnaryOperator

fun op(operator: BinaryOperator, v1: Expression, v2: Expression) =
  Apply(operator, listOf(v1, v2))

fun op(operator: UnaryOperator, v: Expression) = Apply(operator, listOf(v))
