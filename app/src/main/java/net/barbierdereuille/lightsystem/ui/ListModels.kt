package net.barbierdereuille.lightsystem.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import net.barbierdereuille.lightsystem.R
import net.barbierdereuille.lightsystem.data.Model

@Composable
fun ListModels(models: List<Model>?, navController: NavController? = null) {
  if(models == null) {
    ShowNoModels()
  } else {
    ShowModels(models, navController)
  }
}

@Composable
private fun ShowModels(models: List<Model>, navController: NavController? = null) {
  Page(stringResource(id = R.string.list_models_title)) {
    Box(modifier = Modifier.fillMaxSize()) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(12.dp)
      ) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
          item(key = 0) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
              Image(
                painter = painterResource(id = R.drawable.ic_lightsystems),
                contentDescription = "LightSystems Image",
                modifier = Modifier.size(150.dp)
              )
            }
          }
          items(models, key = { it.id }) { ShowModel(it) }
        }
      }
      Box(
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .padding(24.dp)
      ) {
        FloatingActionButton(
          onClick = { navController?.navigateToNewModel() },
          shape = CircleShape,
          containerColor = MaterialTheme.colorScheme.background,
        ) {
          Image(
            painter = painterResource(id = R.drawable.ic_add_icon),
            contentDescription = "Add model",
            colorFilter = ColorFilter.tint(Colors.DarkGreen),
            modifier = Modifier.size(48.dp)
          )
        }
      }
    }
  }
}

@Composable
private fun ShowModel(model: Model) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(8.dp)
  ) {
    Text(model.name)
  }
}

@Composable
private fun ShowNoModels() {
  Box(contentAlignment = Alignment.Center) {
    Text("<NO MODELS>")
  }
}

@Preview(showBackground = true)
@Composable
private fun ShowNoModelsPreview() {
  MaterialTheme {
    ShowNoModels()
  }
}

@Preview(showBackground = true, heightDp = 350, widthDp = 250)
@Composable
private fun ShowModelsPreview() {
  MaterialTheme {
    ListModels(
      listOf(
        Model(name = "model 1", id = 1, axiom = "L"),
        Model(name = "model 2", id = 2, axiom = "R"),
        Model(name = "model 3", id = 3, axiom = "A"),
      )
    )
  }
}
