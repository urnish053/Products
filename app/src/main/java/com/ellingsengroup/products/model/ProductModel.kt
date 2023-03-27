package com.ellingsengroup.products.model

data class ProductModel(
    val limit: Int,
    val products: ArrayList<Product>,
    val skip: Int,
    val total: Int,
)