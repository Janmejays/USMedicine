package com.integrationTestExample.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.integrationTestExample.databinding.ItemFragmentAddDrugBinding
import com.integrationTestExample.room.entities.ConceptProperty

/***
 * Add adapter we are passing drug list as constructor
 * and based on this list binding occurs
 * implemented view binding for adapters
 */
class AddAdapter(var drugList: ArrayList<ConceptProperty>) :
    RecyclerView.Adapter<AddAdapter.ListHolder>() {
    var onAddClicked: ((ConceptProperty, Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddAdapter.ListHolder {
        val binding = ItemFragmentAddDrugBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ListHolder(binding)
    }

    override fun onBindViewHolder(holder: ListHolder, position: Int) {
        drugList[position].let { holder.bind(it) }

    }

    override fun getItemCount(): Int {
        return drugList.size
    }

    inner class ListHolder(val binding: ItemFragmentAddDrugBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(get: ConceptProperty) {
            binding.drugNameTV.text = get.name.replaceFirstChar{it.uppercaseChar()}
        }
        init {
            /**
             * click event for add item
             * it will invoke items with position
             * and add from fragment.
             */
            binding.addTV.setOnClickListener {
                onAddClicked?.invoke(drugList[adapterPosition] , adapterPosition)
            }
        }
    }

}