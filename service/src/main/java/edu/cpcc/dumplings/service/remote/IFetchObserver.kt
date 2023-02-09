package edu.cpcc.dumplings.service.remote

import edu.cpcc.dumplings.core.model.FetchStatus
import com.github.kr328.kaidl.BinderInterface

@BinderInterface
fun interface IFetchObserver {
    fun updateStatus(status: FetchStatus)
}