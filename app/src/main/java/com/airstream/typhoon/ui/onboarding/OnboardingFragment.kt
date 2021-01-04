package com.airstream.typhoon.ui.onboarding

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.airstream.typhoon.R


class OnboardingFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())

        val inflater = LayoutInflater.from(context)
        val newFileView: View = inflater.inflate(R.layout.fragment_onboarding, null)
        builder.setView(newFileView)

        builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, _ ->
            dialog.dismiss()
        })

        return builder.create()
    }
}