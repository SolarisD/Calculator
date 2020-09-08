package com.solarisd.calc.core

import androidx.lifecycle.MutableLiveData
import com.solarisd.calc.core.enums.Operators
import com.solarisd.calc.core.enums.States
import com.solarisd.calc.core.enums.Symbols
import java.lang.Exception
import java.math.BigDecimal
import java.math.RoundingMode

class Calculator {
    val value: MutableLiveData<String> = MutableLiveData()
    val memory: MutableLiveData<String> = MutableLiveData()
    val history: MutableLiveData<Operation> = MutableLiveData()
    private val buffer = InputBuffer()
    private val fs = FS()
    private var m: BigDecimal? = null
    fun clear(){
        fs.clear()
        buffer.clear()
        value.postValue(buffer.value)
        history.postValue(null)
    }
    fun symbol(symbol: Symbols){
        if (fs.state == States.VALUE_SAVED || fs.state == States.RESULT) clear()
        buffer.symbol(symbol)
        if (buffer.value!!.length > 2){
            value.postValue(buffer.value?.fromDisplayString()?.toDisplayString())
        } else {
            value.postValue(buffer.value)
        }
    }
    fun operator(operator: Operators){
        try {
            fs.operator(operator, buffer.value?.toBigDecimal())
            buffer.clear()
            val result = fs.value
            value.postValue(result.toDisplayString())
            history.postValue(fs.lastOperation)
        }catch (e: Exception){
            value.postValue("Error")
            history.postValue(fs.lastOperation)
        }
    }
    fun result(){
        try {
            fs.result(buffer.value?.toBigDecimal())
            value.postValue(fs.value.toDisplayString())
            history.postValue(fs.lastOperation)
        }catch (e: Exception){
            value.postValue("Error")
            history.postValue(fs.lastOperation)
        }

    }
    fun negative(){
        when(fs.state){
            States.CLEARED, States.OPERATOR_SAVED -> {
                buffer.negative()
                value.postValue(buffer.value)
            }
            else -> {
                fs.negative()
                value.postValue(fs.value.toDisplayString())
            }
        }
    }
    fun backspace(){
        when(fs.state){
            States.CLEARED, States.OPERATOR_SAVED -> {
                buffer.backspace()
                value.postValue(buffer.value)
            }
            else -> {}
        }
    }
    fun percent(){
        val src = fs.value
        val input = buffer.value?.toBigDecimal() ?: BigDecimal.ZERO
        buffer.clear()
        val prc = src.multiply(BigDecimal(0.01)).multiply(input).setScale(10, RoundingMode.HALF_UP).stripTrailingZeros()
        fs.result(prc)
        value.postValue(fs.value.toDisplayString())
    }

    fun memoryClear(){
        m = null
        memory.postValue(null)
    }
    fun memoryPlus(){
        value.value?.let {
            if (m != null) m = m!!.add(it.fromDisplayString())
            else m = it.fromDisplayString()
            memory.postValue(m?.toDisplayString())
        }
    }
    fun memoryMinus(){
        value.value?.let {
            if (m != null) m = m!!.subtract(it.fromDisplayString())
            else m = -it.fromDisplayString()
            memory.postValue(m?.toDisplayString())
        }
    }
    fun memoryRestore(){
        m?.let{
            buffer.setDecimal(it)
        }
        value.postValue(buffer.value?.fromDisplayString()?.toDisplayString())
    }

    fun historyClear(){
        history.postValue(null)
    }
}