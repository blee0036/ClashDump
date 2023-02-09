package edu.cpcc.dumplings.design.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import edu.cpcc.dumplings.core.model.Provider
import edu.cpcc.dumplings.design.BR

class ProviderState(
    val provider: Provider,
    updatedAt: Long,
    updating: Boolean,
) : BaseObservable() {
    var updatedAt: Long = updatedAt
        @Bindable get
        set(value) {
            field = value

            notifyPropertyChanged(BR.updatedAt)
        }

    var updating: Boolean = updating
        @Bindable get
        set(value) {
            field = value

            notifyPropertyChanged(BR.updating)
        }
}