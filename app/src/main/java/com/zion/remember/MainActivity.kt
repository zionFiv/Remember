package com.zion.remember

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.ui.AppBarConfiguration
import com.dianping.logan.Logan
import com.zion.remember.databinding.ActivityMainBinding
import com.zion.remember.game.NumberGameActivity
import com.zion.remember.item.ItemDetailHostActivity
import com.zion.remember.item.ItemListFragment
import com.zion.remember.main.*
import com.zion.remember.util.LogUtil

class MainActivity : AppCompatActivity() {
    private var videoFragment = VideoFragment()
    private var gameFragment = GameFragment()
    private var settingsFragment = SettingsFragment()
    private var noteFragment = NoteFragment.newInstance()
    private var bookFragment = BookFragment()
    private var jokeFragment = JokeFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(binding.root)
        Logan.w("this is log 2", 0x12)
        replaceFragment(bookFragment)
        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            Logan.w("this is log check", 0x12)
//            Logan.f()
//            LogUtil.parse(this@MainActivity, arrayOf("2022-06-15"))=
            when (checkedId) {
                R.id.main_tab -> replaceFragment(bookFragment)
                R.id.note_tab -> {
                    replaceFragment(noteFragment)
                }
                R.id.game_tab -> replaceFragment(jokeFragment)
                R.id.settings_tab -> {
                    replaceFragment(settingsFragment)
                }
            }


        }

    }

    private fun replaceFragment(nextFragment: Fragment) {
        val fm = supportFragmentManager.beginTransaction()
        fm.replace(R.id.container, nextFragment)
        fm.commit()
    }


    override fun onStart() {
        super.onStart()
        Log.w("onStart", "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.w("onResume", "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.w("onPause", "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.w("onStop", "onStop")
    }

    override fun onRestart() {
        super.onRestart()
        Log.w("onRestart", "onRestart")
    }

}