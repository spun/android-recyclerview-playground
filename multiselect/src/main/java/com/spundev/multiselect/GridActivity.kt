package com.spundev.multiselect

import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StableIdKeyProvider
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.spundev.commonresources.data.ResourcesData
import com.spundev.multiselect.adapter.GridAdapter
import com.spundev.multiselect.adapter.MyLookup
import com.spundev.multiselect.adapter.SpacesItemDecoration

class GridActivity : AppCompatActivity() {

    lateinit var selectionTracker: SelectionTracker<Long>

    companion object {

        fun start(context: Context) {
            val starter = Intent(context, GridActivity::class.java)
            context.startActivity(starter)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ms_activity_grid)

        // RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recyclerview)
        val adapter = GridAdapter(this) { _, _ ->
            // Item click event
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        // Spacing between items decorator
        recyclerView.addItemDecoration(SpacesItemDecoration(3, 8))
        // Submit items to adapter
        adapter.submitList(ResourcesData.instance)


        // Create selection tracker
        selectionTracker = SelectionTracker.Builder<Long>(
                "selection1",
                recyclerView,
                StableIdKeyProvider(recyclerView),
                MyLookup(recyclerView),
                StorageStrategy.createLongStorage()
        ).withSelectionPredicate(
                SelectionPredicates.createSelectAnything()
        ).build()

        // retrieve selection tracker state
        if (savedInstanceState != null) {
            selectionTracker.onRestoreInstanceState(savedInstanceState)
        }

        // Listen for selection changes
        selectionTracker.addObserver(
                object : SelectionTracker.SelectionObserver<Long>() {
                    override fun onSelectionChanged() {
                        // Get number of selected items
                        val numSelectedItems: Int = selectionTracker.selection.size()

                        // Change toolbar
                        if (numSelectedItems > 0) {
                            changeToolbarState(
                                    numSelectedItems.toString(),
                                    R.color.ms_selection_colorPrimary,
                                    R.color.ms_selection_colorPrimaryDark, true
                            )
                        } else {
                            changeToolbarState(
                                    getString(R.string.ms_app_name),
                                    R.color.ms_colorPrimary,
                                    R.color.ms_colorPrimaryDark,
                                    false
                            )
                        }
                    }
                })
        // Add selection tracker
        adapter.setTracker(selectionTracker)
    }

    fun changeToolbarState(
            newTitle: String, @ColorRes backgroundColor: Int, @ColorRes statusBarColor: Int,
            showClose: Boolean
    ) {
        // Toolbar title
        title = newTitle
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(false)
            // Toolbar background
            it.setBackgroundDrawable(
                    ColorDrawable(ContextCompat.getColor(this@GridActivity, backgroundColor))
            )
            // Set status bar color
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.statusBarColor = ContextCompat.getColor(this@GridActivity, statusBarColor)
            }
            it.setHomeAsUpIndicator(R.drawable.ms_ic_close_white_24dp)
            it.setDisplayHomeAsUpEnabled(showClose)
        }
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        // Save the user's current selected items
        outState.run {
            selectionTracker.onSaveInstanceState(outState)
        }
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState)
    }

    // Toolbar back button
    override fun onSupportNavigateUp(): Boolean {
        // Check if there is something selected
        return if (selectionTracker.hasSelection()) {
            // Clear selection
            selectionTracker.clearSelection()
            false
        } else {
            super.onSupportNavigateUp()
        }
    }

    override fun onBackPressed() {
        // Check if there is something selected
        if (selectionTracker.hasSelection()) {
            // Clear selection
            selectionTracker.clearSelection()
        } else {
            // behave normally
            super.onBackPressed()
        }
    }
}
