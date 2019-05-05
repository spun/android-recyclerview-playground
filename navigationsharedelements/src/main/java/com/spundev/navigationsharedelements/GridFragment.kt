package com.spundev.navigationsharedelements

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.spundev.commonresources.data.ResourcesData
import com.spundev.navigationsharedelements.adapter.GridAdapter

class GridFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.nse_fragment_grid, container, false)

        // RecyclerView
        recyclerView = rootView.findViewById(R.id.recyclerview)
        val adapter = GridAdapter(requireContext()) { id, view ->
            // Navigation action
            val action = GridFragmentDirections.actionGridToDetail(id)
            // Shared element
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // Shared element details
                val extras = FragmentNavigatorExtras(view to ResourcesData.instance[id].transitionName)
                view.findNavController().navigate(action, extras)
            } else {
                view.findNavController().navigate(action)
            }
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)

        // Update content
        adapter.submitList(ResourcesData.instance)

        // Wait for RecyclerView
        postponeEnterTransition()
        recyclerView.doOnPreDraw {
            startPostponedEnterTransition()
        }
        return rootView
    }
}
