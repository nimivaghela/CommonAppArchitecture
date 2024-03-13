package com.app.structure.shareddata.base

import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

open class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    internal var binding: ViewDataBinding? = null

    init {
        setBinding(itemView)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : ViewDataBinding> getBinding(): T {
        return binding as T
    }

    private fun setBinding(itemView: View) {
        binding = DataBindingUtil.bind(itemView)
    }
}