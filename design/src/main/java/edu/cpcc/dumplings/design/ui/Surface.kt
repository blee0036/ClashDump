package edu.cpcc.dumplings.design.ui

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import edu.cpcc.dumplings.design.BR

class Surface : BaseObservable() {
    var insets: Insets = Insets.EMPTY
        @Bindable get
        set(value) {
            field = value

            notifyPropertyChanged(BR.insets)
        }
}