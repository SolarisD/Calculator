package com.dmitryluzev.core

import androidx.lifecycle.MutableLiveData
import com.dmitryluzev.core.operations.BinaryOperation
import com.dmitryluzev.core.operations.Operation
import com.dmitryluzev.core.operations.OperationFactory
import com.dmitryluzev.core.operations.UnaryOperation
import com.dmitryluzev.core.values.Value

class Alu constructor(){
    val out = MutableLiveData<Operation>()
    var operation: Operation? = null
        private set
    private var onResultReadyListener: ((Operation)->Unit)? = null

    fun setState(current: Operation? = null){
        operation = current
        post()
    }
    fun clear(){
        operation = null
        post()
    }
    fun setOperation(id: String, value: Value){
        OperationFactory.create(id, value)?.let {
            operation = it
            if (it is UnaryOperation) {
                onResultReadyListener?.invoke(operation!!)
            }
            post()
        }

    }
    fun changeOperation(id: String){
        operation = OperationFactory.change(operation, id)
        post()
    }
    fun setValue(value: Value){
        if(operation is BinaryOperation){
            (operation as BinaryOperation).apply { b = value }
            onResultReadyListener?.invoke(operation!!)
        }
        post()
    }
    fun repeat(){
        operation = OperationFactory.repeat(operation)
        operation?.let { onResultReadyListener?.invoke(it) }
        post()
    }
    fun setPercent(value: Value){
        if(operation is BinaryOperation){
            (operation as BinaryOperation).apply { b = value; percentage = true }
        } else {
            operation = OperationFactory.create(OperationFactory.DIVIDE_ID, value, Value(100.0), false)
        }
        operation?.let { onResultReadyListener?.invoke(it) }
        post()
    }
    fun setOnResultReadyListener(listener:(Operation) -> Unit){
        onResultReadyListener = listener
    }

    private fun post(){
        operation?.let {
            if (it.result == null) {
                out.value = operation
                return
            }
        }
        out.value = null
    }
}