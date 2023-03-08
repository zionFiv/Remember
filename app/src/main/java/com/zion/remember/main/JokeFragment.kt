package com.zion.remember.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zion.remember.R
import com.zion.remember.databinding.FragmentJokeBinding
import com.zion.remember.http.RManager
import com.zion.remember.word.WordsListActivity

class JokeFragment : Fragment() {
    private var _jokeBinding : FragmentJokeBinding?= null
    private lateinit var viewModel : JokeViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _jokeBinding =  FragmentJokeBinding.inflate(inflater, container, false)
        return _jokeBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(JokeViewModel::class.java)
        viewModel.getJoke().observe(viewLifecycleOwner){
            _jokeBinding?.jokeTxt?.text = it.list[0].content
        }
        viewModel.getCityWeather("南昌市").observe(viewLifecycleOwner) {
            _jokeBinding?.weatherSituation?.text = it.weather
        }

        viewModel.getWords().observe(viewLifecycleOwner){
            var items = mutableListOf<String>()
            it?.forEach { vo ->
                items.add(vo.word + " : " + vo.wordExplain)
            }
            _jokeBinding?.wheelView?.setDatas(items)
        }

        _jokeBinding?.wheelMore?.apply {
            text = "更多"
            setOnClickListener {
                startActivity(Intent( activity, WordsListActivity::class.java))
            }

        }

    }
}