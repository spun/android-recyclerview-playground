package com.spundev.navigationsharedelements

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.res.use
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.material.transition.MaterialArcMotion
import com.google.android.material.transition.MaterialContainerTransform
import com.spundev.commonresources.data.ResourcesData

class DetailFragment : Fragment() {

    private val args: DetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Shared element transition
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.my_nav_host_fragment
            duration = resources.getInteger(R.integer.nse_motion_duration_large).toLong()
            scrimColor = Color.TRANSPARENT
            setPathMotion(MaterialArcMotion())
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.nse_fragment_detail, container, false)

        // Retrieve item
        val itemId = args.itemId
        val item = ResourcesData.instance[itemId]

        // Delay enter transition (required if the image needs some time to be loaded)
        // postponeEnterTransition()

        // Set image
        val detailImageView: ImageView = rootView.findViewById(R.id.detailImageView)
        detailImageView.setImageResource(item.image)
        // Set transition name
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            detailImageView.transitionName = item.transitionName
        }

        // Continue enter transition
        // startPostponedEnterTransition()

        return rootView
    }
}
