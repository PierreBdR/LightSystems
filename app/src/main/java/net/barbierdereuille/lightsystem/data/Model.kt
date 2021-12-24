package net.barbierdereuille.lightsystem.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Model(
  val name: String,
  val axiom: String,
  val rules: List<Rule>? = null,
  val id: Long = NO_ID,
) : Parcelable {
  companion object {
    const val NO_ID: Long = 0
  }
}
