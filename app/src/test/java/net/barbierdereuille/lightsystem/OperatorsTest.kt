package net.barbierdereuille.lightsystem

import com.google.common.truth.Truth.assertThat
import net.barbierdereuille.lightsystem.data.ModelContext
import net.barbierdereuille.lightsystem.data.Value
import net.barbierdereuille.lightsystem.functions.Add
import net.barbierdereuille.lightsystem.functions.And
import net.barbierdereuille.lightsystem.functions.Divide
import net.barbierdereuille.lightsystem.functions.Multiply
import net.barbierdereuille.lightsystem.functions.Not
import net.barbierdereuille.lightsystem.functions.Or
import net.barbierdereuille.lightsystem.functions.Power
import net.barbierdereuille.lightsystem.functions.Subtract
import org.junit.Test

class OperatorsTest {

  @Test
  fun addTest1() {
    val e1 = op(Add, op(Add, Value(1.0), Value(2.0)), Value(3.0))
    assertThat(e1.apply(ModelContext())).isEqualTo(6.0)
    assertThat(e1.show()).isEqualTo("1 + 2 + 3")
  }

  @Test
  fun addTest2() {
    val e1 = op(Add, Value(1.0), op(Add, Value(2.0), Value(3.0)))
    assertThat(e1.apply(ModelContext())).isEqualTo(6.0)
    assertThat(e1.show()).isEqualTo("1 + 2 + 3")
  }

  @Test
  fun subtractTest1() {
    val e1 = op(Subtract, op(Subtract, Value(1.0), Value(2.0)), Value(3.0))
    assertThat(e1.apply(ModelContext())).isEqualTo(-4.0)
    assertThat(e1.show()).isEqualTo("1 - 2 - 3")
  }

  @Test
  fun subtractTest2() {
    val e1 = op(Subtract, Value(1.0), op(Subtract, Value(2.0), Value(3.0)))
    assertThat(e1.apply(ModelContext())).isEqualTo(2.0)
    assertThat(e1.show()).isEqualTo("1 - (2 - 3)")
  }

  @Test
  fun powerTest1() {
    val e1 = op(Power, op(Power, Value(2.0), Value(3.0)), Value(2.0))
    assertThat(e1.apply(ModelContext())).isEqualTo(64.0)
    assertThat(e1.show()).isEqualTo("(2 ^ 3) ^ 2")
  }

  @Test
  fun powerTest2() {
    val e1 = op(Power, Value(2.0), op(Power, Value(3.0), Value(2.0)))
    assertThat(e1.apply(ModelContext())).isEqualTo(512.0)
    assertThat(e1.show()).isEqualTo("2 ^ 3 ^ 2")
  }

  @Test
  fun mixAddMultiplyAndPower() {
    val e1 = op(Power, op(Multiply, Value(2.0), op(Add, Value(3.0), Value(2.1))), Value(3.0))
    assertThat(e1.apply(ModelContext())).isWithin(1e-6).of(1061.208)
    assertThat(e1.show()).isEqualTo("(2 * (3 + 2.1)) ^ 3")
  }

  @Test
  fun divideTest1() {
    val e1 = op(Divide, op(Divide, Value(8.0), Value(2.0)), Value(4.0))
    assertThat(e1.apply(ModelContext())).isEqualTo(1.0)
    assertThat(e1.show()).isEqualTo("8 / 2 / 4")
  }

  @Test
  fun divideTest2() {
    val e1 = op(Divide, Value(8.0), op(Divide, Value(2.0), Value(4.0)))
    assertThat(e1.apply(ModelContext())).isEqualTo(16.0)
    assertThat(e1.show()).isEqualTo("8 / (2 / 4)")
  }

  @Test
  fun andTest1() {
    val e1 = op(And, Value(1.0), op(And, Value(2.0), Value(1.0)))
    assertThat(e1.apply(ModelContext())).isEqualTo(1.0)
    assertThat(e1.show()).isEqualTo("1 && 2 && 1")
  }

  @Test
  fun andTest2() {
    val e1 = op(And, op(And, Value(1.0), Value(2.0)), Value(0.0))
    assertThat(e1.apply(ModelContext())).isEqualTo(0.0)
    assertThat(e1.show()).isEqualTo("1 && 2 && 0")
  }

  @Test
  fun orTest1() {
    val e1 = op(Or, Value(1.0), op(Or, Value(2.0), Value(1.0)))
    assertThat(e1.apply(ModelContext())).isEqualTo(1.0)
    assertThat(e1.show()).isEqualTo("1 || 2 || 1")
  }

  @Test
  fun orTest2() {
    val e1 = op(Or, op(Or, Value(1.0), Value(2.0)), Value(0.0))
    assertThat(e1.apply(ModelContext())).isEqualTo(1.0)
    assertThat(e1.show()).isEqualTo("1 || 2 || 0")
  }

  @Test
  fun notTest1() {
    val e1 = op(And, op(Not, Value(0.0)), Value(1.0))
    assertThat(e1.apply(ModelContext())).isEqualTo(1.0)
    assertThat(e1.show()).isEqualTo("!0 && 1")
  }

  @Test
  fun notTest2() {
    val e1 = op(Not, op(And, Value(0.0), Value(1.0)))
    assertThat(e1.apply(ModelContext())).isEqualTo(1.0)
    assertThat(e1.show()).isEqualTo("!(0 && 1)")
  }

}
