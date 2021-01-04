package com.airstream.typhoon.ui.library

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airstream.typhoon.R
import com.airstream.typhoon.adapter.LibraryCategoryAdapter
import com.airstream.typhoon.adapter.RecyclerListener
import com.airstream.typhoon.data.library.entities.Category
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.uvnode.typhoon.extensions.model.Series
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LibraryManagerFragment : BottomSheetDialogFragment() {

    private val libraryManagerViewModel: LibraryManagerViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        dialog.setOnShowListener { dialog ->
            val d = dialog as BottomSheetDialog
            val bottomSheet =
                d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
            BottomSheetBehavior.from(bottomSheet!!).state =
                BottomSheetBehavior.STATE_EXPANDED

            BottomSheetBehavior.from(bottomSheet!!).peekHeight = 0
        }

        // Do something with your dialog like setContentView() or whatever

        // Do something with your dialog like setContentView() or whatever
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_library_manager, container, false)

        val categoriesList: RecyclerView = root.findViewById(R.id.listview_categories)
        categoriesList.layoutManager = LinearLayoutManager(requireActivity())

        val bundle = requireArguments()
        val mode = bundle.getInt("mode")

        val categoryAdapter = LibraryCategoryAdapter(requireActivity(), mode == 1)

        if (mode == 1) {
            val series = bundle.getParcelable<Series>("series")!!

            Log.d(TAG, "onCreateView: ${series.source} ${series.id}")

            categoryAdapter.onCheckChangedListener = object : RecyclerListener.OnCheckChangedListener {
                override fun onCheckChanged(categoryId: Int, isChecked: Boolean) {
                    if (isChecked) {
                        CoroutineScope(Dispatchers.Default).launch {
                            libraryManagerViewModel.addSeriesToCategory(categoryId, series)
                        }
                    } else {
                        CoroutineScope(Dispatchers.Default).launch {
                            libraryManagerViewModel.removeSeriesFromCategory(
                                categoryId,
                                series.source,
                                series.id
                            )
                        }
                    }
                }
            }

            libraryManagerViewModel.getCategories(series.source, series.id).observe(
                viewLifecycleOwner,
                {
//                Log.d(TAG, "onCreateView: $it")
                    categoryAdapter.setList(it)
                })
        }

        categoryAdapter.onEditListener = object : LibraryCategoryAdapter.OnEditCategoryListener {
            override fun onEdit(category: Category) {
                showListManageDialog(category)
            }
        }

        categoriesList.adapter = categoryAdapter

        val newCategoryButton: Button = root.findViewById(R.id.button_add_category)
        newCategoryButton.setOnClickListener {
            showListNameDialog()
        }

        if (mode == 0) {
            libraryManagerViewModel.getCategories().observe(viewLifecycleOwner, {
                categoryAdapter.setList(it)
            })
        }

        return root
    }

    private fun showListManageDialog(category: Category) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(R.string.dialog_library_list_rename)

        val input = EditText(activity)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        input.setText(category.name)

        builder.setPositiveButton(R.string.dialog_ok,
            DialogInterface.OnClickListener { _, _ ->
                val name = input.text.toString()
                if ("" != name) {

                    CoroutineScope(Dispatchers.Default).launch {
                        libraryManagerViewModel.renameCategory(category.id, name)

                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                activity,
                                R.string.library_list_renamed,
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }

                } else {
                    Toast.makeText(
                        activity,
                        R.string.dialog_library_list_field_blank,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

        builder.setNeutralButton("Delete", DialogInterface.OnClickListener { dialogInterface, _ ->
            CoroutineScope(Dispatchers.Default).launch {
                libraryManagerViewModel.delete(category)

                withContext(Dispatchers.Main) {
                    dialogInterface.cancel()
                }
            }

        })

        builder.setNegativeButton(R.string.dialog_cancel,
            DialogInterface.OnClickListener { dialog, _ -> dialog.cancel() })

        builder.show()

    }

    private fun showListNameDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(R.string.dialog_library_list_new_name)
        val input = EditText(activity)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)
        builder.setPositiveButton(R.string.dialog_ok,
            DialogInterface.OnClickListener { _, _ ->
                val name = input.text.toString()
                if ("" != name) {
                    CoroutineScope(Dispatchers.Default).launch {
                        libraryManagerViewModel.addCategory(name)
                    }
//                    sendRefreshEvent()
                    Toast.makeText(activity, R.string.dialog_library_list_added, Toast.LENGTH_SHORT)
                        .show()
//                    if (listsAdapter.getCount() > 0) {
//                        noListsTextView.setVisibility(TextView.GONE)
//                    }
                } else {
                    Toast.makeText(
                        activity,
                        R.string.dialog_library_list_field_blank,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        builder.setNegativeButton("Cancel",
            DialogInterface.OnClickListener { dialog, _ -> dialog.cancel() })
        builder.show()
    }


    companion object {
        private const val TAG = "LibraryManagerFragment"
    }

}