package com.integrationTestExample.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.integrationTestExample.R
import com.integrationTestExample.adapters.ListAdapter
import com.integrationTestExample.databinding.FragmentListBinding
import com.integrationTestExample.room.entities.ConceptProperty
import com.integrationTestExample.viewmodels.ListViewModel
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class ListFragment : Fragment(), KodeinAware {
    override val kodein by kodein()
    /**
     * used kodein dependency injection
     * the require dependencies for view models like
     * repository api,local db .. will be provided by kodein.
     */

    private val viewModel: ListViewModel by instance()
    private var _binding: FragmentListBinding? = null
    private lateinit var listAdapter: ListAdapter
    private var listDrugs: ArrayList<ConceptProperty> = ArrayList()
    private val binding get() = _binding!!
    private var isAllFabVisible: Boolean? = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
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
                 *the live data is observed through drugList
                 * for avoiding leaks used live data to expand to view
                 */
                viewModel.drugList.observe(viewLifecycleOwner) { drugs ->
                    if (!drugs.isNullOrEmpty()) {
                        //clear of any data exist in the list
                        listDrugs.clear()
                        //hiding fab button and text if we gor the list from room
                        binding.fab.visibility = View.GONE
                        binding.textHome.visibility = View.GONE
                        //adding items to list
                        listAdapter.drugList?.clear()
                        listDrugs.addAll(drugs)
                        listAdapter.notifyDataSetChanged()

                    } else {
                        //if there is no data store yet then clear the list and make fab
                        //visible and showing info text
                        listDrugs.clear()
                        listAdapter.drugList?.clear()
                        listAdapter.notifyItemChanged(0)
                        binding.fab.visibility = View.VISIBLE
                        binding.textHome.visibility = View.VISIBLE
                        binding.textHome.setText(R.string.empty_list)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun initViews() {
        /***
         * first call to vm for getting the list, it will return an updated list with interaction
         * details.
         */
        viewModel.getDrugsList()//
        binding.listRV.layoutManager = LinearLayoutManager(requireActivity())
        listAdapter = ListAdapter(listDrugs)
        binding.listRV.adapter = listAdapter
        binding.fab.setOnClickListener {
            isAllFabVisible = if (!isAllFabVisible!!) {
                fabVisible()
                true
            } else {
                fabInvisible()
                false
            }
        }
        binding.addCamFab.setOnClickListener {
            val navController = findNavController()
            navController.popBackStack() // current fragment will be pop up from the stack
            navController.navigate(R.id.navigation_scan)
        }
        binding.addSearch.setOnClickListener {
            val navController = findNavController()
            navController.popBackStack() // current fragment will be pop up from the stack
            navController.navigate(R.id.navigation_search)
        }
    }
    /***
     * if there is no records found then visibility of the search and camera fab is true
     * details.
     */
    private fun fabVisible() {
        binding.addCamFab.visibility = View.VISIBLE
        binding.addCameraTV.visibility = View.VISIBLE
        binding.addSearch.visibility = View.VISIBLE
        binding.searchFabTV.visibility = View.VISIBLE
    }
    /***
     *hiding the fab for search and scan
     */
    private fun fabInvisible() {
        binding.addCamFab.visibility = View.GONE
        binding.addCameraTV.visibility = View.GONE
        binding.addSearch.visibility = View.GONE
        binding.searchFabTV.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel._druglist.value = null
        _binding = null
    }
}
