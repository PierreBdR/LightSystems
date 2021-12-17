package net.barbierdereuille.lightsystem.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import net.barbierdereuille.lightsystem.data.Model
import net.barbierdereuille.lightsystem.data.ModelDatabase
import androidx.lifecycle.asLiveData

@HiltViewModel
class ModelsViewModel @Inject internal constructor(
  database: ModelDatabase
) : ViewModel() {
  val models: LiveData<List<Model>> = database.allModels().asLiveData()
}
