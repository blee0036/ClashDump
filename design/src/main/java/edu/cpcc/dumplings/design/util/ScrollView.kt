package edu.cpcc.dumplings.design.util

import edu.cpcc.dumplings.design.view.ObservableScrollView

val ObservableScrollView.isTop: Boolean
    get() = scrollX == 0 && scrollY == 0
