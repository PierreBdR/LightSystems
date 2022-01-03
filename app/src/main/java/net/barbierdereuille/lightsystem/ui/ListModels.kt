package net.barbierdereuille.lightsystem.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.barbierdereuille.lightsystem.R
import net.barbierdereuille.lightsystem.data.Model

@Composable
fun ListModels(models: List<Model>?) {
  if(models == null) {
    ShowLoading()
  } else {
    ShowModels(models)
  }
}

@Composable
private fun ShowModels(models: List<Model>) {
  Page(stringResource(id = R.string.list_models_title)) {
    val navController = LocalNavigator.current
    Box(modifier = Modifier.fillMaxSize()) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(12.dp)
      ) {
        if(models.isEmpty()) {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .padding(8.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              stringResource(R.string.no_models),
              style = MaterialTheme.typography.displaySmall,
              color = MaterialTheme.colorScheme.primaryContainer,
              textAlign = TextAlign.Center,
            )
          }
        } else {
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
  val navController = LocalNavigator.current
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(8.dp)
      .clickable { navController?.navigateToEditModel(model.id) }
  ) {
    Text(model.name)
  }
}

@Composable
private fun ShowLoading() {
  Page(stringResource(id = R.string.list_models_title)) {
    Column(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        stringResource(R.string.loading_models),
        color = MaterialTheme.colorScheme.secondary,
        style = MaterialTheme.typography.titleLarge,
      )
      CircularProgressIndicator()
    }
  }
}

@Preview(showBackground = true)
@Composable
private fun ShowLoadingPreview() {
  MaterialTheme {
    Surface(color = MaterialTheme.colorScheme.background) {
      ShowLoading()
    }
  }
}

/*
@Preview(showBackground = true, heightDp = 350, widthDp = 250)
@Composable
private fun ShowModelsPreview() {
  MaterialTheme {
    Surface(color = MaterialTheme.colorScheme.background) {
      ListModels(
        listOf(
          Model(name = "model 1", id = 1, start = "L"),
          Model(name = "model 2", id = 2, start = "R"),
          Model(name = "model 3", id = 3, start = "A"),
        )
      )
    }
  }
}

@Preview(showBackground = true, heightDp = 350, widthDp = 250)
@Composable
private fun ShowModelsEmptyPreview() {
  MaterialTheme {
    Surface(color = MaterialTheme.colorScheme.background) {
      ListModels(emptyList())
    }
  }
}
*/