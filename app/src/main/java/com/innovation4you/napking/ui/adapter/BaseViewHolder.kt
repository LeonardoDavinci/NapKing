package com.innovation4you.napking.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.View

import butterknife.ButterKnife

open class BaseViewHolder(itemView: View, onClickListener: View.OnClickListener) : RecyclerView.ViewHolder(itemView) {

    init {
        ButterKnife.bind(this, itemView)
        itemView.setOnClickListener(onClickListener)
    }
}