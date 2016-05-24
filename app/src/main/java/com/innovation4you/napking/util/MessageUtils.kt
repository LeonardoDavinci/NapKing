package com.innovation4you.napking.util

import android.app.Activity
import android.content.Context
import android.support.annotation.StringRes
import android.view.View
import android.widget.Toast

import com.innovation4you.napking.R
import com.nispok.snackbar.Snackbar
import com.nispok.snackbar.SnackbarManager
import com.nispok.snackbar.enums.SnackbarType
import com.nispok.snackbar.listeners.ActionClickListener

object MessageUtils {

    fun showSuccessMessage(activity: Activity, @StringRes textResId: Int) {
        showSuccessMessage(activity, activity.getText(textResId).toString())
    }

    fun showSuccessMessage(activity: Activity?, text: String) {
        if (activity == null) {
            return
        }
        SnackbarManager.show(
                Snackbar.with(activity).text(text).type(SnackbarType.MULTI_LINE).duration(Snackbar.SnackbarDuration.LENGTH_SHORT), activity)
    }

    fun showErrorMessage(activity: Activity?, @StringRes textResId: Int) {
        if (activity == null) {
            return
        }
        SnackbarManager.show(
                Snackbar.with(activity).type(SnackbarType.MULTI_LINE).text(textResId).duration(Snackbar.SnackbarDuration.LENGTH_SHORT), activity)
    }

    @JvmOverloads fun showErrorMessageWithRetry(activity: Activity?, @StringRes textResId: Int, infinite: Boolean = false,
                                                onRetryClickListener: ActionClickListener) {
        if (activity == null) {
            return
        }
        SnackbarManager.show(
                Snackbar.with(activity).text(textResId).type(SnackbarType.MULTI_LINE).actionLabel(R.string.retry).actionColorResource(R.color.accent).dismissOnActionClicked(true).duration(if (infinite) Snackbar.SnackbarDuration.LENGTH_INDEFINITE else Snackbar.SnackbarDuration.LENGTH_LONG).actionListener(onRetryClickListener), activity)
    }

    fun cancelMessages() {
        SnackbarManager.dismiss()
    }

    fun showToastMessage(context: Context, @StringRes textResId: Int) {
        Toast.makeText(context, textResId, Toast.LENGTH_SHORT).show()
    }

    fun addTooltipMessage(anchorView: View, @StringRes textResId: Int) {
        CheatSheet.setup(anchorView, textResId)
    }

    fun addTooltipMessage(anchorView: View, text: String) {
        CheatSheet.setup(anchorView, text)
    }

    fun removeTooltipMessage(anchorView: View) {
        CheatSheet.remove(anchorView)
    }
}
