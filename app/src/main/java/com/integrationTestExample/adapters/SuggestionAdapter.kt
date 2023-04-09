package com.integrationTestExample.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.integrationTestExample.R
import com.integrationTestExample.databinding.ItemFragmentListBinding

/**
 * this adapter is for getting the suggestions
 *we are showing no text found as there is no suggestion avaialable
 * else showing the suggestion list.
 */
class SuggestionAdapter(var drugList: ArrayList<String>) :
    RecyclerView.Adapter<SuggestionAdapter.ListHolder>() {
    lateinit var context: Context
    var onItemClicked: ((Int, String) -> Unit)? = null
    lateinit var mNoSuggestionFound: String

    inner class ListHolder(val binding: ItemFragmentListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("StringFormatMatches")
        fun bind(get: String) {
            //Suggestion listing
            if (get == mNoSuggestionFound) {
                binding.drugNameTV.text = mNoSuggestionFound
            } else
                binding.drugNameTV.text = context.resources.getString(
                    R.string.suggestion_found,
                    get.replaceFirstChar { it.uppercaseChar() },
                    0
                )
        }

        init {
            binding.card.setOnClickListener {
                if (drugList[adapterPosition] != mNoSuggestionFound) {
                    onItemClicked?.invoke(
                        adapterPosition,
                        drugList[adapterPosition]
                    )
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return drugList.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SuggestionAdapter.ListHolder {
        val binding =
            ItemFragmentListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        mNoSuggestionFound = context.resources.getString(R.string.nosuggestion)
        return ListHolder(binding)
    }

    override fun onBindViewHolder(holder: ListHolder, position: Int) {
        drugList[position].let { holder.bind(it) }
    }
}