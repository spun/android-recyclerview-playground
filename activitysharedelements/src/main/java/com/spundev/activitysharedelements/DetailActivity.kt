package com.spundev.activitysharedelements

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import com.spundev.commonresources.data.ResourcesData


class DetailActivity : AppCompatActivity() {

    private var itemId: Int = -1

    companion object {
        const val SELECTED_ITEM_POSITION = "selected"
        private const val EXTRA_ITEM_ID: String = "EXTRA_ITEM_ID"

        fun start(activity: Activity, itemId: Int, sharedElement: View, sharedElementName: String) {
            // Intent
            val starter = Intent(activity, DetailActivity::class.java)
            starter.putExtra(EXTRA_ITEM_ID, itemId)

            // Shared element pair
            val imagePair = Pair.create(sharedElement, sharedElementName)

            // Prevent shared elements overflow [https://stackoverflow.com/a/34784677]
            val options = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val decorView = activity.window.decorView
                val statusBackground: View = decorView.findViewById(android.R.id.statusBarBackground)
                val navBackground: View? = decorView.findViewById(android.R.id.navigationBarBackground)
                val statusPair = Pair.create(statusBackground, statusBackground.transitionName)

                if (navBackground == null) {
                    ActivityOptionsCompat.makeSceneTransitionAnimation(activity, imagePair, statusPair)
                } else {
                    val navPair = Pair.create(navBackground, navBackground.transitionName)
                    ActivityOptionsCompat.makeSceneTransitionAnimation(activity, imagePair, statusPair, navPair)
                }
            } else {
                ActivityOptionsCompat.makeSceneTransitionAnimation(activity, imagePair)
            }

            // Launch activity
            ActivityCompat.startActivity(activity, starter, options.toBundle())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ase_activity_detail)

        // Retrieve item
        itemId = intent.getIntExtra(EXTRA_ITEM_ID, -1)
        val item = ResourcesData.instance[itemId]

        // Set image
        val detailImageView: ImageView = findViewById(R.id.detailImageView)
        detailImageView.setImageResource(item.image)
        // Set transition name
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            detailImageView.transitionName = item.transitionName
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // Respond to the action bar's Up/Home button
            android.R.id.home -> {
                supportFinishAfterTransition()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun finishAfterTransition() {
        val data = Intent()
        data.putExtra(SELECTED_ITEM_POSITION, itemId)
        setResult(Activity.RESULT_OK, data)
        super.finishAfterTransition()
    }

}
