package com.solarisd.calc.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.solarisd.calc.R
import com.solarisd.calc.databinding.FragmentDefKeyboardBinding
import com.solarisd.calc.viewmodel.MainViewModel

class DefKeyboardFragment : Fragment() {
    companion object {
        @JvmStatic
        fun newInstance() = DefKeyboardFragment()
    }
    private val vm: MainViewModel by activityViewModels()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<FragmentDefKeyboardBinding>(inflater, R.layout.fragment_def_keyboard, container, false)
        binding.vm = vm
        return binding.root
    }
}