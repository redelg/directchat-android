package com.codergang.directchat.ui.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.codergang.directchat.R
import com.codergang.directchat.databinding.FragmentStepThreeBinding

class StepThreeFragment : Fragment() {

    private lateinit var binding: FragmentStepThreeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStepThreeBinding.inflate(layoutInflater)
        return binding.root
    }

}