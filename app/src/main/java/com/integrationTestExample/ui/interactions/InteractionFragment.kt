package com.integrationTestExample.ui.interactions

//import com.druginteractr.views.launch.interactions.InteractionFragmentArgs
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.integrationTestExample.R
import com.integrationTestExample.adapters.InteractionAdapter
import com.integrationTestExample.databinding.FragmentInteractionBinding
import com.integrationTestExample.room.entities.Interaction
import com.integrationTestExample.viewmodels.InteractionViewModel
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class InteractionFragment : Fragment() , KodeinAware {
    /**
     * Kodein dependency is used , it will supply
     * the required dependencies for view model.
     */
    override val kodein by kodein()
    private val viewModel: InteractionViewModel by instance()

    private lateinit var mInteactionAdapter: InteractionAdapter
    private var listDrugs: ArrayList<Interaction> = ArrayList()

//    companion object {
//        fun newInstance() = InteractionFragment()
//    }

    private lateinit var binding: FragmentInteractionBinding



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInteractionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        initviews()
    }

    private fun initviews() {
        try {
            binding.backTV.setOnClickListener {
                val navController = findNavController()
                navController.popBackStack()
                navController.navigate(R.id.navigation_interaction_home)
            }
            /**
             * parameters are received by navigation arguments.
             * required parameters are name and rxcui code for selected drug
             */
            val args: InteractionFragmentArgs by navArgs()
            if (!args.id.isNullOrEmpty()) {
                binding.drugNameTV.text = args.name.replaceFirstChar { it.uppercase() }
                binding.interactionRV.layoutManager = LinearLayoutManager(requireActivity())
                mInteactionAdapter = InteractionAdapter(listDrugs, args.name)
                binding.interactionRV.adapter = mInteactionAdapter
                viewModel.getInteractionData(args.id)
            }
        } catch (Ex: Exception) {
            Ex.printStackTrace()
        }
    }


    private fun initObservers() {
        lifecycleScope.launch {
            try {
                /**
                 * observing live data from table_interactions.
                */
                viewModel.interactionlist.observe(viewLifecycleOwner) { drugs ->
                    if (listDrugs.size > 0)
                        listDrugs.clear()
                    listDrugs.addAll(drugs)
                    mInteactionAdapter.notifyItemChanged(0)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}