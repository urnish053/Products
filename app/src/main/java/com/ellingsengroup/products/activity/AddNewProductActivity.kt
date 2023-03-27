package com.ellingsengroup.products.activity

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.ellingsengroup.products.R
import com.ellingsengroup.products.adapter.SelectedImageAdapter
import com.ellingsengroup.products.databinding.ActivityAddProductBinding
import com.ellingsengroup.products.databinding.DialogSelectImageBinding
import com.ellingsengroup.products.network.Status
import com.ellingsengroup.products.utils.*
import com.ellingsengroup.products.viewmodel.ProductViewModel
import java.io.File


class AddNewProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddProductBinding
    private lateinit var selectedImageAdapter: SelectedImageAdapter
    private var fileList: ArrayList<String> = ArrayList()
    private val productViewModel by viewModels<ProductViewModel>()
    var camera_uri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding()
        setAdapter()

        with(binding) {
            txtAddProduct.setOnClickListener {
                if (fileList.size <= 1) {
                    baseContext.toast(getString(R.string.err_select_image))
                } else if (edtProductTitle.text.toString().isEmpty()) {
                    baseContext.toast(getString(R.string.err_title))
                } else if (edtProductDescription.text.toString().isEmpty()) {
                    baseContext.toast(getString(R.string.err_desc))
                } else if (edtProductPrice.text.toString().isEmpty()) {
                    baseContext.toast(getString(R.string.err_price))
                } else if (edtProductDiscount.text.toString().isEmpty()) {
                    baseContext.toast(getString(R.string.err_discount))
                } else {
                    if (isNetworkAvailable(this@AddNewProductActivity)) {
                        hideSoftKeyboard()
                        productViewModel.addNewProduct(
                            edtProductTitle.text.toString(),
                            edtProductDescription.text.toString(),
                            edtProductPrice.text.toString(),
                            edtProductDiscount.text.toString()
                        ).observe(this@AddNewProductActivity) {
                            it.let { resources ->
                                when (resources.status) {
                                    Status.LOADING -> {
                                        binding.progressBar.show()
                                    }
                                    Status.SUCCESS -> {
                                        binding.progressBar.hide()
                                        baseContext.toast(getString(R.string.product_add_success))
                                        finish()
                                    }
                                    Status.ERROR -> {
                                        Log.e("Error","Errro->"+resources.message)
                                        resources.message?.let { it1 -> toast(it1) }
                                        binding.progressBar.hide()
                                    }
                                }
                            }
                        }
                    } else {

                        toast(getString(R.string.no_internet))
                    }
                }
            }
        }
    }

    private fun viewBinding() {
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    private fun setAdapter() {
        selectedImageAdapter = SelectedImageAdapter(onItemClick = { data: String?, position: Int ->
            if (position == 0) {
                if (fileList.size <= MAX_SELECT_IMAGE) {
                    dialogImageOption()
                } else {
                    toast(getString(R.string.max_image))
                }
            }

        })
        with(binding) {
            txtToolbarTitle.text = getString(R.string.add_product)
            setSupportActionBar(binding.layoutToolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            rvSelectedImage.apply {
                layoutManager = GridLayoutManager(context, 3)
                adapter = selectedImageAdapter
            }
        }

        fileList.add(getString(R.string.app_name))
        selectedImageAdapter.setList(fileList)
        selectedImageAdapter.notifyDataSetChanged()
    }


    private fun dialogImageOption() {
        val bindingDialogImageOption = DialogSelectImageBinding.inflate(layoutInflater, null, false)

        val dialogBuilder = AlertDialog.Builder(this, R.style.CustomAlertDialog)
            .setView(bindingDialogImageOption.root).show()
        dialogBuilder.show()

        with(bindingDialogImageOption) {
            txtSelectCamera.setOnClickListener {
                dialogBuilder.dismiss()
                activityResultLauncher.launch(Manifest.permission.CAMERA)
            }

            txtSelectGallery.setOnClickListener {
                dialogBuilder.dismiss()
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                getResultGallery.launch(intent)

            }
        }
    }

    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                val values = ContentValues()
                values.put(MediaStore.Images.Media.TITLE, "Products")
                values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera")
                camera_uri =
                    contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, camera_uri)
                getResultCamera.launch(cameraIntent)

            } else {
                baseContext.toast(getString(R.string.camera_permission_deny))
            }
        }

    private val getResultCamera =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {

            try {
                if (it.resultCode == RESULT_OK) {
                    val file_camera = File(getPathFromUri(camera_uri, this).toString())
                    fileList.add(file_camera.absolutePath)
                    selectedImageAdapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    private val getResultGallery =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            val image_uri: Uri? = it.data?.data
            val file = File(getPathFromUri(image_uri, this).toString())
            fileList.add(file.absolutePath)
            selectedImageAdapter.notifyDataSetChanged()
        }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }

        return super.onOptionsItemSelected(item)

    }
}