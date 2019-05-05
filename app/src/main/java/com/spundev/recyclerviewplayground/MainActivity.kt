package com.spundev.recyclerviewplayground

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Open Shared element between activities example
        val sharedActivityButton: Button = findViewById(R.id.sharedActivityButton)
        sharedActivityButton.setOnClickListener {
            com.spundev.activitysharedelements.GridActivity.start(this)
        }

        // Open Shared element between fragments example
        val sharedNavigationButton: Button = findViewById(R.id.sharedNavigationButton)
        sharedNavigationButton.setOnClickListener {
            com.spundev.navigationsharedelements.MainActivity.start(this)
        }

        // Open Multiple item selection example
        val multiSelectButton: Button = findViewById(R.id.multiSelectButton)
        multiSelectButton.setOnClickListener {
            com.spundev.multiselect.GridActivity.start(this)
        }
    }

    /* ----------------------------------------------------------- */

    // CREDITS
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.credits -> {
                showCreditsDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showCreditsDialog() {
        // Content text view
        val textView = TextView(this)
        textView.setText(R.string.credits_dialog_message)
        textView.movementMethod = LinkMovementMethod.getInstance() // this is important to make the links clickable

        // Add margin to the TextView (https://stackoverflow.com/a/27776276)
        val container = FrameLayout(this)
        val params = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.topMargin = resources.getDimension(R.dimen.dialog_margin).toInt()
        params.leftMargin = resources.getDimension(R.dimen.dialog_margin).toInt()
        params.rightMargin = resources.getDimension(R.dimen.dialog_margin).toInt()
        textView.layoutParams = params
        container.addView(textView)

        // Show dialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.credits_dialog_title))
        builder.setView(container)

        val positiveText = getString(android.R.string.ok)
        builder.setPositiveButton(positiveText) { _, _ ->
            // positive button logic
        }

        val dialog = builder.create()
        // display dialog
        dialog.show()
    }
}
