package com.ellingsengroup.products.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ellingsengroup.products.R
import com.ellingsengroup.products.databinding.RowSelectedImageBinding
import com.ellingsengroup.products.utils.inflater
import com.ellingsengroup.products.utils.loadImageFromURL


class SelectedImageAdapter(val onItemClick: ((data: String?, position: Int) -> Unit)) :
    RecyclerView.Adapter<GenericViewHolder<*>>() {

    private var listData: ArrayList<String>? = null

    fun setList(listData: ArrayList<String>?) {

        this.listData = listData
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder<*> {
        return DefaultViewHolder(
            RowSelectedImageBinding.inflate(
                parent.context.inflater,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        if (listData == null)
            return 0
        else
            return listData?.size!!
    }


    override fun onBindViewHolder(holder: GenericViewHolder<*>, position: Int) {
        (holder as DefaultViewHolder).bind(listData?.get(position), position)
    }

    inner class DefaultViewHolder(val view: RowSelectedImageBinding) :
        GenericViewHolder<String>(view.root) {

        fun bind(data: String?, position: Int) {

            with(view) {
                imgItemImage.setOnClickListener {
                    onItemClick.invoke(data, position)
                }
                if (position != 0){
                    imgItemImage.loadImageFromURL(data, R.mipmap.ic_launcher)
                }

            }
        }

        override fun bind(data: String?) {

        }


    }

}