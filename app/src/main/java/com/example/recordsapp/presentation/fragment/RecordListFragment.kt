package com.example.recordsapp.presentation.fragment

import android.app.Activity
import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.os.storage.StorageManager
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recordsapp.R
import com.example.recordsapp.databinding.FragmentRecordListBinding
import com.example.recordsapp.domain.model.Note
import com.example.recordsapp.presentation.adapter.RecordsListAdapter
import com.example.recordsapp.presentation.viewmodel.RecordsListViewModel
import com.example.recordsapp.utils.collectLatestLifeCycleFlow
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
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

        collectLatestLifeCycleFlow(viewModel.notesList){ notesList ->
            homeAdapter.differ.submitList(notesList)
        }
        collectLatestLifeCycleFlow(viewModel.toast){ message ->
            toast(message)
        }
        binding?.btnExportPdf?.setOnClickListener {
            printPdf()
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
            if (viewModel.imageUri==null) {
                toast("Please Select Image")
                return@setOnClickListener
            }
            val image: Bitmap = MediaStore.Images.Media.getBitmap(
                requireActivity().contentResolver,
                viewModel.imageUri
            )
            if (title.length < 3) {
                noteTitle.error = "too short"
            } else if (des.length < 15) {
                noteDescription.error = "Must have more then 15 Characters"
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
                    toast(com.github.dhaval2404.imagepicker.ImagePicker.getError(data))
                }
                else -> {
                    toast("Task Cancelled")
                }
            }
        }


    @Throws(IOException::class)
    fun printPdf() {

        try {
          // internal memory/ storage
            val filePDFOutput = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val storageManager =
                    getSystemService(requireActivity().applicationContext, StorageManager::class.java)
                val storageVolume = storageManager!!.storageVolumes[0]
                File(storageVolume.directory?.path + "/Download/Records.pdf")
            } else {
                File(Environment.getExternalStorageDirectory(), "Records.pdf")
            };
            val pdfDocument = PdfDocument()

            val listOfNotes = viewModel.notesList.value
            if (listOfNotes.isEmpty())  toast("Please Add AtLeast 1 Record")


            val inflater = LayoutInflater.from(context)
            val view = inflater.inflate(R.layout.item_record, null)

            val head = view.findViewById<TextView>(R.id.head)
            val subhead = view.findViewById<TextView>(R.id.subhead)
            val image = view.findViewById<ImageView>(R.id.image)

            listOfNotes.forEachIndexed { index, note ->

                head.text = note.title
                subhead.text = note.description
                image.setImageBitmap(note.image)

                val pageInfo = PdfDocument.PageInfo.Builder(
                    binding?.homeRv?.width!!,
                    binding?.homeRv?.height!!,
                    index + 1
                ).create()
                val page = pdfDocument.startPage(pageInfo)


                val bitmap = getBitmapFromView(view)
                page.canvas.drawBitmap(bitmap!!, 0F, 0F, null)
                pdfDocument.finishPage(page)

            }

            pdfDocument.writeTo(FileOutputStream(filePDFOutput))
            pdfDocument.close()

            Toast.makeText(requireContext(), "Pdf Saved To Downloads Folder", Toast.LENGTH_SHORT)
                .show()


        }catch (e:Exception){
            println(e.stackTrace)

            Toast.makeText(requireContext(), "Something Went Wrong", Toast.LENGTH_SHORT)
                .show()

        }



    }

    private fun getBitmapFromView(view: View): Bitmap? {
        //Fetch the dimensions of the viewport
        val displayMetrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context?.display?.getRealMetrics(displayMetrics)
            displayMetrics.densityDpi
        } else {
            activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        }
        view.measure(
            View.MeasureSpec.makeMeasureSpec(
                displayMetrics.widthPixels, View.MeasureSpec.EXACTLY
            ),
            View.MeasureSpec.makeMeasureSpec(
                displayMetrics.heightPixels, View.MeasureSpec.EXACTLY
            )
        )
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)

        val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        val bgDrawable = view.background
        if (bgDrawable != null) bgDrawable.draw(canvas) else canvas.drawColor(Color.WHITE)
        view.draw(canvas)
        return returnedBitmap
    }

}