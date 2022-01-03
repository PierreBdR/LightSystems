package net.barbierdereuille.lightsystem

import com.google.common.truth.Truth.assertThat
import kotlin.test.assertFailsWith
import net.barbierdereuille.lightsystem.data.ModelContext
import net.barbierdereuille.lightsystem.data.Placeholder
import net.barbierdereuille.lightsystem.data.PlaceholderApplyException
import net.barbierdereuille.lightsystem.data.Value
import net.barbierdereuille.lightsystem.data.Variable
import net.barbierdereuille.lightsystem.functions.Add
import net.barbierdereuille.lightsystem.functions.Multiply
import net.barbierdereuille.lightsystem.functions.Subtract
import org.junit.Test

class ExpressionTest {
  @Test
  fun testValue() {
    val v1 = Value(1.0)
    val v2 = Value(1.2)
    val v3 = Value(1.2e-3)
    val v4 = Value(1.2e-9)
    val v5 = Value(1.2e3)
    val v6 = Value(1.2e6)
    val context = ModelContext()

    assertThat(v1.show()).isEqualTo("1")
    assertThat(v2.show()).isEqualTo("1.2")
    assertThat(v3.show()).isEqualTo("0.0012")
    assertThat(v4.show()).isEqualTo("1.2E-9")
    assertThat(v5.show()).isEqualTo("1200")
    assertThat(v6.show()).isEqualTo("1.2E6")
    assertThat(v1.apply(context)).isEqualTo(1.0)
  }

  @Test
  fun testVariable() {
    val v1 = Variable("a", 0)
    val v2 = Variable("b", 1)
    val context = ModelContext(binding = arrayOf(0.5))

    assertThat(v1.show()).isEqualTo("a")
    assertThat(v2.show()).isEqualTo("b")

    assertThat(v1.apply(context)).isEqualTo(0.5)
    assertFailsWith<IndexOutOfBoundsException> {
      assertThat(v2.apply(context)).isEqualTo(1.2)
    }
  }

  @Test
  fun testPlaceholder() {
    val ph = Placeholder
    val context = ModelContext()

    assertThat(ph.show()).isEqualTo("\u30ed")
    assertFailsWith<PlaceholderApplyException> {
      ph.apply(context)
    }
  }

  @Test
  fun testApply() {
    val expression = op(Add, Value(1.2), Variable("a", 0))
    val context = ModelContext(binding = arrayOf(0.5))

    assertThat(expression.apply(context)).isEqualTo(1.7)
    assertThat(expression.show()).isEqualTo("1.2 + a")
  }

  @Test
  fun testComplex() {
    val v1 = Variable("a", 0)
    val expression = op(Multiply, op(Subtract, op(Add, Value(1.2), v1), v1), Value(2.0))
    val context = ModelContext(binding = arrayOf(0.5))

    assertThat(expression.apply(context)).isEqualTo(2.4)
    assertThat(expression.show()).isEqualTo("(1.2 + a - a) * 2")
  }
}
