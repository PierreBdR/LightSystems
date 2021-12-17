package net.barbierdereuille.lightsystem.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.TextField
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import net.barbierdereuille.lightsystem.R
import net.barbierdereuille.lightsystem.workers.NewModelWorker

@Composable
fun NewModel(navController: NavController? = null) {
  Page("New Model") {
    var modelName by rememberSaveable { mutableStateOf("My Model") }
    var modelAxiom by rememberSaveable { mutableStateOf("A") }
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          stringResource(id = R.string.new_model_name),
          style = MaterialTheme.typography.bodyMedium
        )
        TextField(value = modelName, onValueChange = { modelName = it })
      }
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          stringResource(id = R.string.new_model_axiom),
          style = MaterialTheme.typography.bodyMedium
        )
        TextField(value = modelAxiom, onValueChange = { modelAxiom = it })
      }
      Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth().padding(15.dp)
      ) {
        Button(onClick = {
          navController?.popBackStack()
        }) {
          Text("Cancel")
        }
        Button(onClick = {
          NewModelWorker.execute(context, modelName, modelAxiom)
          navController?.popBackStack()
        }) {
          Text("Validate")
        }
      }
    }
  }
}