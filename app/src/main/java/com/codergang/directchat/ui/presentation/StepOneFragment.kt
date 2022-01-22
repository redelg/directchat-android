package com.codergang.directchat.ui.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codergang.directchat.R
import com.codergang.directchat.databinding.FragmentStepOneBinding


class StepOneFragment : Fragment() {

    private lateinit var binding: FragmentStepOneBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStepOneBinding.inflate(layoutInflater)
        return inflater.inflate(R.layout.fragment_step_one, container, false)
    }

}