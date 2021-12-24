package net.barbierdereuille.lightsystem.ui

import android.content.res.TypedArray
import android.nfc.Tag
import android.util.Log
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
import androidx.compose.material.DropdownMenu
import androidx.compose.material.TopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import net.barbierdereuille.lightsystem.LightSystemsTag
import net.barbierdereuille.lightsystem.R
import net.barbierdereuille.lightsystem.workers.SeedDatabaseWorker

@Composable
fun Page(name: String, content: @Composable () -> Unit) {
  Column(modifier = Modifier.fillMaxSize()) {
    TopAppBar(
      backgroundColor = MaterialTheme.colorScheme.background,
      navigationIcon = { BackNavigation() },
      title = {
        Text(
          name,
          style = MaterialTheme.typography.titleLarge,
          color = MaterialTheme.colorScheme.onBackground,
          modifier = Modifier.weight(1f)
        )
      },
      actions = {
        LightweightSystemsMenu()
      }
    )
    content()
  }
}

@Composable
private fun BackNavigation() {
  val navController = LocalNavigator.current
  if(navController?.isAtRoot() == false) {
    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier
        .size(48.dp)
        .clickable { navController.navigateUp() }
    ) {
      Image(
        painterResource(id = R.drawable.ic_back_arrow),
        contentDescription = "Previous screen",
        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer)
      )
    }
  }
}

@Composable
fun LightweightSystemsMenu() {
  var showMenu by rememberSaveable { mutableStateOf(false) }
  val context = LocalContext.current
  Box(
    contentAlignment = Alignment.Center,
    modifier = Modifier.size(48.dp)
  ) {
    Image(
      painter = painterResource(id = R.drawable.ic_lightsystems),
      contentDescription = "System Menu",
      modifier = Modifier
        .size(32.dp)
        .clickable { showMenu = true }
    )
    DropdownMenu(
      expanded = showMenu,
      onDismissRequest = { showMenu = false },
      modifier = Modifier.padding(start = 8.dp, end = 8.dp),
    ) {
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
