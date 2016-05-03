package com.amerbauer.seachbox.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.MenuRes;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.amerbauer.seachbox.R;

import java.util.ArrayList;
import java.util.List;

public class SearchBox extends RelativeLayout {

	private TextView defaultTextTextView;
	private BackKeyEditText searchEditText;
	private ListView suggestionsListView;
	private View suggestionsListDivider;
	private ImageView overflowIcon;
	private ImageView icon;
	private ImageView backIcon;
	private String defaultText;
	private ProgressBar progressBar;

	private SearchListener listener;
	private SuggestionsAdapter suggestionsAdapter;
	private ArrayList<SuggestionItem> initialSuggestions;
	private boolean searchOpen;
	private boolean triggerListener;

	public SearchBox(Context context) {
		this(context, null);
	}

	public SearchBox(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SearchBox(final Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		inflate(context, R.layout.searchbox, this);

		searchOpen = false;
		defaultTextTextView = (TextView) findViewById(R.id.searchbox_default_text);
		searchEditText = (BackKeyEditText) findViewById(R.id.searchbox_edittext);
		suggestionsListView = (ListView) findViewById(R.id.searchbox_suggestions_list);
		suggestionsListDivider = findViewById(R.id.searchbox_suggestions_list_divider);
		progressBar = (ProgressBar) findViewById(R.id.searchbox_progressbar);
		overflowIcon = (ImageView) findViewById(R.id.searchbox_overflow_icon);
		backIcon = (ImageView) findViewById(R.id.searchbox_back);
		icon = (ImageView) findViewById(R.id.searchbox_icon);

		defaultTextTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				toggleSearch();
			}

		});

		setupSearchEditText();
		defaultText = "";

		suggestionsAdapter = new SuggestionsAdapter();
		suggestionsListView.setAdapter(suggestionsAdapter);

		setBackButtonVisible(false);
	}

	private void setupSearchEditText() {
		searchEditText.setOnEditorActionListener(new OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId,
			                              KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					search(getSearchTerm());
					return true;
				}
				return false;
			}
		});

		searchEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus && searchEditText.getText().length() > 0) {
					searchEditText.selectAll();
				}
			}
		});

		searchEditText.setBackKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (isSearchOpen()) {
					hideSuggestions();
				}
				return true;
			}
		});

		searchEditText.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER) {
					if (TextUtils.isEmpty(getSearchTerm())) {
						toggleSearch();
					} else {
						search(getSearchTerm());
					}
					return true;
				}
				return false;
			}
		});

		searchEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				if (triggerListener) {
					if (listener != null) {
						listener.onSearchTermChanged(s.toString());
					}
				}
				triggerListener = true;

				if (TextUtils.isEmpty(s) && initialSuggestions != null) {
					setInitialSuggestions();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
			                              int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
			                          int count) {

			}
		});
	}

	private void clearSuggestions() {
		suggestionsAdapter.clear();
	}

	/***
	 * Toggle the searchbox's open/closed state manually
	 */
	public void toggleSearch() {
		if (searchOpen) {
			if (TextUtils.isEmpty(getSearchTerm())) {
				setDefaultTextInternal(defaultText);
			}
			closeSearch();
		} else {
			openSearch();
		}
	}

	public void hideSuggestions() {
		if (TextUtils.isEmpty(getSearchTerm())) {
			searchEditText.setVisibility(View.GONE);
			defaultTextTextView.setVisibility(View.VISIBLE);
		}
		setSuggestionsListVisibility(false);
		clearSuggestions();
	}

	private void setSuggestionsListVisibility(boolean visible) {
		suggestionsListView.setVisibility(visible ? View.VISIBLE : View.GONE);
		suggestionsListDivider.setVisibility(visible ? View.VISIBLE : View.GONE);
	}

	public boolean isSearchOpen() {
		return searchOpen;
	}

	public void setOverflowMenu(@MenuRes int overflowMenuResId) {
		overflowIcon.setVisibility(VISIBLE);
		final PopupMenu popupMenu = new PopupMenu(getContext(), overflowIcon);
		popupMenu.getMenuInflater().inflate(overflowMenuResId, popupMenu.getMenu());

		overflowIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				popupMenu.show();
			}
		});
	}

	/***
	 * Set whether to show the progress bar spinner
	 *
	 * @param isLoading Whether to show
	 */
	public void setLoading(boolean isLoading) {
		if (isLoading) {
			progressBar.setVisibility(View.VISIBLE);
		} else {
			progressBar.setVisibility(View.INVISIBLE);
		}
	}

	/***
	 * Set the suggestionsListView that are shown (up to 5) when the searchbox is opened with no text
	 *
	 * @param results Results
	 */
	public void setInitialSuggestions(ArrayList<SuggestionItem> results) {
		this.initialSuggestions = results;
	}

	/***
	 * Set the search listener
	 *
	 * @param listener SearchListener
	 */
	public void setSearchListener(SearchListener listener) {
		this.listener = listener;
	}

	/***
	 * Set the maximum length of the searchbox's edittext
	 *
	 * @param length Length
	 */
	public void setMaxLength(int length) {
		searchEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(
				length)});
	}

	/***
	 * Set the text of the defaultTextTextView (default text when closed)
	 *
	 * @param text Text
	 */
	public void setDefaultText(String text) {
		this.defaultText = text;
		setDefaultTextInternal(text);
	}

	/***
	 * Set the text color of the defaultTextTextView
	 *
	 * @param color
	 */
	public void setLogoTextColor(int color) {
		defaultTextTextView.setTextColor(color);
	}

	/***
	 * Set the image drawable of the drawer icon defaultTextTextView (do not set if you have not hidden the menu icon)
	 *
	 * @param icon Icon
	 */
	public void setIcon(Drawable icon) {
		this.icon.setImageDrawable(icon);
	}

	public void setBackButtonVisible(final boolean visible) {
		backIcon.setImageResource(visible ? R.drawable.searchback_back : R.drawable.searchback_search);
	}

	public void setOnBackButtonClickListener(final OnClickListener onBackButtonClickListener) {
		backIcon.setOnClickListener(onBackButtonClickListener);
	}

	public void setDrawerLogo(Integer icon) {
		setIcon(getResources().getDrawable(icon));
	}

	public ImageView getDrawerLogoView() {
		return icon;
	}

	/***
	 * Sets the hint for the Search Field
	 *
	 * @param hint The hint for Search Field
	 */
	public void setHint(String hint) {
		this.searchEditText.setHint(hint);
	}

	/***
	 * Get the searchbox's current text
	 *
	 * @return Text
	 */
	public String getSearchTerm() {
		return searchEditText.getText().toString();
	}

	/***
	 * Set the searchbox's current text manually
	 *
	 * @param term Text
	 */
	public void setSearchTerm(String term, final boolean triggerListener) {
		if (term == null) {
			term = "";
		}
		this.triggerListener = triggerListener;
		searchEditText.setText(term);
	}

	public void clearSearch() {
		updateSearched(null);
	}

	/***
	 * Set the searchable items from a list (replaces any current items)
	 */
	public void setSuggestionItems(final ArrayList<SuggestionItem> suggestionItems) {
		if (suggestionItems != null && !suggestionItems.isEmpty()) {
			suggestionsAdapter.setSuggestions(suggestionItems);
			setSuggestionsListVisibility(true);
		} else {
			hideSuggestions();
		}
	}

	private void search(final SuggestionItem suggestionItem) {
		if (listener != null) {
			listener.onSuggestionClick(suggestionItem);
		}
		updateSearched(suggestionItem.title);
	}

	private void search(final String text) {
		if (listener != null) {
			listener.onSearch(text);
		}
		updateSearched(text);
	}

	private void updateSearched(String text) {
		setSearchTerm(text, false);

		if (!TextUtils.isEmpty(getSearchTerm())) {
			setDefaultTextInternal(text);
		} else {
			setDefaultTextInternal(defaultText);
		}
		closeSearch();
	}

	private void openSearch() {
		searchEditText.setVisibility(View.VISIBLE);
		defaultTextTextView.setVisibility(View.GONE);
		setSuggestionsListVisibility(true);
		searchEditText.requestFocus();

		searchOpen = true;

		if (TextUtils.isEmpty(searchEditText.getText())) {
			setInitialSuggestions();
		}

		if (listener != null) {
			listener.onSearchOpened();
		}

		final InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.toggleSoftInputFromWindow(
				getApplicationWindowToken(),
				InputMethodManager.SHOW_FORCED, 0);
	}

	private void setInitialSuggestions() {
		if (initialSuggestions != null && initialSuggestions.isEmpty()) {
			setSuggestionsListVisibility(false);
			clearSuggestions();
		} else {
			suggestionsAdapter.setSuggestions(initialSuggestions);
			setSuggestionsListVisibility(true);
		}
	}

	private void closeSearch() {
		searchEditText.setVisibility(View.GONE);
		defaultTextTextView.setVisibility(View.VISIBLE);
		setSuggestionsListVisibility(false);

		if (listener != null) {
			listener.onSearchClosed();
		}

		final InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(getApplicationWindowToken(), 0);
		searchOpen = false;
	}

	private void setDefaultTextInternal(String text) {
		defaultTextTextView.setText(text);
	}

	public class SuggestionsAdapter extends BaseAdapter implements OnClickListener {

		List<SuggestionItem> suggestions;

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_search_suggestion, parent, false);
			}

			final SuggestionItem suggestionItem = suggestions.get(position);

			final TextView title = (TextView) convertView.findViewById(R.id.title);
			title.setText(suggestionItem.title);

			final ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
			icon.setImageDrawable(suggestionItem.icon);

			final ImageView up = (ImageView) convertView.findViewById(R.id.up);
			up.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					setSearchTerm(title.getText().toString(), true);
					searchEditText.setSelection(searchEditText.getText().length());
				}

			});

			convertView.setTag(suggestionItem);
			convertView.setOnClickListener(this);
			return convertView;
		}

		public void clear() {
			setSuggestions(null);
		}

		public void setSuggestions(final List<SuggestionItem> suggestions) {
			this.suggestions = suggestions;
			notifyDataSetChanged();
		}

		@Override
		public void onClick(View v) {
			search((SuggestionItem) v.getTag());
		}

		@Override
		public int getCount() {
			return suggestions != null ? suggestions.size() : 0;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
	}

	public interface SearchListener {

		/**
		 * Called when the searchbox is opened
		 */
		void onSearchOpened();

		/**
		 * Called when the clear button is pressed
		 */
		void onSearchCleared();

		/**
		 * Called when the searchbox is closed
		 */
		void onSearchClosed();

		/**
		 * Called when the searchbox's edittext changes
		 */
		void onSearchTermChanged(String term);

		/**
		 * Called when a search happens, with a result
		 *
		 * @param result
		 */
		void onSearch(String result);

		/**
		 * Called when a search result is clicked, with the result
		 *
		 * @param result
		 */
		void onSuggestionClick(SuggestionItem result);
	}

	public static class SuggestionItem {

		public String title;
		public Drawable icon;
		public Object tag;

		public SuggestionItem(String title, Drawable icon) {
			this.title = title;
			this.icon = icon;
		}

		public SuggestionItem(String title, Drawable icon, Object tag) {
			this.title = title;
			this.icon = icon;
			this.tag = tag;
		}

		public SuggestionItem(String title) {
			this.title = title;
		}

		@Override
		public String toString() {
			return title;
		}

	}

}
