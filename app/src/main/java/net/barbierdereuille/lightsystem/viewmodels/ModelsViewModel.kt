package net.barbierdereuille.lightsystem.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import net.barbierdereuille.lightsystem.data.Model
import net.barbierdereuille.lightsystem.data.Repository

@HiltViewModel
class ModelsViewModel @Inject internal constructor(repository: Repository) : ViewModel() {
  val models: LiveData<List<Model>> = repository.allModels().asLiveData()
}
