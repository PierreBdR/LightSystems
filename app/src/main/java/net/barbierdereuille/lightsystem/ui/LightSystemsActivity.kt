package net.barbierdereuille.lightsystem.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import net.barbierdereuille.lightsystem.viewmodels.ModelsViewModel

@AndroidEntryPoint
class LightSystemsActivity : ComponentActivity() {

  private val modelsViewModel: ModelsViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      val navController = rememberNavController()
      MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          NavHost(navController, startDestination = "listModels") {
            composable("listModels") {
              val models by modelsViewModel.models.observeAsState()
              ListModels(models, navController)
            }
            composable("newModel") {
              NewModel(navController)
            }
          }
        }
      }
    }
  }
}

fun NavController.navigateToListModels() { navigate("listModels") }
fun NavController.navigateToNewModel() { navigate("newModel") }