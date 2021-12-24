package net.barbierdereuille.lightsystem.ui

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.TextField
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import net.barbierdereuille.lightsystem.R
import net.barbierdereuille.lightsystem.data.Model
import net.barbierdereuille.lightsystem.data.Rule
import net.barbierdereuille.lightsystem.workers.SaveModelWorker
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import net.barbierdereuille.lightsystem.LightSystemsTag
import net.barbierdereuille.lightsystem.workers.DeleteModelWorker

@Composable
fun EditModel(model: Model?) {
  if(model == null) {
    Page(stringResource(R.string.loading_model)) { CircularProgressIndicator() }
    return
  }
  Page("Edit Model") {
    val modelState = rememberSaveable(model) { mutableStateOf(model) }
    var editedModel by modelState
    val scrollState = rememberScrollState()
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
        .verticalScroll(scrollState),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      EditableField(
        name = R.string.new_model_name,
        value = editedModel.name,
        onValueChange = { editedModel = editedModel.copy(name = it) })

      EditableField(
        name = R.string.new_model_axiom,
        value = editedModel.axiom,
        onValueChange = { editedModel = editedModel.copy(axiom = it) })

      editedModel.rules?.let {
        EditRules(it, modelState)
      }

      Navigation(editedModel, addRule = {
        editedModel = editedModel.copy(rules = editedModel.rules!! + Rule("lhs", "rhs"))
      })
    }
  }
}

private data class RuleItem(
  val type: Rule.Part,
  val index: Int
)

@Composable
private fun EditRules(
  rules: List<Rule>,
  modelState: MutableState<Model>,
) {
  Box(
    modifier = Modifier
      .padding(4.dp)
      .border(width = 1.dp, color = MaterialTheme.colorScheme.secondary)
      .padding(8.dp)
  ) {
    if(rules.isEmpty()) {
      Text(
        stringResource(R.string.no_rules),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.primaryContainer,
      )
    } else {
      ShowRules(
        rules,
        modelState,
      )
    }
  }
}

private const val deleteId = "DELETE"
private val lhsId = Rule.Part.LHS.name
private val rhsId = Rule.Part.RHS.name
private const val arrowId = "ARROW"

@Composable
private fun ShowRules(
  rules: List<Rule>,
  modelState: MutableState<Model>,
) {
  val measurePolicy = MeasurePolicy { measurables, constraints ->
    Log.i(LightSystemsTag, "lhsId = $lhsId")
    Log.i(LightSystemsTag, "rhsId = $rhsId")
    // First split things
    val deleteMeasurables = measurables.filter { it.layoutId == deleteId }
    val lhsMeasurables = measurables.filter { it.layoutId == lhsId }
    val arrowMeasurables = measurables.filter { it.layoutId == arrowId }
    val rhsMeasurables = measurables.filter { it.layoutId == rhsId }

    check(
      deleteMeasurables.size == lhsMeasurables.size &&
        lhsMeasurables.size == arrowMeasurables.size &&
        arrowMeasurables.size == rhsMeasurables.size
    ) {
      "There must be as many delete, lhs, arrow and rhs objects."
    }

    val deletePlaceables = deleteMeasurables.map { it.measure(constraints) }
    val lhsPlaceables = lhsMeasurables.map { it.measure(constraints) }
    val arrowPlaceables = arrowMeasurables.map { it.measure(constraints) }

    val deleteWidth = deletePlaceables.maxOf { it.width }
    val lhsWidth = lhsPlaceables.maxOf { it.width }
    val arrowWidth = arrowPlaceables.maxOf { it.width }

    val maxWidth = constraints.maxWidth
    val splitRhs = maxWidth - (arrowWidth + lhsWidth + deleteWidth) < maxWidth / 3

    val rhsMaxWidth =
      if(splitRhs) 3 * maxWidth / 4 else maxWidth - lhsWidth - arrowWidth - deleteWidth
    val rhsConstraints = constraints.copy(maxWidth = rhsMaxWidth)
    val rhsPlaceables = rhsMeasurables.map { it.measure(rhsConstraints) }

    val lhsHeight = listOf(
      lhsPlaceables.sumOf { it.height },
      arrowPlaceables.sumOf { it.height },
      deletePlaceables.sumOf { it.height },
    ).maxOf { it }
    val rhsHeight = rhsPlaceables.sumOf { it.height }
    val height = if(splitRhs) {
      lhsHeight + rhsHeight
    } else {
      maxOf(lhsHeight, rhsHeight)
    }
    layout(width = constraints.maxWidth, height = height) {
      var yPosition = 0
      lhsPlaceables.indices.forEach { index ->
        val lhs = lhsPlaceables[index]
        val rhs = rhsPlaceables[index]
        val arrow = arrowPlaceables[index]
        val delete = deletePlaceables[index]
        val leftHeight = listOf(delete, lhs, arrow).maxOf { it.height }
        delete.placeRelative(x = 0, y = yPosition + (leftHeight - lhs.height) / 2)
        lhs.placeRelative(
          x = deleteWidth + lhsWidth - lhs.width,
          y = yPosition + (leftHeight - lhs.height) / 2
        )
        arrow.placeRelative(
          x = deleteWidth + lhsWidth,
          y = yPosition + (leftHeight - arrow.height) / 2
        )
        if(splitRhs) {
          yPosition += leftHeight
          rhs.placeRelative(x = maxWidth / 4, y = yPosition)
          yPosition += rhs.height
        } else {
          rhs.placeRelative(x = deleteWidth + lhsWidth + arrowWidth, y = yPosition)
          yPosition += maxOf(leftHeight, rhs.height)
        }
      }
    }
  }
  Layout(measurePolicy = measurePolicy, content =
  {
    var model by modelState
    rules.forEachIndexed { index, rule ->
      Image(
        painter = painterResource(R.drawable.ic_deletion_icon),
        contentDescription = "Delete rule $index",
        modifier = Modifier
          .layoutId(deleteId)
          .padding(8.dp)
          .size(32.dp)
          .clickable {
            model = model.copy(rules = rules.take(index) + rules.drop(index + 1))
          },
      )
      RulePart(RuleItem(Rule.Part.LHS, index), rule.lhs, modelState)
      Image(
        painter = painterResource(R.drawable.ic_rule_apply),
        contentDescription = "Rule application",
        modifier = Modifier
          .layoutId(arrowId)
          .width(32.dp)
          .padding(horizontal = 8.dp),
        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
      )
      RulePart(RuleItem(Rule.Part.RHS, index), rule.rhs, modelState)
    }
  })
}

