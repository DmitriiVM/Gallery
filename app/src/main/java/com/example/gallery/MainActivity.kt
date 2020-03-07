package com.example.gallery

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), ColorRecyclerAdapter.OnItemClickListener {

    private lateinit var adapter: ColorRecyclerAdapter
    private var imageUriList = arrayListOf<Uri>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setRecyclerView()
        button.setOnClickListener {
            openDirectory()
        }
    }

    private fun setRecyclerView() {
        val numberOfRows =
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                NUMBER_OF_ROWS_PORTRAIT
            } else {
                NUMBER_OF_ROWS_HORIZONTAL
            }
        recyclerView.layoutManager =
            GridLayoutManager(this, numberOfRows, GridLayoutManager.HORIZONTAL, false)
        recyclerView.addItemDecoration(MarginItemDecoration(20))
        adapter = ColorRecyclerAdapter(this)
        recyclerView.adapter = adapter
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        val stringImageList = savedInstanceState.getStringArrayList(KEY_IMAGE_URI_LIST)

        imageUriList = stringImageList?.map { Uri.parse(it) } as ArrayList<Uri>
        adapter.setImages(imageUriList)
        super.onRestoreInstanceState(savedInstanceState)
    }

    private fun openDirectory() {
        Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            startActivityForResult(this, OPEN_DOCUMENT_TREE_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == OPEN_DOCUMENT_TREE_CODE && resultCode == Activity.RESULT_OK) {
            val directoryUri = data?.data ?: return
            getPictures(directoryUri)
        }
    }

    private fun getPictures(directoryUri: Uri) {
        val documentsTree = DocumentFile.fromTreeUri(this, directoryUri) ?: return
        val documents = documentsTree.listFiles()

        imageUriList = documents.filter {
            it.type == "image/jpeg" || it.type == "image/png" || it.type == "image/jpg"
        }.map { it.uri } as ArrayList<Uri>

        adapter.setImages(imageUriList)
    }

    override fun onItemClick(uri: Uri) {
        Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "image/*")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(this)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putStringArrayList(
            KEY_IMAGE_URI_LIST,
            imageUriList.map { it.toString() } as ArrayList)
        super.onSaveInstanceState(outState)
    }

    companion object {
        internal const val NUMBER_OF_ROWS_HORIZONTAL = 2
        internal const val NUMBER_OF_ROWS_PORTRAIT = 4
        internal const val OPEN_DOCUMENT_TREE_CODE = 0
        internal const val KEY_IMAGE_URI_LIST = "key_image_uri_list"
    }
}

