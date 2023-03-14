package com.example.recordsapp.presentation.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.recordsapp.databinding.FragmentRecordDetailsBinding


class RecordDetailsFragment : BaseFragment<FragmentRecordDetailsBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentRecordDetailsBinding
            = FragmentRecordDetailsBinding::inflate


    override fun setup() {
    }
}