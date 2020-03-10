package com.example.gallery

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.image_preview.*

class MainActivity : AppCompatActivity(), ColorRecyclerAdapter.OnItemClickListener,
    ColorRecyclerAdapter.OnItemLongClickListener {

    private lateinit var adapter: ColorRecyclerAdapter
    private var imageUriList = listOf<Uri>()
    private lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setRecyclerView()
        buttonOpenDirectory.setOnClickListener {
            openDirectory()
        }
    }

    private fun setRecyclerView() {
        recyclerView.apply {
            layoutManager =
                PreCachingLayoutManager(
                    this@MainActivity,
                    getNumberOfRows(),
                    GridLayoutManager.HORIZONTAL,
                    false
                )
            setHasFixedSize(true)
            addItemDecoration(
                getDivider(GridLayoutManager.VERTICAL, R.drawable.recyclerview_divider_horizontal)
            )
            addItemDecoration(
                getDivider(GridLayoutManager.HORIZONTAL, R.drawable.recyclerview_divider_vertical)
            )
            addOnItemTouchListener(onItemTouchListener)
        }
        adapter = ColorRecyclerAdapter(this, this)
        recyclerView.adapter = adapter
    }

    private fun getNumberOfRows() =
        when (resources.configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> NUMBER_OF_ROWS_PORTRAIT
            else -> NUMBER_OF_ROWS_HORIZONTAL
        }

    private fun getDivider(orientation: Int, drawable: Int) =
        DividerItemDecoration(this, orientation).apply {
            setDrawable(resources.getDrawable(drawable, null))
        }

    private val onItemTouchListener = object : RecyclerView.OnItemTouchListener {
        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {

            if (e.action == MotionEvent.ACTION_UP &&
                ::dialog.isInitialized && dialog.isShowing) {
                dialog.dismiss()
            }
            return false
        }

        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        val stringImageList = savedInstanceState.getStringArrayList(KEY_IMAGE_URI_LIST)
        imageUriList = stringImageList?.map { Uri.parse(it) } ?: return
        adapter.setImages(imageUriList)
        super.onRestoreInstanceState(savedInstanceState)
    }

    private fun openDirectory() {
        Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
            startActivityForResult(this, OPEN_DOCUMENT_TREE_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == OPEN_DOCUMENT_TREE_CODE && resultCode == Activity.RESULT_OK) {
            getPictures(data?.data ?: return)
        }
    }

    private fun getPictures(directoryUri: Uri) {
        val documentsTree = DocumentFile.fromTreeUri(this, directoryUri) ?: return
        val documents = documentsTree.listFiles()

        imageUriList = documents.filter {
            it.type == "image/jpeg" || it.type == "image/png" || it.type == "image/jpg"
        }.map { it.uri }

        adapter.setImages(imageUriList)
    }

    override fun onItemClick(uri: Uri) {
        Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "image/*")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(this)
        }
    }

    override fun onItemLongClick(uri: Uri) {
        val view = layoutInflater.inflate(R.layout.image_preview, null)
        dialog = Dialog(this, R.style.CustomDialog)
        dialog.setContentView(view)
        Picasso.get()
            .load(uri)
            .fit()
            .centerCrop()
            .into(dialog.imagePreview)
        dialog.show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putStringArrayList(
            KEY_IMAGE_URI_LIST,
            imageUriList.map { it.toString() } as ArrayList)
        super.onSaveInstanceState(outState)
    }

    companion object {
        private const val NUMBER_OF_ROWS_HORIZONTAL = 2
        private const val NUMBER_OF_ROWS_PORTRAIT = 4
        private const val OPEN_DOCUMENT_TREE_CODE = 0
        private const val KEY_IMAGE_URI_LIST = "key_image_uri_list"
    }
}

