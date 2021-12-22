package net.barbierdereuille.lightsystem.ui

import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import net.barbierdereuille.lightsystem.LightSystemsTag
import net.barbierdereuille.lightsystem.data.Model

@Composable
fun EditModel(model: Model?) {
  if (model == null) {
    Page("Edit <null> model") { Text("Null model") }
    return
  }
  Page("Edit Model ${model.name}") {
    Text("Full model: $model")
    Log.i(LightSystemsTag, "full model: $model")
  }
}