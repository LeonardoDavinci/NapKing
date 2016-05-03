package com.innovation4you.napking.util;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.Toast;

import com.innovation4you.napking.R;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.nispok.snackbar.listeners.ActionClickListener;

public class MessageUtils {

	public static void showSuccessMessage(final Activity activity, @StringRes final int textResId) {
		showSuccessMessage(activity, activity.getText(textResId).toString());
	}

	public static void showSuccessMessage(final Activity activity, final String text) {
		if (activity == null) {
			return;
		}
		SnackbarManager.show(
				Snackbar.with(activity)
						.text(text)
						.type(SnackbarType.MULTI_LINE)
						.duration(Snackbar.SnackbarDuration.LENGTH_SHORT)
				, activity);
	}

	public static void showErrorMessage(final Activity activity, @StringRes final int textResId) {
		if (activity == null) {
			return;
		}
		SnackbarManager.show(
				Snackbar.with(activity)
						.type(SnackbarType.MULTI_LINE)
						.text(textResId)
						.duration(Snackbar.SnackbarDuration.LENGTH_SHORT)
				, activity);
	}

	public static void showErrorMessageWithRetry(final Activity activity, @StringRes final int textResId, final ActionClickListener
			onRetryClickListener) {
		showErrorMessageWithRetry(activity, textResId, onRetryClickListener, true);
	}

	public static void showErrorMessageWithRetry(final Activity activity, @StringRes final int textResId, final ActionClickListener
			onRetryClickListener, final boolean infinite) {
		if (activity == null) {
			return;
		}
		SnackbarManager.show(
				Snackbar.with(activity)
						.text(textResId)
						.type(SnackbarType.MULTI_LINE)
						.actionLabel(R.string.retry)
						.actionColorResource(R.color.accent)
						.dismissOnActionClicked(true)
						.duration(infinite ? Snackbar.SnackbarDuration.LENGTH_INDEFINITE : Snackbar.SnackbarDuration.LENGTH_LONG)
						.actionListener(onRetryClickListener)
				, activity);
	}

	public static void cancelMessages() {
		SnackbarManager.dismiss();
	}

	public static void showToastMessage(final Context context, @StringRes final int textResId) {
		Toast.makeText(context, textResId, Toast.LENGTH_SHORT).show();
	}

	public static void addTooltipMessage(final View anchorView, @StringRes final int textResId) {
		CheatSheet.setup(anchorView, textResId);
	}

	public static void addTooltipMessage(final View anchorView, final String text) {
		CheatSheet.setup(anchorView, text);
	}

	public static void removeTooltipMessage(final View anchorView) {
		CheatSheet.remove(anchorView);
	}
}
