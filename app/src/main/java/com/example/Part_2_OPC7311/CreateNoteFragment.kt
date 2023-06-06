package com.example.Part_2_OPC7311

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.Part_2_OPC7311.database.NotesDatabase
import com.example.Part_2_OPC7311.entities.Notes
import com.example.Part_2_OPC7311.util.NoteBottomSheetFragment
import kotlinx.android.synthetic.main.fragment_create_note.*
import kotlinx.android.synthetic.main.fragment_create_note.tvDateTime
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.util.*


class CreateNoteFragment : BaseFragment(), EasyPermissions.RationaleCallbacks,
    EasyPermissions.PermissionCallbacks {
    private var param1: String? = null
    private var param2: String? = null
    private var currentDate: String? = null
    private var selectedColor = "#171C26"

    private var READ_STORAGE_PERM = 123
    private var REQUEST_CODE_IMAGE = 456
    private var selectedImagePath = ""
    private var webLink = ""
    private var noteId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        noteId = requireArguments().getInt("noteId", -1)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_note, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            CreateNoteFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (noteId != -1) {
            launch {
                context?.let {
                    var notes = NotesDatabase.getDatabase(it).noteDao().getSpecificNote(noteId)

                    colorView.setBackgroundColor(Color.parseColor(notes.color))
                    colorView2.setBackgroundColor(Color.parseColor(notes.color))

                    etNoteTitle.setText(notes.title)
                    etNoteSubTitle.setText(notes.subTitle)
                    etNoteDescription.setText(notes.noteText)

                    if (notes.imgPath != null && notes.imgPath != "") {
                        selectedImagePath = notes.imgPath!!
                        ivNoteCreate.setImageBitmap(BitmapFactory.decodeFile(notes.imgPath))
                        ivNoteCreate.visibility = View.VISIBLE
                        layoutImage.visibility = View.VISIBLE
                        ivDelete.visibility = View.VISIBLE
                    } else {
                        ivNoteCreate.visibility = View.GONE
                        layoutImage.visibility = View.GONE
                        ivDelete.visibility = View.GONE
                    }

                    if (notes.webLink != null && notes.webLink != "") {
                        tvWebLink.text = notes.webLink
                        layoutWebUrl.visibility = View.VISIBLE
                        etWebUrl.setText(notes.webLink)
                        ivDeleteUrl.visibility = View.VISIBLE
                        tvWebLink.visibility = View.VISIBLE
                    } else {
                        ivDeleteUrl.visibility = View.GONE
                        layoutWebUrl.visibility = View.GONE
                    }
                }
            }
        }


        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            BroadcastReceiver, IntentFilter("bottom_sheet_action")
        )

        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        currentDate = sdf.format(Date())

        colorView.setBackgroundColor(Color.parseColor(selectedColor))
        colorView2.setBackgroundColor(Color.parseColor(selectedColor))
        tvDateTime.text = currentDate

        //
        imgDone.setOnClickListener {
            if (noteId != -1) {
                updateNote()
            } else {
                saveNote()
            }
        }

        imgBack.setOnClickListener {
//            replaceFragment(HomeFragment.newInstance(), false)
            requireActivity().supportFragmentManager.popBackStack()
        }

        //
        imgMore.setOnClickListener {
            var noteBottomSheetFragment = NoteBottomSheetFragment.newInstance(noteId)
            noteBottomSheetFragment.show(
                requireActivity().supportFragmentManager,
                "Bottom Sheet"
            )
        }

        //
        btnOk.setOnClickListener {
            if (etWebUrl.text.toString().trim().isNotEmpty()) {
                checkWebUrl()
            } else {
                Toast.makeText(requireContext(), "Url is required", Toast.LENGTH_SHORT).show()
            }
        }

        //
        btnCancel.setOnClickListener {
            if (noteId != -1){
                tvWebLink.visibility = View.VISIBLE
                layoutWebUrl.visibility = View.GONE
            }else{
                layoutWebUrl.visibility = View.GONE
            }
        }

        //
        tvWebLink.setOnClickListener {
            var intent = Intent(Intent.ACTION_VIEW, Uri.parse(etWebUrl.text.toString()))
            startActivity(intent)
        }

        //
        ivDelete.setOnClickListener {
            selectedImagePath = ""
            layoutImage.visibility = View.GONE
        }

        //
        ivDeleteUrl.setOnClickListener {
            webLink = ""
            tvWebLink.visibility = View.GONE
            ivDeleteUrl.visibility = View.GONE
            layoutWebUrl.visibility = View.GONE
        }
    }

    private fun updateNote() {
        launch {
            context?.let {
                val notes = NotesDatabase.getDatabase(it).noteDao().getSpecificNote(noteId)
                notes.title = etNoteTitle.text.toString()
                notes.subTitle = etNoteSubTitle.text.toString()
                notes.noteText = etNoteDescription.text.toString()
                notes.dateTime = currentDate
                notes.color = selectedColor
                notes.imgPath = selectedImagePath
                notes.webLink = webLink

                NotesDatabase.getDatabase(it).noteDao().updateNote(notes)
                requireActivity().supportFragmentManager.popBackStack()
                Toast.makeText(context, "Insert Success", Toast.LENGTH_SHORT).show()

                etNoteTitle.setText("")
                etNoteSubTitle.setText("")
                etNoteDescription.setText("")
                ivNoteCreate.visibility = View.GONE
                tvWebLink.visibility = View.GONE
                layoutImage.visibility = View.GONE
            }
        }
    }

    private fun deleteNote() {
        launch {
            context?.let {
                NotesDatabase.getDatabase(it).noteDao().deleteSpecificNote(noteId)
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }

    private fun saveNote() {
        if (etNoteTitle.text.isNullOrEmpty()) {
            Toast.makeText(context, "Categorie name Required", Toast.LENGTH_SHORT).show()
        } else if (etNoteSubTitle.text.isNullOrEmpty()) {
            Toast.makeText(context, "Description is Required", Toast.LENGTH_SHORT).show()
        } else if (etNoteDescription.text.isNullOrEmpty()) {
            Toast.makeText(context, "Categorie information is Required", Toast.LENGTH_SHORT).show()
        } else {
            launch {
                val notes = Notes()

                notes.title = etNoteTitle.text.toString()
                notes.subTitle = etNoteSubTitle.text.toString()
                notes.noteText = etNoteDescription.text.toString()
                notes.dateTime = currentDate
                notes.color = selectedColor
                notes.imgPath = selectedImagePath
                notes.webLink = webLink

                context?.let {
                    NotesDatabase.getDatabase(it).noteDao().insertNotes(notes)
                    requireActivity().supportFragmentManager.popBackStack()
                    Toast.makeText(context, "Insert Success", Toast.LENGTH_SHORT).show()

                    etNoteTitle.setText("")
                    etNoteSubTitle.setText("")
                    etNoteDescription.setText("")
                    ivNoteCreate.visibility = View.GONE
                    tvWebLink.visibility = View.GONE
                    layoutImage.visibility = View.GONE
                }
            }
        }
    }

    fun replaceFragment(fragment: Fragment, istransition: Boolean) {
        val fragmentTransition = requireActivity().supportFragmentManager.beginTransaction()

        if (istransition) {
            fragmentTransition.setCustomAnimations(
                android.R.anim.slide_out_right,
                android.R.anim.slide_in_left
            )
        }
        fragmentTransition.add(R.id.frameLayout, fragment)
            .addToBackStack(fragment.javaClass.simpleName).commit()
    }

    private fun checkWebUrl() {
        layoutWebUrl.visibility = View.GONE
        etWebUrl.isEnabled = false
        webLink = etWebUrl.text.toString()
        tvWebLink.visibility = View.VISIBLE
        tvWebLink.text = etWebUrl.text.toString()
    }

    private val BroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            var actionColor = p1!!.getStringExtra("action")

            when (actionColor) {
                "Blue" -> {
                    selectedColor = p1.getStringExtra("selectedColor")!!
                    colorView.setBackgroundColor(Color.parseColor(selectedColor))
                    colorView2.setBackgroundColor(Color.parseColor(selectedColor))
                }

                "Yellow" -> {
                    selectedColor = p1.getStringExtra("selectedColor")!!
                    colorView.setBackgroundColor(Color.parseColor(selectedColor))
                    colorView2.setBackgroundColor(Color.parseColor(selectedColor))
                }

                "Purple" -> {
                    selectedColor = p1.getStringExtra("selectedColor")!!
                    colorView.setBackgroundColor(Color.parseColor(selectedColor))
                    colorView2.setBackgroundColor(Color.parseColor(selectedColor))
                }

                "Green" -> {
                    selectedColor = p1.getStringExtra("selectedColor")!!
                    colorView.setBackgroundColor(Color.parseColor(selectedColor))
                    colorView2.setBackgroundColor(Color.parseColor(selectedColor))
                }

                "Orange" -> {
                    selectedColor = p1.getStringExtra("selectedColor")!!
                    colorView.setBackgroundColor(Color.parseColor(selectedColor))
                    colorView2.setBackgroundColor(Color.parseColor(selectedColor))
                }

                "Black" -> {
                    selectedColor = p1.getStringExtra("selectedColor")!!
                    colorView.setBackgroundColor(Color.parseColor(selectedColor))
                    colorView2.setBackgroundColor(Color.parseColor(selectedColor))
                }

                "Image" -> {
                    readStorageTask()
                    layoutWebUrl.visibility = View.GONE
                }

                "WebUrl" -> {
                    layoutWebUrl.visibility = View.VISIBLE
                }

                "DeleteNote" -> {
                    // Delete Note
                    deleteNote()
                }

                else -> {
                    layoutImage.visibility = View.GONE
                    ivNoteCreate.visibility = View.GONE
                    layoutWebUrl.visibility = View.GONE

                    selectedColor = p1.getStringExtra("selectedColor")!!
                    colorView.setBackgroundColor(Color.parseColor(selectedColor))
                    colorView2.setBackgroundColor(Color.parseColor(selectedColor))
                }
            }
        }
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(BroadcastReceiver)
        super.onDestroy()
    }

    private fun hasReadStoragePermission(): Boolean {
        return EasyPermissions.hasPermissions(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    private fun readStorageTask() {
        if (hasReadStoragePermission()) {
//            Toast.makeText(requireContext(), "Permission Granted", Toast.LENGTH_SHORT).show()
            pickImageFromGallery()
        } else {
            EasyPermissions.requestPermissions(
                requireActivity(),
                "This app needs access your storage",
                READ_STORAGE_PERM,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }


    private fun pickImageFromGallery() {
        var intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(intent, REQUEST_CODE_IMAGE)
        }
    }

    private fun getPathFromUri(contentUri: Uri): String? {
        var filePath: String? = null
        var cursor = requireActivity().contentResolver.query(contentUri, null, null, null, null)
        if (cursor == null) {
            filePath = contentUri.path
        } else {
            cursor.moveToFirst()
            var index = cursor.getColumnIndex("_data")
            filePath = cursor.getString(index)
            cursor.close()
        }
        return filePath
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                var selectedImageUrl = data.data
                if (selectedImageUrl != null) {
                    try {
                        var inputStream =
                            requireActivity().contentResolver.openInputStream(selectedImageUrl)
                        var bitmap = BitmapFactory.decodeStream(inputStream)
                        ivNoteCreate.setImageBitmap(bitmap)
                        ivNoteCreate.visibility = View.VISIBLE
                        layoutImage.visibility = View.VISIBLE

                        selectedImagePath = getPathFromUri(selectedImageUrl)!!
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults,
            requireActivity()
        )
    }

    override fun onRationaleAccepted(requestCode: Int) {
    }

    override fun onRationaleDenied(requestCode: Int) {
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(requireActivity(), perms)) {
            AppSettingsDialog.Builder(requireActivity()).build().show()
        }
    }
}