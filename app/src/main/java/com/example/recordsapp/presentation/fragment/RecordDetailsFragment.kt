package com.example.recordsapp.presentation.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.recordsapp.MainActivity
import com.example.recordsapp.databinding.FragmentRecordDetailsBinding
import com.example.recordsapp.presentation.viewmodel.DetailsViewModel


class RecordDetailsFragment : BaseFragment<FragmentRecordDetailsBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentRecordDetailsBinding
            = FragmentRecordDetailsBinding::inflate
    private val args by navArgs<RecordDetailsFragmentArgs>()

    private val viewModel: DetailsViewModel by viewModels()



    override fun setup() {

        binding?.note = args.note

        binding?.btnBack?.setOnClickListener {
            (activity as MainActivity).onBackPressed()
        }

        binding?.btnDelete?.setOnClickListener {
            viewModel.deleteNoteById(args.note.id)
        }

        viewModel.isNoteDeleted.observe(viewLifecycleOwner){
            if (it)findNavController().navigate(RecordDetailsFragmentDirections.actionRecordDetailsFragmentToRecordListFragment())
        }
    }


}