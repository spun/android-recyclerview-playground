package com.spundev.swipeactions

import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.spundev.swipeactions.databinding.SwaActivitySwipeActionsBinding
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SwipeActivity : AppCompatActivity() {

    private lateinit var binding: SwaActivitySwipeActionsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = SwaActivitySwipeActionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prepareRecyclerView()
    }

    private fun prepareRecyclerView() {

        // We use a MutableStateFlow instead of a simple list to emulate the events of an app that
        // uses a database and notifies the adapter when something changes
        val initialList = List(5) { id -> SwipeItem(id, "Item $id") }
        val itemsList: MutableStateFlow<List<SwipeItem>> = MutableStateFlow(initialList)

        // Layout manager
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.swipeActionsRecyclerView.layoutManager = layoutManager

        // Create adapter
        val adapter = SwipeAdapter(this)

        // Set adapter
        binding.swipeActionsRecyclerView.adapter = adapter
        binding.swipeActionsRecyclerView.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        )
        // Add Swipe to delete touch helper
        ItemTouchHelper(
            SwipeCallback(
                context = this,
                adapter = adapter,
                swipeRightColor = ColorDrawable(ContextCompat.getColor(this, R.color.swa_swipe_red)),
                swipeLeftColor =ColorDrawable(ContextCompat.getColor(this, R.color.swa_swipe_blue)),
                swipeRightIcon = ContextCompat.getDrawable(this, R.drawable.ic_delete_28),
                swipeLeftIcon = ContextCompat.getDrawable(this, R.drawable.ic_check_28)
            ) { item, direction ->
                // Toggle isActive
                if ((direction and ItemTouchHelper.LEFT) != 0) {
                    val list = itemsList.value.map { listItem ->
                        if (listItem.id == item?.id) {
                            listItem.copy(isActive = !listItem.isActive)
                        } else {
                            listItem
                        }
                    }.sortedBy { it.id }.sortedBy { !it.isActive }
                    itemsList.value = list
                }
                // Delete
                if ((direction and ItemTouchHelper.RIGHT) != 0) {
                    // We could add a "delete(...)" to the adapter, but I think this is more comparable
                    // with apps that would change the database here
                    val list = itemsList.value.toMutableList().apply { remove(item) }
                    itemsList.value = list
                }

                // We could add a "delete(...)" to the adapter, but I think this is more comparable
                // with apps that would change the database here
                val list = itemsList.value.toMutableList().apply { remove(item) }
                itemsList.value = list
            }
        ).attachToRecyclerView(binding.swipeActionsRecyclerView)

        // Update adapter content
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                itemsList.collect {
                    adapter.submitList(it)
                }
            }
        }
    }

    companion object {
        fun start(context: Context) {
            val starter = Intent(context, SwipeActivity::class.java)
            context.startActivity(starter)
        }
    }
}
