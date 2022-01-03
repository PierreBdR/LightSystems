package net.barbierdereuille.lightsystem

import com.google.common.truth.extensions.proto.ProtoTruth.assertThat
import net.barbierdereuille.lightsystem.data.Apply
import net.barbierdereuille.lightsystem.data.Assignment
import net.barbierdereuille.lightsystem.data.BoundSymbol
import net.barbierdereuille.lightsystem.data.MatchingSymbol
import net.barbierdereuille.lightsystem.data.Model
import net.barbierdereuille.lightsystem.data.ResolvedSymbols
import net.barbierdereuille.lightsystem.data.Rule
import net.barbierdereuille.lightsystem.data.Symbol
import net.barbierdereuille.lightsystem.data.Value
import net.barbierdereuille.lightsystem.data.Variable
import net.barbierdereuille.lightsystem.data.toProto
import net.barbierdereuille.lightsystem.functions.Add
import net.barbierdereuille.lightsystem.functions.GreaterOrEqual
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
import org.junit.Test

class ProtoTest {

  @Test
  fun toProto() {
    val a = Symbol(0, "A", 1)
    val b = Symbol(1, "B", 0)
    val c = Symbol(2, "C", 2)
    val x = Variable("x", 0)
    val y = Variable("y", 1)
    val z = Variable("z", 2)
    val t = Variable("t", 3)
    val u = Variable("u", 4)
    val sample = Model(
      name = "model",
      start = listOf(ResolvedSymbols(a, 1.0)),
      symbols = listOf(a, b, c),
      decompositions = listOf(),
      rules = listOf(
        Rule(
          variables = listOf("x", "y", "z", "t", "u"),
          lhs = listOf(MatchingSymbol(a, x), MatchingSymbol(c, listOf(y, z))),
          leftContext = listOf(MatchingSymbol(b)),
          rightContext = listOf(MatchingSymbol(a, x)),
          rhs = listOf(
            BoundSymbol(a, t),
            BoundSymbol(b),
            BoundSymbol(c, listOf(Value(2.5), u))
          ),
          condition = Apply(GreaterOrEqual, listOf(x, Value(0.0))),
          block = listOf(
            Assignment(t, Apply(Add, listOf(y, z))),
            Assignment(u, Apply(Add, listOf(x, Value(1.0)))),
          )
        )
      ),
      id = 1L,
    )

    assertThat(sample.toProto()).isEqualTo(
      model {
        name = "model"
        start += listOf(
          resolvedSymbol {
            symbolId = 0
            parameters += 1.0
          }
        )
        symbols += listOf(
          symbol {
            name = "A"
            arity = 1
            id = 0
          },
          symbol {
            name = "B"
            arity = 0
            id = 1
          },
          symbol {
            name = "C"
            arity = 2
            id = 2
          },
        )
        rules += listOf(
          rule {
            variables += listOf("x", "y", "z", "t", "u")
            lhs += listOf(
              matchingSymbol {
                symbolId = 0
                variables += listOf(x.index)
              },
              matchingSymbol {
                symbolId = 2
                variables += listOf(y.index, z.index)
              }
            )
            leftContext += listOf(
              matchingSymbol { symbolId = b.id }
            )
            rightContext += listOf(
              matchingSymbol {
                symbolId = a.id
                variables += listOf(x.index)
              }
            )
            condition = expression {
              apply = applyExpression {
                function = Function.GREATER_OR_EQUAL
                parameters += listOf(
                  expression { variable = x.index },
                  expression { value = 0.0 },
                )
              }
            }
            block += listOf(
              assignment {
                variable = t.index
                value = expression {
                  apply = applyExpression {
                    function = Function.ADD
                    parameters += listOf(
                      expression { variable = y.index },
                      expression { variable = z.index },
                    )
                  }
                }
              },
              assignment {
                variable = u.index
                value = expression {
                  apply = applyExpression {
                    function = Function.ADD
                    parameters += listOf(
                      expression { variable = x.index },
                      expression { value = 1.0 },
                    )
                  }
                }
              }
            )
            rhs += listOf(
              boundSymbol {
                symbolId = a.id
                parameters += listOf(
                  boundParameter { variable = t.index }
                )
              },
              boundSymbol {
                symbolId = b.id
              },
              boundSymbol {
                symbolId = c.id
                parameters += listOf(
                  boundParameter { value = 2.5 },
                  boundParameter { variable = u.index }
                )
              }
            )
          }
        )
      }
    )
  }
}