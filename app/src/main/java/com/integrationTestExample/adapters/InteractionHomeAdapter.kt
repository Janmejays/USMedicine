package com.integrationTestExample.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.integrationTestExample.R
import com.integrationTestExample.databinding.ItemFragmentListBinding
import com.integrationTestExample.room.entities.ConceptProperty

/**
 * drug list is passed from fragment
 * is inoking itemclick for selecting specific item
 */
class InteractionHomeAdapter(var drugList: ArrayList<ConceptProperty>?) :
    RecyclerView.Adapter<InteractionHomeAdapter.ListHolder>() {
    var onItemClicked: ((ConceptProperty, Int) -> Unit)? = null
    lateinit var context: Context

    inner class ListHolder(val binding: ItemFragmentListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("StringFormatInvalid", "StringFormatMatches")
        fun bind(get: ConceptProperty) {
            //for bold and Esclamtion if interaction is present.
            if (get.interaction) {
                binding.drugNameTV.setTypeface(
                    binding.drugNameTV.typeface,
                    Typeface.BOLD
                )
                //First letter of the drug should be in Caps, replaceFirstChar does the work of changing the character to Caps
                val text = get.name.replaceFirstChar { it.uppercaseChar() }
                binding.drugNameTV.text  = context.resources.getString(R.string.drug_name, text, 0)
            } else {
                binding.drugNameTV.text =
                    get.name.replaceFirstChar { it.uppercaseChar() }
            }

        }

        init {
            //item click event for viewing the detailed interaction information
            binding.card.setOnClickListener {
                if (drugList?.get(adapterPosition)?.interaction == true) {
                    drugList?.let { it1 ->
                        onItemClicked?.invoke(
                            it1[adapterPosition],
                            adapterPosition
                        )
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return drugList?.size!!
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): InteractionHomeAdapter.ListHolder {
        val binding = ItemFragmentListBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        return ListHolder(binding)
    }

    override fun onBindViewHolder(holder: ListHolder, position: Int) {
        drugList?.get(position)?.let { holder.bind(it) }
    }
}