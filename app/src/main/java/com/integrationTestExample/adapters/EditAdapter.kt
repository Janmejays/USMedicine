package com.integrationTestExample.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.integrationTestExample.R
import com.integrationTestExample.databinding.ItemFragmentEditBinding
import com.integrationTestExample.room.entities.ConceptProperty

/***
 * Edit  adapter we are passing drug list as constructor
 * and based on this list binding occurs
 * implemented view binding for adapters
 * the count is drug list count we are passing from fragments.
 */
class EditAdapter(var drugList: ArrayList<ConceptProperty>?) :
    RecyclerView.Adapter<EditAdapter.ListHolder>() {
     var onDeleteClicked: ((Int) -> Unit)? = null
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditAdapter.ListHolder {
        val binding = ItemFragmentEditBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        return ListHolder(binding)
    }

    override fun onBindViewHolder(holder: ListHolder, position: Int) {
        drugList?.get(position)?.let { holder.bind(it) }

    }

    override fun getItemCount(): Int {
        return drugList?.size!!
    }

    inner class ListHolder(val binding: ItemFragmentEditBinding) :
        RecyclerView.ViewHolder(binding.root) {
        ////Setting the text as bold and exclamation if interaction is found
        @SuppressLint("StringFormatInvalid", "StringFormatMatches")
        fun bind(get: ConceptProperty) {
            if (get.interaction) {
                binding.drugNameTV.setTypeface(
                    binding.drugNameTV.typeface,
                    Typeface.BOLD
                )
                val text = get.name.replaceFirstChar { it.uppercaseChar() }
                binding.drugNameTV.text = context.resources.getString(R.string.drug_name, text, 0)
            } else {
                //setting as normal text
                binding.drugNameTV.setTypeface(null, Typeface.NORMAL)
                binding.drugNameTV.text = get.name.replaceFirstChar { it.uppercaseChar() }
            }

        }

        init {
            //invoking delete by position
            binding.deleteTV.setOnClickListener {
                onDeleteClicked?.invoke(adapterPosition)
            }
        }
    }

}