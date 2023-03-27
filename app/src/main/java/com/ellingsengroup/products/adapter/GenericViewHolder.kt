package com.ellingsengroup.products.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class GenericViewHolder<T>(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(data: T?)

}