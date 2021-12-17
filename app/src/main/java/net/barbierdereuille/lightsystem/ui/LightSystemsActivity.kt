package net.barbierdereuille.lightsystem.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import net.barbierdereuille.lightsystem.R
import net.barbierdereuille.lightsystem.data.Model
import net.barbierdereuille.lightsystem.data.ModelDatabase
import net.barbierdereuille.lightsystem.viewmodels.ModelsViewModel

@AndroidEntryPoint
class LightSystemsActivity : ComponentActivity() {

  @Inject lateinit var database: ModelDatabase
  private val modelsViewModel: ModelsViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      MaterialTheme {
        val models by modelsViewModel.models.observeAsState()
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          models?.let { ShowModels(it) } ?: ShowNoModels()
        }
      }
    }
  }
}

@Composable
fun ShowModels(models: List<Model>) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(12.dp)
  ) {
    Title()
    LazyColumn(modifier = Modifier.fillMaxSize()) {
      item(key = 0) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Center) {
          Image(
            painter = painterResource(id = R.drawable.ic_lightsystems),
            contentDescription = "LightSystems Image",
            modifier = Modifier
              .size(150.dp)
          )
        }
      }
      items(models, key = { it.id }) { ShowModel(it) }
    }
  }
}

@Composable
fun ShowModel(model: Model) {
  Row(modifier = Modifier
    .fillMaxWidth()
    .padding(8.dp)) {
    Text(model.name)
  }
}

@Composable
fun ShowNoModels() {
  Box(contentAlignment = Center) {
    Text("<NO MODELS>")
  }
}

@Composable
fun ColumnScope.Title() {
  Text(
    text = "LightSystems Models",
    modifier = Modifier.align(CenterHorizontally),
    style = MaterialTheme.typography.titleMedium
  )
  Spacer(modifier = Modifier.size(30.dp))
}

@Preview(showBackground = true, heightDp = 350, widthDp = 250)
@Composable
fun ShowModelsPreview() {
  MaterialTheme {
    ShowModels(listOf(
      Model(name = "model 1", id = 1, axiom = "L"),
      Model(name = "model 2", id = 2, axiom = "R"),
      Model(name = "model 3", id = 3, axiom = "A"),
    ))
  }
}

@Preview(showBackground = true)
@Composable
fun ShowNoModelsPreview() {
  MaterialTheme {
    ShowNoModels()
  }
}