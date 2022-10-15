package com.zion.remember.main.placeholder

import android.app.Activity
import androidx.activity.ComponentActivity
import com.zion.remember.game.NumberGameActivity
import com.zion.remember.item.ItemDetailHostActivity

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 *
 * TODO: Replace all uses of this class before publishing your app.
 */
object GameholderContent {

    /**
     * An array of sample (placeholder) items.
     */
    val ITEMS: MutableList<GameHolderItem> = ArrayList()



    private val activitys = arrayListOf( ItemDetailHostActivity::class.java, NumberGameActivity::class.java)

    init {
        // Add some sample items.
        addItem(createholderItem("数独",NumberGameActivity::class.java ))
    }

    private fun addItem(item: GameHolderItem) {
        ITEMS.add(item)

    }

    private fun createholderItem(gameName : String, gameActivity : Class<out Activity>): GameHolderItem {
        return GameHolderItem(gameName, gameName, gameActivity)
    }



    /**
     * A placeholder item representing a piece of content.
     */
    data class GameHolderItem(val id: String, val content: String, val instance: Class<out Activity>) {
        override fun toString(): String = content
    }
}