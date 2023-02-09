package edu.cpcc.dumplings.service.remote

import edu.cpcc.dumplings.core.model.LogMessage
import com.github.kr328.kaidl.BinderInterface

@BinderInterface
interface ILogObserver {
    fun newItem(log: LogMessage)
}