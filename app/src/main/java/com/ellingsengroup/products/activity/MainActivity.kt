package com.ellingsengroup.products.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.ellingsengroup.products.R
import com.ellingsengroup.products.adapter.ProductListAdapter
import com.ellingsengroup.products.databinding.ActivityMainBinding
import com.ellingsengroup.products.model.Product
import com.ellingsengroup.products.network.Status
import com.ellingsengroup.products.utils.*
import com.ellingsengroup.products.viewmodel.ProductViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var productListAdapter: ProductListAdapter
    private val productViewModel by viewModels<ProductViewModel>()
    val productList = ArrayList<Product>()
    var viewStatus = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding()
        setAdapter()

        binding.fabAddNewproduct.setOnClickListener {
            val intent = Intent(this, AddNewProductActivity::class.java)
            startActivity(intent)
        }

         if (isNetworkAvailable(this)) {
             getProductList()
         } else {
             with(binding){
                progressBar.hide()
                txtError.show()
             }
         }

    }

    private fun viewBinding() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    private fun setAdapter() {
        productListAdapter = ProductListAdapter()

        with(binding) {

            rvProducts.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = productListAdapter
            }

            imgSelectView.setOnClickListener {
                if (productList.size > 0){
                    if (viewStatus) {
                        manageView(viewStatus)
                        rvProducts.apply {
                            layoutManager = GridLayoutManager(context, 2)
                            adapter = productListAdapter
                        }
                        productListAdapter.notifyDataSetChanged()
                    } else {
                        manageView(viewStatus)
                        rvProducts.apply {
                            layoutManager = LinearLayoutManager(context)
                            adapter = productListAdapter
                        }
                        productListAdapter.notifyDataSetChanged()
                    }
                }else{
                    baseContext.toast(getString(R.string.error_no_record))
                }
            }
        }
    }

    private fun getProductList() {
        productViewModel.getProductList().observe(this) {
            it.let { resources ->
                when (resources.status) {
                    Status.LOADING -> {
                        showProgressBar(true)

                    }
                    Status.SUCCESS -> {
                        if (it.data?.products != null && it.data.products.size > 0) {
                            it.data.products.map {
                                productList.add(
                                    Product(
                                        brand = it.brand,
                                        category = it.category,
                                        description = it.description,
                                        discountPercentage = it.discountPercentage,
                                        id = it.id,
                                        images =it.images,
                                        price = it.price,
                                        rating = it.rating,
                                        stock = it.stock,
                                        thumbnail = it.thumbnail,
                                        title = it.title,
                                    )
                                )
                            }
                            showProgressBar(false)
                            productListAdapter.setList(productList)
                            productListAdapter.notifyDataSetChanged()
                        }

                    }
                    Status.ERROR -> {
                        resources.message?.let { it1 -> toast(it1) }
                        showProgressBar(false)
                    }
                }
            }
        }
    }

    private fun showProgressBar(status: Boolean){
        if (status){
            with(binding){
                progressBar.show()
                rvProducts.hide()
            }
        }else{
            with(binding){
                progressBar.hide()
                rvProducts.show()
            }
        }
    }


    private fun manageView(status: Boolean) {
        if (status) {
            viewStatus = false
            itemType = 2
            binding.imgSelectView.setImageDrawable(
                AppCompatResources.getDrawable(
                    this,
                    R.drawable.ic_vertical
                )
            )
        } else {
            viewStatus = true
            itemType = 1
            binding.imgSelectView.setImageDrawable(
                AppCompatResources.getDrawable(
                    this,
                    R.drawable.ic_default
                )
            )
        }

    }

}