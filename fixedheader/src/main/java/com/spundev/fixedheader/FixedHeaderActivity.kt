package com.spundev.fixedheader

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.spundev.fixedheader.adapter.FixHeaderAdapter
import com.spundev.fixedheader.data.FixHeaderData

class FixedHeaderActivity : AppCompatActivity() {

    companion object {

        fun start(context: Context) {
            val starter = Intent(context, FixedHeaderActivity::class.java)
            context.startActivity(starter)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fix_activity_fixed_header)

        // RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recyclerview)
        val adapter = FixHeaderAdapter(this)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(
            // Date fixed header
            DateHeaderDecoration(
                this,
                FixHeaderData.instance
            )
        )
        // Submit items to adapter
        adapter.submitList(FixHeaderData.instance)
    }
}
