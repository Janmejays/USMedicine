package com.integrationTestExample.ui.interactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.integrationTestExample.R
import com.integrationTestExample.adapters.InteractionHomeAdapter
import com.integrationTestExample.databinding.FragmentListBinding
import com.integrationTestExample.room.entities.ConceptProperty
import com.integrationTestExample.viewmodels.ListViewModel
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class InteractionFragmentHome : Fragment(), KodeinAware {
    /**
     * added kodein dependency injection it will initialize view model and
     * required dependencies.
     */
    override val kodein by kodein()
    private val viewModel: ListViewModel by instance()
//    companion object {
//        fun newInstance() = InteractionFragmentHome()
//    }
    private var _binding: FragmentListBinding? = null
    private lateinit var listAdapter: InteractionHomeAdapter
    private var listDrugs: ArrayList<ConceptProperty> = ArrayList()
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return  binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initObservers()
    }

    private fun initObservers() {
        lifecycleScope.launch {
            try {
                /**
                 * interaction live data is emitted if data present in room database
                 */
                viewModel.interactiondruglist.observe(viewLifecycleOwner) { drugs ->

                    if (!drugs.isNullOrEmpty()) {
                        //clearing the exiting drugs from list
                        listDrugs.clear()
                        binding.textHome.visibility = View.GONE
                        listDrugs.addAll(drugs)
                        listAdapter.notifyItemChanged(0)
                    } else {
                        binding.textHome.visibility = View.VISIBLE
                        binding.textHome.setText(R.string.empty_interaction)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun initViews() {
        /**
         *  viewModel.getInteractionDrugList() is for getting drugs which has interaction to each other
         */
        viewModel.getIntercationDrugList()
        binding.listRV.layoutManager = LinearLayoutManager(requireActivity())
        listAdapter = InteractionHomeAdapter(listDrugs)
        binding.listRV.adapter = listAdapter
        binding.fab.isVisible = false
        listAdapter.onItemClicked = { drugs, _ ->
            val action =
                InteractionFragmentHomeDirections.actionInteractionFragmentHomeToNavigationInteraction(
                    drugs.rxcui,
                    drugs.name
                )
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}