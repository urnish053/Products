package com.ellingsengroup.products.network

import com.ellingsengroup.products.model.Product
import com.ellingsengroup.products.model.ProductModel
import okhttp3.RequestBody
import retrofit2.http.*

interface EndPoints {

    @GET("products")
    suspend fun getProduct(
    ):ProductModel

    @POST("products/add")
    suspend fun addNewProduct(
        @Body request: RequestBody
    ): Product
}