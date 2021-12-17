package net.barbierdereuille.lightsystem.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.DropdownMenu
import androidx.compose.material.TopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import net.barbierdereuille.lightsystem.R
import net.barbierdereuille.lightsystem.workers.SeedDatabaseWorker

@Composable
fun Page(name: String, content: @Composable () -> Unit) {
  Column(modifier = Modifier.fillMaxSize()) {
    TopAppBar(
      backgroundColor = MaterialTheme.colorScheme.background,
      title = {
        Row(modifier = Modifier.fillMaxWidth()) {
          Text(
            name,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f)
          )
          LightweightSystemsMenu()
        }
      },
    )
    content()
  }
}

@Composable
fun LightweightSystemsMenu() {
  var showMenu by rememberSaveable { mutableStateOf(false) }
  val context = LocalContext.current
  Box {
    Image(
      painter = painterResource(id = R.drawable.ic_lightsystems),
      contentDescription = "System Menu",
      modifier = Modifier
        .size(32.dp)
        .clickable { showMenu = true }
    )
    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
      Text(
        "Reset database",
        modifier = Modifier.clickable {
          SeedDatabaseWorker.execute(context, reset = true)
          showMenu = false
        }
      )
    }
  }
}
