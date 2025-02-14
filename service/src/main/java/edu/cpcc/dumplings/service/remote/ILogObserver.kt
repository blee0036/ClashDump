package edu.cpcc.dumplings.service.remote

import com.github.kr328.clash.core.model.LogMessage
import com.github.kr328.kaidl.BinderInterface

@BinderInterface
interface ILogObserver {
    fun newItem(log: LogMessage)
}