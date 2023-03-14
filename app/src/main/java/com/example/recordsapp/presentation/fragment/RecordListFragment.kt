package com.example.recordsapp.presentation.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.recordsapp.databinding.FragmentRecordListBinding


class RecordListFragment : BaseFragment<FragmentRecordListBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentRecordListBinding
            = FragmentRecordListBinding::inflate


    override fun setup() {
     }
}