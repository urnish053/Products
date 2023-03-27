package com.ellingsengroup.products.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ellingsengroup.products.R
import com.ellingsengroup.products.databinding.RowProductsBinding
import com.ellingsengroup.products.databinding.RowProductsVerticalBinding
import com.ellingsengroup.products.model.Product
import com.ellingsengroup.products.utils.inflater
import com.ellingsengroup.products.utils.itemType
import com.ellingsengroup.products.utils.loadImageFromURL

class ProductListAdapter() :
    RecyclerView.Adapter<GenericViewHolder<*>>() {

        private var listData: ArrayList<Product>? = null

    fun setList(listData: ArrayList<Product>) {
        this.listData = listData
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder<*> {

        val view = when (viewType) {
            1 -> DefaultViewHolder(
                RowProductsBinding.inflate(
                    parent.context.inflater,
                    parent,
                    false
                )
            )

            2 -> VerticalViewHolder(
                RowProductsVerticalBinding.inflate(
                    parent.context.inflater,
                    parent,
                    false
                )
            )
            else -> {
                DefaultViewHolder(
                    RowProductsBinding.inflate(
                        parent.context.inflater,
                        parent,
                        false
                    )
                )

            }
        }

        return view
    }

    override fun getItemCount(): Int {
        if (listData == null)
            return 0
        else
            return listData?.size!!
    }

    override fun getItemViewType(position: Int): Int = itemType

    override fun onBindViewHolder(holder: GenericViewHolder<*>, position: Int) {
        when (holder.itemViewType) {
            1 -> {
                (holder as DefaultViewHolder).bind(listData?.get(position))
            }
            2 -> {
                (holder as VerticalViewHolder).bind(listData?.get(position))
            }
            else ->  {
                (holder as DefaultViewHolder).bind(listData?.get(position))
            }

        }

    }

    inner class DefaultViewHolder(val view: RowProductsBinding) :
        GenericViewHolder<Product>(view.root) {

        override fun bind(data: Product?) {

            with(view) {
                txtItemTitle.text = data?.title
                txtItemDescription.text = data?.description
                txtItemPrice.text = "$" + data?.price.toString()
                txtItemDiscount.text = data?.discountPercentage.toString() + "%"
                txtItemRating.text = data?.rating.toString()
                imgItemImage.loadImageFromURL(data?.images?.get(0),R.mipmap.ic_launcher)

            }
        }
    }

    inner class VerticalViewHolder(val view: RowProductsVerticalBinding) :
        GenericViewHolder<Product>(view.root) {

        override fun bind(data: Product?) {

            with(view) {
                txtItemTitle.text = data?.title
                txtItemPrice.text = "$" + data?.price.toString()
                txtItemRating.text = data?.rating.toString()
                imgItemImage.loadImageFromURL(data?.images?.get(0), R.mipmap.ic_launcher)

            }
        }
    }


}