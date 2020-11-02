package com.dmitryluzev.calculator.core.operations

import com.dmitryluzev.calculator.core.Value
import java.lang.Exception

abstract class BinaryOperation(): Operation {
    override var a: Value? = null
    var b: Value? = null
    protected abstract fun equal(a: Value, b: Value): Value
    override val result: Value?
        get() =
            if (a != null && b != null){
                try {
                    equal(a!!, b!!)
                }catch (e: Exception){
                    Value.NaN
                }
            } else {
                null
            }

    override fun toStoreString(): String = "${Companion.getTagId(this)};${a};${b}"
}