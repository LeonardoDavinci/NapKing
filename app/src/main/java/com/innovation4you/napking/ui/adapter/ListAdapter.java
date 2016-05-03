package com.innovation4you.napking.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public abstract class ListAdapter<T, VH extends BaseViewHolder> extends RecyclerView.Adapter<VH> {

	final protected List<T> items;
	final protected View.OnClickListener onClickListener;

	public ListAdapter(final List<T> items, final View.OnClickListener onClickListener) {
		this.items = items;
		this.onClickListener = onClickListener;
	}

	@Override
	public VH onCreateViewHolder(ViewGroup parent, int viewType) {
		final View view = LayoutInflater.from(parent.getContext()).inflate(
				getLayoutResId(), parent, false);
		return onCreateViewHolder(view);
	}

	@Override
	public void onBindViewHolder(VH holder, int position) {
		onBindViewHolder(holder, items.get(position), position);
	}

	protected abstract int getLayoutResId();

	public abstract VH onCreateViewHolder(final View view);

	public abstract void onBindViewHolder(VH viewHolder, T item, int position);

	@Override
	public int getItemCount() {
		return items.size();
	}
}
