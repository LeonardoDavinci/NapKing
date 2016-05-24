package com.innovation4you.napking.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class ListAdapter<T, VH : BaseViewHolder>(protected val items: List<T>, protected val onClickListener: View.OnClickListener) : RecyclerView.Adapter<VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(
                layoutResId, parent, false)
        return onCreateViewHolder(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        onBindViewHolder(holder, items[position], position)
    }

    protected abstract val layoutResId: Int

    abstract fun onCreateViewHolder(view: View): VH

    abstract fun onBindViewHolder(viewHolder: VH, item: T, position: Int)

    override fun getItemCount(): Int {
        return items.size
    }
}
