package com.integrationTestExample.adapters


import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.integrationTestExample.R
import com.integrationTestExample.databinding.ItemFragmentInteractionBinding
import com.integrationTestExample.room.entities.Interaction



/***
 * InteractionAdapter we are passing drug list as constructor
 * and based on this list binding occurs
 * implemented view binding for adapters
 * the count is drug list count we are passing from fragments.
 */
class InteractionAdapter(var drugList: ArrayList<Interaction>?, var Drug: String) :
    RecyclerView.Adapter<InteractionAdapter.ListHolder>() {
lateinit var context:Context
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): InteractionAdapter.ListHolder {
        context=parent.context
        val binding = ItemFragmentInteractionBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ListHolder(binding)
    }

    override fun onBindViewHolder(holder: ListHolder, position: Int) {
        drugList?.get(position)?.let { holder.bind(it) }

    }

    override fun getItemCount(): Int {
        return drugList?.size!!
    }

    inner class ListHolder(val binding: ItemFragmentInteractionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("RestrictedApi", "StringFormatMatches", "StringFormatInvalid")
        fun bind(get: Interaction) {
            if (get.minConceptName1 != Drug) {
                //first letter of the drug should be in Caps, that why adding replaceFirstChar
                binding.drugNameTV.text =get.minConceptName1.replaceFirstChar{it.uppercaseChar()}
            } else {
                binding.drugNameTV.text =get.minConceptName2.replaceFirstChar{it.uppercaseChar()}
            }
            binding.severityTV.text =   context.resources.getString(R.string.severity, get.severity, 0)
            binding.descriptionTV.text = get.description
        }
    }
}