package com.solarisd.calc.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.lifecycle.*
import androidx.preference.PreferenceManager
import com.solarisd.calc.core.*
import com.solarisd.calc.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(application: Application): AndroidViewModel(application){
    //PRIVATE
    private val c = Core()
    private val v = application.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    private val dao: Dao = DB.getInstance(application).dao()
    private val pref by lazy { PreferenceManager.getDefaultSharedPreferences(application) }
    private val vibro: Boolean
    get() = pref.getBoolean("vibration_buttons", false)
    private fun vibrate(){
        if (vibro){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                v.vibrate(50);
            }
        }
    }
    //PUBLIC
    val keyboard: Boolean
    get() = pref.getBoolean("extended_keyboard", false)
    val bufferDisplay:  LiveData<String> = Transformations.map(c.buffer){
        it?.toString() ?: "0"
    }
    val memoryDisplay:  LiveData<String> = Transformations.map(c.memory){
        if (it.isNullOrEmpty()) ""
        else "M: $it"
    }
    val historyDisplay:  LiveData<String> = Transformations.map(c.history){
        //SAVE DATA TO DB
        it?.let {
            if (it.isComplete){
                viewModelScope.launch(Dispatchers.IO) {
                    dao.insert(Record(expression = it.toString()))
                }
            }
        }
        //POST TO DISPLAY
        it?.toString() ?: ""
    }
    val historyRecords: LiveData<List<Record>> = dao.getLiveRecords()
    fun buttonPressed(button: Buttons){
        vibrate()
        when(button) {
            Buttons.ZERO-> c.symbol('0')
            Buttons.ONE-> c.symbol('1')
            Buttons.TWO-> c.symbol('2')
            Buttons.THREE-> c.symbol('3')
            Buttons.FOUR-> c.symbol('4')
            Buttons.FIVE-> c.symbol('5')
            Buttons.SIX-> c.symbol('6')
            Buttons.SEVEN-> c.symbol('7')
            Buttons.EIGHT->  c.symbol('8')
            Buttons.NINE-> c.symbol('9')
            Buttons.DOT-> c.symbol('.')
            Buttons.NEGATIVE -> c.negative()
            Buttons.CLEAR-> c.clear()
            Buttons.BACKSPACE-> c.backspace()
            Buttons.RESULT-> c.result()
            Buttons.PLUS-> c.operation(Operations.Add())
            Buttons.MINUS-> c.operation(Operations.Subtract())
            Buttons.MULTIPLY-> c.operation(Operations.Multiply())
            Buttons.DIVIDE-> c.operation(Operations.Divide())
            Buttons.PERCENT-> c.percent()
            Buttons.SQR-> c.operation(Operations.Sqr())
            Buttons.SQRT-> c.operation(Operations.Sqrt())
            Buttons.SIN-> c.operation(Operations.Sin())
            Buttons.COS-> c.operation(Operations.Cos())
            Buttons.TAN-> c.operation(Operations.Tan())
            Buttons.M_CLEAR-> c.memoryClear()
            Buttons.M_PLUS-> c.memoryPlus()
            Buttons.M_MINUS-> c.memoryMinus()
            Buttons.M_RESTORE-> c.memoryRestore()
        }
    }
    fun clearHistory() {
        dao.deleteAll()
    }
}