@Composable
private fun RulePart(
  ruleItem: RuleItem,
  value: String,
  modelState: MutableState<Model>,
) {
  var model by modelState
  val layoutId = ruleItem.type.name
  val index = ruleItem.index
  val textAlign = if(ruleItem.type == Rule.Part.LHS) TextAlign.End else TextAlign.Start
  Box(
    modifier = Modifier
      .layoutId(layoutId)
      .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp),
    contentAlignment = Alignment.Center,
  ) {
    TextField(
      value = value,
      singleLine = true,
      onValueChange = {
        val rules = model.rules!!
        model = model.copy(
          rules = rules.take(index) +
            rules[index].update(ruleItem.type, it) +
            rules.drop(index + 1)
        )
      },
      textStyle = MaterialTheme.typography.bodyMedium,
      modifier = Modifier
        .layoutId(layoutId)
        .defaultMinSize(minWidth = 48.dp)
        .focusTarget(),
    )
  }
}


@Composable
private fun EditableField(@StringRes name: Int, value: String, onValueChange: (String) -> Unit) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Text(
      stringResource(id = name),
      style = MaterialTheme.typography.bodyMedium,
      modifier = Modifier.width(120.dp),
    )
    TextField(
      value = value,
      onValueChange = onValueChange,
      modifier = Modifier.fillMaxWidth()
    )
  }
}

@Composable
private fun Navigation(model: Model, addRule: () -> Unit) {
  val navController = LocalNavigator.current
  val context = LocalContext.current
  Row(
    horizontalArrangement = Arrangement.SpaceEvenly,
    modifier = Modifier
      .fillMaxWidth()
      .padding(15.dp)
  ) {
    if(model.id == Model.NO_ID) {
      Button(onClick = {
        navController?.popBackStack()
      }) {
        Text("Cancel")
      }
    } else {
      Button(onClick = {
        DeleteModelWorker.execute(context, model)
        navController?.popBackStack()
      }) {
        Text("Delete")
      }
    }
    Button(onClick = {
      Log.i(LightSystemsTag, "Saving model $model")
      SaveModelWorker.execute(context, model)
      navController?.popBackStack()
    }) {
      Text("Validate")
    }

    if(model.rules != null) {
      Button(onClick = addRule) {
        Text("Add rule")
      }
    }
  }
}

@Preview(showBackground = true, heightDp = 350, widthDp = 250)
@Composable
private fun PreviewLoadingModel() {
  MaterialTheme {
    androidx.compose.material3.Surface(color = MaterialTheme.colorScheme.background) {
      EditModel(null)
    }
  }
}

@Preview(showBackground = true, heightDp = 350, widthDp = 250)
@Composable
private fun PreviewEditModel() {
  MaterialTheme {
    androidx.compose.material3.Surface(color = MaterialTheme.colorScheme.background) {
      EditModel(
        Model(
          name = "Test model",
          axiom = "A",
          rules = listOf(
            Rule("A", "AB"),
            Rule("B", "BA")
          )
        )
      )
    }
  }
}

