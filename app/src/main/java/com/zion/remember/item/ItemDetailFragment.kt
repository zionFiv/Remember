package com.zion.remember.item

import android.content.ClipData
import android.os.Bundle
import android.view.DragEvent
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.CollapsingToolbarLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.zion.remember.placeholder.PlaceholderContent
import com.zion.remember.databinding.FragmentItemDetailBinding

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a [ItemListFragment]
 * in two-pane mode (on larger screen devices) or self-contained
 * on handsets.
 */
class ItemDetailFragment : Fragment() {

    /**
     * The placeholder content this fragment is presenting.
     */
    private var item: PlaceholderContent.PlaceholderItem? = null

    lateinit var itemDetailTextView: TextView
    private var toolbarLayout: CollapsingToolbarLayout? = null

    private var _binding: FragmentItemDetailBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val dragListener = View.OnDragListener { v, event ->
        if (event.action == DragEvent.ACTION_DROP) {
            val clipDataItem: ClipData.Item = event.clipData.getItemAt(0)
            val dragData = clipDataItem.text
            item = PlaceholderContent.ITEM_MAP[dragData]
            updateContent()
        }
        Toast.makeText(
            v.context,
            "Context click of item ",
            Toast.LENGTH_LONG
        ).show()

        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            if (it.containsKey(ARG_ITEM_ID)) {
                // Load the placeholder content specified by the fragment
                // arguments. In a real-world scenario, use a Loader
                // to load content from a content provider.
                item = PlaceholderContent.ITEM_MAP[it.getString(ARG_ITEM_ID)]
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentItemDetailBinding.inflate(inflater, container, false)
        val rootView = binding.root

        toolbarLayout = binding.toolbarLayout
        itemDetailTextView = binding.itemDetail

        updateContent()

        binding.fab?.setOnDragListener(dragListener)
        binding.fab?.setOnLongClickListener {
            it.startDragAndDrop(null, View.DragShadowBuilder(it), null, 0)
        }
        binding.fab?.setOnClickListener {

//            GlobalScope.launch {
//
//                while (true) {
////                    Toast.makeText(activity, "aaa", Toast.LENGTH_SHORT).show()
//
////                        val bitmap = BitmapFactory.decodeResource(resources, R.mipmap.ic_a)
////                        var a = HashMap<String, String>(16)
////                        a.put("a", "b")
////                        a.put("a", "b")
////                        a.put("a", "b")
////                        a.put("a", "b")
////                        a.put("a", "b")
////                        a.put("a", "b")
//
//                }
//            }

        }



        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }


    private fun updateContent() {
        toolbarLayout?.title = item?.content
//       val job =  GlobalScope.launch {
//            while (true) {
//                if(resources != null) {
//                    val bitmap = BitmapFactory.decodeResource(resources, R.mipmap.ic_a)
//                }
//            }
//        }
//        binding.fab?.setImageBitmap(bitmap)

        // Show the placeholder content as text in a TextView.
        item?.let {
            itemDetailTextView.text = it.details
        }
    }

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ARG_ITEM_ID = "item_id"

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}