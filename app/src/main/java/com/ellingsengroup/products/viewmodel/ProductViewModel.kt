package com.ellingsengroup.products.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.ellingsengroup.products.network.RequestBuilder
import com.ellingsengroup.products.network.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class ProductViewModel : ViewModel() {

    fun getProductList() = liveData(Dispatchers.IO) {

        emit(Response.loading(null))
        try {
            val data = RequestBuilder.apiRequest.getProduct()
            emit(Response.success(data = data))
        } catch (exception: Exception) {
            emit(Response.error(data = null, message = exception.localizedMessage.toString()))
        }
    }

    fun addNewProduct(
        title: String,
        description: String,
        price: String,
        discountPercentage: String
    ) = liveData(Dispatchers.IO) {
        emit(Response.loading(null))
        try {
            val map = mutableMapOf(
                "title" to title,
                "description" to description,
                "price" to price.toInt(),
                "discountPercentage" to discountPercentage.toDouble(),
                "rating" to 3,
                "stock" to 30,
                "brand" to "Apple",
                "category" to "SmartPhone",
                "thumbnail" to "https://i.dummyjson.com/data/products/1/thumbnail.jpg"
               )

            val result = withContext(Dispatchers.IO) {
                RequestBuilder.apiRequest.addNewProduct(
                    RequestBuilder.createJsonRequestBodyAny(map))
            }
            emit(Response.success(data = result))

        } catch (exception: Exception) {
            emit(Response.error(data = null, message = exception.message.toString()))
        }
    }

}