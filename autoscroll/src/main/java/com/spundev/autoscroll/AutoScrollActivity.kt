package com.spundev.autoscroll

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.spundev.autoscroll.adapter.AutoScrollAdapter
import com.spundev.autoscroll.databinding.AscAutoScrollActivityBinding
import com.spundev.autoscroll.model.AutoScrollItem
import com.spundev.autoscroll.scrollObservers.MyScrollToBottomObserver
import com.spundev.autoscroll.scrollObservers.MyScrollToTopObserver
import com.spundev.autoscroll.scrollObservers.MyScrollToTopPlusObserver

class AutoScrollActivity : AppCompatActivity() {

    private lateinit var binding: AscAutoScrollActivityBinding

    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var adapter: AutoScrollAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = AscAutoScrollActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prepareRecyclerView()

        // Screen controls
        radioGroupListener()
    }

    private fun prepareRecyclerView() {

        // Layout manager
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.autoScrollRecyclerView.layoutManager = layoutManager

        // Create adapter
        adapter = AutoScrollAdapter(this,
            onClick = { item ->
                val newList = adapter.currentList.map { listItem ->
                    if (listItem.id == item.id) {
                        listItem.copy(isActive = !listItem.isActive)
                    } else {
                        listItem
                    }
                }.sortedBy { it.id }.sortedBy { !it.isActive }
                adapter.submitList(newList)
            })

        // Set adapter
        binding.autoScrollRecyclerView.adapter = adapter
        binding.autoScrollRecyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )

        // Update adapter content
        // Create a list of 5 items with ids from 100 to 104
        val itemsList = List(5) {
            val id = it + 100
            AutoScrollItem(id, "Item $id", false)
        }
        adapter.submitList(itemsList)
    }

    private fun radioGroupListener() {
        // Prepare all possible AdapterDataObserver
        var currentObserver: RecyclerView.AdapterDataObserver? = null
        val topScrollObserver = MyScrollToTopObserver(binding.autoScrollRecyclerView, layoutManager)
        val bottomScrollObserver =
            MyScrollToBottomObserver(binding.autoScrollRecyclerView, layoutManager, adapter)
        val topPlusScrollObserver =
            MyScrollToTopPlusObserver(binding.autoScrollRecyclerView, layoutManager)

        // Start listening for changes
        binding.autoScrollRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.none_radio_button -> {
                    // Unregister old AdapterDataObserver
                    currentObserver?.let {
                        adapter.unregisterAdapterDataObserver(it)
                    }
                    currentObserver = null
                }
                R.id.top_radio_button -> {
                    // Unregister old AdapterDataObserver
                    currentObserver?.let {
                        adapter.unregisterAdapterDataObserver(it)
                    }
                    // Register MyScrollToTopObserver
                    currentObserver = topScrollObserver
                    adapter.registerAdapterDataObserver(topScrollObserver)

                }
                R.id.bottom_radio_button -> {
                    // Unregister old AdapterDataObserver
                    currentObserver?.let {
                        adapter.unregisterAdapterDataObserver(it)
                    }
                    // Register MyScrollToBottomObserver
                    currentObserver = bottomScrollObserver
                    adapter.registerAdapterDataObserver(bottomScrollObserver)
                }
                R.id.top_plus_radio_button -> {
                // Unregister old AdapterDataObserver
                currentObserver?.let {
                    adapter.unregisterAdapterDataObserver(it)
                }
                // Register MyScrollToTopPlusObserver
                currentObserver = topPlusScrollObserver
                adapter.registerAdapterDataObserver(topPlusScrollObserver)
            }
            }
        }
    }

    private fun addToTop() {
        val minId = adapter.currentList.minByOrNull { it.id }?.id
        minId?.minus(1)?.let { newItemId ->
            val newList = adapter.currentList.toMutableList()
            newList.add(0, AutoScrollItem(newItemId, "Item $newItemId", false))
            val sortedList = newList.sortedBy { it.id }.sortedBy { !it.isActive }
            adapter.submitList(sortedList)
        }
    }

    private fun addToBottom() {
        val maxId = adapter.currentList.maxByOrNull { it.id }?.id
        maxId?.plus(1)?.let { newItemId ->
            val newList = adapter.currentList.toMutableList()
            newList.add(
                adapter.currentList.size,
                AutoScrollItem(newItemId, "Item $newItemId", false)
            )
            val sortedList = newList.sortedBy { it.id }.sortedBy { !it.isActive }
            adapter.submitList(sortedList)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.asc_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.action_add_to_top -> {
                addToTop()
                true
            }
            R.id.action_add_to_bottom -> {
                addToBottom()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        fun start(context: Context) {
            val starter = Intent(context, AutoScrollActivity::class.java)
            context.startActivity(starter)
        }
    }
}
