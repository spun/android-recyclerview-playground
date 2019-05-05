package com.spundev.activitysharedelements

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.spundev.activitysharedelements.DetailActivity.Companion.SELECTED_ITEM_POSITION
import com.spundev.activitysharedelements.adapter.GridAdapter
import com.spundev.commonresources.data.ResourcesData


class GridActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    private var selectedItem: Int = RecyclerView.NO_POSITION

    companion object {
        fun start(context: Context) {
            val starter = Intent(context, GridActivity::class.java)
            context.startActivity(starter)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ase_activity_grid)

        recyclerView = findViewById(R.id.recyclerview)
        val adapter = GridAdapter(this) { id, view ->
            DetailActivity.start(this, id, view, ResourcesData.instance[id].transitionName)
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(this, 3)


        adapter.submitList(ResourcesData.instance)
    }

    override fun onActivityReenter(requestCode: Int, data: Intent) {

        supportPostponeEnterTransition()

        // Retrieve position of the item
        selectedItem = data.getIntExtra(SELECTED_ITEM_POSITION, RecyclerView.NO_POSITION)
        if (selectedItem != RecyclerView.NO_POSITION) {
            scrollToSelectedItem()
        }
    }

    private val layoutChangeListener = object : View.OnLayoutChangeListener {
        override fun onLayoutChange(v: View?, l: Int, t: Int, r: Int, b: Int, oL: Int, oT: Int, oR: Int, oB: Int) {
            // Get RecyclerView layoutManager
            recyclerView.layoutManager?.let { layoutManager ->
                // Check if the viewAtPosition is not null
                layoutManager.findViewByPosition(selectedItem)?.let { viewAtPosition ->
                    // Check if the view is completely visible
                    if (layoutManager.isViewPartiallyVisible(viewAtPosition, true, true)) {
                        // If viewAtPosition exists and is fully visible, continue postponed transition
                        // and remove listener
                        recyclerView.removeOnLayoutChangeListener(this)
                        supportStartPostponedEnterTransition()
                    }
                }
            }
        }
    }

    private fun scrollToSelectedItem() {
        // Add change listener to the RecyclerView
        recyclerView.addOnLayoutChangeListener(layoutChangeListener)

        // Get RecyclerView layoutManager and check if null
        recyclerView.layoutManager?.let { layoutManager ->
            // Check if view exists and if is partial or fully visible
            val viewAtPosition = layoutManager.findViewByPosition(selectedItem)
            if (viewAtPosition != null && layoutManager.isViewPartiallyVisible(viewAtPosition, true, true)) {
                // If viewAtPosition exists and is fully visible, continue postponed transition
                // and remove listener
                recyclerView.removeOnLayoutChangeListener(layoutChangeListener)
                supportStartPostponedEnterTransition()
            } else {
                // if view doesn't exists or is not fully visible, scroll to position
                recyclerView.post { layoutManager.scrollToPosition(selectedItem) }
            }
        }
    }

}
