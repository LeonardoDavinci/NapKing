package com.innovation4you.napking.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;

public class BaseViewHolder extends RecyclerView.ViewHolder {

	public BaseViewHolder(final View itemView, final View.OnClickListener onClickListener) {
		super(itemView);
		ButterKnife.bind(this, itemView);
		itemView.setOnClickListener(onClickListener);
	}
}