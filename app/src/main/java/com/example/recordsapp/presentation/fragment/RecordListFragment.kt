package com.example.recordsapp.presentation.fragment

import android.app.Activity
import android.app.Dialog
import android.graphics.Bitmap
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recordsapp.R
import com.example.recordsapp.databinding.FragmentRecordListBinding
import com.example.recordsapp.domain.model.Note
import com.example.recordsapp.presentation.adapter.RecordsListAdapter
import com.example.recordsapp.presentation.viewmodel.RecordsListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RecordListFragment : BaseFragment<FragmentRecordListBinding>() {

    private val viewModel: RecordsListViewModel by viewModels()

    @Inject
    lateinit var homeAdapter: RecordsListAdapter

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentRecordListBinding
            = FragmentRecordListBinding::inflate

    override fun setup() {
        setupRecyclerView()

        binding?.addBtn?.setOnClickListener {
            showDialog()
        }

        homeAdapter.setOnItemClickListener { note ->
            findNavController().navigate(
                RecordListFragmentDirections.actionRecordListFragmentToRecordDetailsFragment(note)
            )
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.notesList.collectLatest { notesList ->
                homeAdapter.differ.submitList(notesList)
            }
        }
     }


    private fun showDialog() {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.add_record_dialog)
        val noteTitle = dialog.findViewById(R.id.dialogTitle) as EditText
        val noteDescription = dialog.findViewById(R.id.dialogDes) as EditText
        val addBtn = dialog.findViewById(R.id.cd_addBtn) as Button
        val noBtn = dialog.findViewById(R.id.cd_close) as ImageView
        val dialogImage = dialog.findViewById(R.id.cdImageView) as ImageView
        noBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()

        dialogImage.setOnClickListener {
            com.github.dhaval2404.imagepicker.ImagePicker.with(this)
                .compress(1024)         //Final image size will be less than 1 MB(Optional)
                .maxResultSize(
                    1080,
                    720
                )  //Final image resolution will be less than 1080 x 1080(Optional)
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                    dialogImage.setImageURI(viewModel.imageUri)
                }


        }
        addBtn.setOnClickListener {
            val title = noteTitle.text.toString().trim()
            val des = noteDescription.text.toString().trim()
            val image: Bitmap = MediaStore.Images.Media.getBitmap(
                requireActivity().contentResolver,
                viewModel.imageUri
            )
            if (title.length < 5) {
                noteTitle.error = "too short"
            } else if (des.length < 100) {
                noteDescription.error = "Must have more then 100 Characters"
            } else {
                viewModel.insertNote(
                    Note(
                        title = title,
                        description = des,
                        image = image
                    )
                )
                dialog.dismiss()
            }

        }


    }

    private fun setupRecyclerView() {
        binding?.homeRv?.apply {
            adapter = homeAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            when (resultCode) {
                Activity.RESULT_OK -> {
                    //Image Uri will not be null for RESULT_OK
                    val fileUri = data?.data!!
                    viewModel.imageUri = fileUri

                }
                com.github.dhaval2404.imagepicker.ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(
                        requireActivity(),
                        com.github.dhaval2404.imagepicker.ImagePicker.getError(data),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    Toast.makeText(requireActivity(), "Task Cancelled", Toast.LENGTH_SHORT).show()
                }
            }
        }

}