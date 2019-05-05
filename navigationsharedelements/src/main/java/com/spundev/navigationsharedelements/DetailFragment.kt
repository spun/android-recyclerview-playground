package com.spundev.navigationsharedelements

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import com.spundev.commonresources.data.ResourcesData

class DetailFragment : Fragment() {

    private val args: DetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Shared element transition
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            /*val ts = TransitionSet()
            // This transition captures the layout bounds of target views before and after
            // the scene change and animates those changes during the transition.
            ts.addTransition(ChangeBounds())
            // This Transition captures an ImageView's matrix before and after the scene change
            // and animates it during the transition.
            // In combination with ChangeBounds, ChangeImageTransform allows ImageViews that
            // change size, shape, or ImageView.ScaleType to animate contents smoothly.
            ts.addTransition(ChangeImageTransform())
            sharedElementEnterTransition = ts*/

            // Alternative
            sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
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
