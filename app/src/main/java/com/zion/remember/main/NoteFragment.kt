package com.zion.remember.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView

import com.zion.remember.databinding.FragmentNoteBinding
import com.zion.remember.db.NoteInformation


/*
 * MVVM livedata + view model + Room
 * 左滑删除，长按修改，下拉刷新，上拉加载
 */
class NoteFragment : Fragment() {

    companion object {
        fun newInstance() = NoteFragment()
    }

    private lateinit var viewModel: NoteViewModel
    private var _binding: FragmentNoteBinding? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNoteBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        viewModel = ViewModelProvider(this).get(NoteViewModel::class.java)

        viewModel.getFirstShowTen().observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                _binding?.noteList?.adapter =
                    NoteItemRecyclerViewAdapter(it)
                _binding?.noteRefresh?.setEnableLoadMore(false)
                Log.d("load", "3 + " + System.currentTimeMillis())
                _binding?.noteList?.scrollToPosition(it.size-1)
            }
        }



        _binding?.noteRefresh?.setOnRefreshListener {
            viewModel.getLastTen()?.observe(viewLifecycleOwner) { result ->
                result.onSuccess {
                    (_binding?.noteList?.adapter as NoteItemRecyclerViewAdapter).addHeadData((it))
                    _binding?.noteRefresh?.finishRefresh()
                }

            } ?:
                _binding?.noteRefresh?.finishRefresh()


        }
        _binding?.noteRefresh?.setOnLoadMoreListener {
            viewModel.getNextTen().observe(viewLifecycleOwner) { result ->
                result.onSuccess {
                    (_binding?.noteList?.adapter as NoteItemRecyclerViewAdapter).addData((it))
                    if (it.size < 10) {
                        _binding?.noteRefresh?.setEnableLoadMore(false)
                    }
                }

            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}