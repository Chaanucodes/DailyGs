package com.chan.dailygoals.tasks

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.SeekBar
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.DialogFragment
import com.chan.dailygoals.R
import kotlinx.android.synthetic.main.add_new_task_fragment.*
import kotlinx.android.synthetic.main.add_new_task_fragment.view.*


class AddNewTaskDialogFragment(
    val closeKeyboard : (view : View)-> Unit = fun(_: View){}
) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v =inflater.inflate(R.layout.add_new_task_fragment, container, false)

        v.seek_progress_new_task_dialog.setOnSeekBarChangeListener(object  : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                v.text_live_progress_new_task_dialog.setText("$p1%")
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        v.cancel_button_new_task_done.setOnClickListener {
            closeKeyboard(v)
            this.dismiss()
        }
        v.done_button_new_task_dialog.setOnClickListener {
            if (v.edit_name_new_task_dialog.text.isNotEmpty()){
                closeKeyboard(v)
                LoadingBarCallback.isLoading.value = true
                DialogFragmentDataCallback.addTempData(
                    v.edit_name_new_task_dialog.text.toString().trim().toLowerCase().capitalize(),
                v.seek_progress_new_task_dialog.progress)
                this.dismiss()
            }
        }
        return v
    }


}