package com.rpg.funbox.presentation.game.gameselect

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rpg.funbox.R
import com.rpg.funbox.presentation.game.gameselect.GameSelectUiState.Companion.QUESTION_CARD_TYPE
import timber.log.Timber

@BindingAdapter("app:game_title")
fun TextView.bindGameTitle(gameCard: GameSelectUiState.GameCard) {
    when (gameCard.gameType) {
        QUESTION_CARD_TYPE -> {
            text = resources.getString(R.string.game_question_card_title)
        }
    }
}

@BindingAdapter("entries")
fun Spinner.setEntries(entries: List<Int>?) {
    entries?.run {
        val arrayAdapter = ArrayAdapter(context, R.layout.quiz_question_count_spinner, entries)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter = arrayAdapter
    }
}

@InverseBindingAdapter(attribute = "selectedValue", event = "selectedValueAttrChanged")
fun Spinner.getSelectedValue(): Any? {
    return selectedItem
}

@BindingAdapter("selectedValueAttrChanged")
fun Spinner.setInverseBindingAdapter(inverseBindingListener: InverseBindingListener?) {
    inverseBindingListener?.run {
        onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (tag != position) {
                    inverseBindingListener.onChange()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
}

@BindingAdapter("selectedValue")
fun Spinner.setSelectedValue(selectedValue: Int) {
    adapter?.run {
        Timber.d("Select $selectedValue")
        val position = (adapter as ArrayAdapter<Int>).getPosition(selectedValue)
        setSelection(position, false)
        tag = position
    }
}

@BindingAdapter("app:submitGames")
fun <T, VH : RecyclerView.ViewHolder> RecyclerView.bindItems(items: List<T>) {
    val adapter = this.adapter ?: return
    val listAdapter: ListAdapter<T, VH> = adapter as ListAdapter<T, VH>
    listAdapter.submitList(items)
}