package com.tivasoft.biconui.ui.common.profile.backpack

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.tivasoft.biconui.data.model.network.responses.common.Backpack
import com.tivasoft.biconui.ui.common.profile.backpack.BackpackFragment
import com.tivasoft.biconui.ui.common.profile.conversation.ConversationFragment
import kotlin.math.ceil
import kotlin.math.min

class BackpackTabAdapter(
    fragment: ConversationFragment,
    private val backpackList: ArrayList<Backpack>
) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int =
        ceil(backpackList.size.toDouble() / 4).toInt()

    override fun createFragment(position: Int): Fragment {
        val fromIndex = position * 4
        val toIndex = min(fromIndex + 4, backpackList.size)
        val items = backpackList.subList(
            fromIndex, toIndex
        )
        return BackpackFragment(items)
    }
}