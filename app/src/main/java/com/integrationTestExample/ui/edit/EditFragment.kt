package com.integrationTestExample.ui.edit

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.integrationTestExample.R
import com.integrationTestExample.adapters.EditAdapter
import com.integrationTestExample.databinding.FragmentListBinding
import com.integrationTestExample.room.entities.ConceptProperty
import com.integrationTestExample.utils.CustomDialog
import com.integrationTestExample.viewmodels.ListViewModel
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class EditFragment : Fragment(), KodeinAware {
    override val kodein by kodein()
    private val viewModel: ListViewModel by instance()

    /**
     * dependency injection(kodein is added,
     * it will produce dependencies for creating the view model.
     */

    private var _binding: FragmentListBinding? = null
    private lateinit var mEditAdapter: EditAdapter
    private var listDrugs: ArrayList<ConceptProperty> = ArrayList()
    private val binding get() = _binding!!
    private var customDialog: CustomDialog? = null
    private var delPosition: Int = -1
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
        initObservers()
        initViews()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun initObservers() {
        lifecycleScope.launch {
            try {
                /**
                 * getting the drug list for delete(including both interactions and without interactions)

                 */
                viewModel._druglist.observe(viewLifecycleOwner) { drugs ->
                    if (!drugs.isNullOrEmpty()) {
                        listDrugs.clear()
                        binding.fab.visibility = View.INVISIBLE
                        listDrugs.addAll(drugs)
                        binding.textHome.visibility = View.GONE
                        mEditAdapter.notifyDataSetChanged()

                    } else {
                        listDrugs.clear()
                        mEditAdapter.notifyDataSetChanged()
                        binding.fab.visibility = View.VISIBLE
                        binding.textHome.visibility = View.VISIBLE
                        binding.textHome.setText(R.string.empty_list)
                    }


                }
                /**
                after deleting any item updating the list
                 */
                viewModel.del.observe(viewLifecycleOwner) { del ->
                    if (!del.isNullOrEmpty()) {
                        listDrugs.removeAt(delPosition)
                        mEditAdapter.notifyItemRemoved(delPosition)
                        viewModel.del.value = null
                        binding.textHome.visibility = View.GONE
                        if (listDrugs.size > 0) {
                            binding.fab.visibility = View.INVISIBLE
                        } else {
                            binding.fab.visibility = View.VISIBLE
                            binding.textHome.visibility = View.VISIBLE
                            binding.textHome.setText(R.string.empty_list)
                        }
                        showCustomDialogue()

                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initViews() {
        binding.listRV.layoutManager = LinearLayoutManager(requireActivity())
        mEditAdapter = EditAdapter(listDrugs)
        binding.listRV.adapter = mEditAdapter
        viewModel.getDrugList()
        //adapter clicking event for delete
        mEditAdapter.onDeleteClicked = { pos ->
            delPosition = pos
            viewModel.deleteITem(listDrugs[pos].rxcui)
            mEditAdapter.notifyDataSetChanged()
        }
        /**
         * if fab is visible(list is empty)
         * then enabling it and on click of fab
         * will shows up camera and search fabs
         */

        binding.fab.setOnClickListener {
            isAllFabVisible = if (!isAllFabVisible!!) {
                fabVisible()
                // make the boolean variable true as
                // we have set the sub FABs
                // visibility to GONE
                true
            } else {
                fabInvisible()
                false
            }
        }
        /**
         * for launching the camera screen
         *
         */
        binding.addCamFab.setOnClickListener {
            val navController = findNavController()
            navController.popBackStack() // current fragment will be pop up from the stack
            navController.navigate(R.id.navigation_scan)
        }
        /**
         * for launching the Search screen
         *
         */
        binding.addSearch.setOnClickListener {
            val navController = findNavController()
            navController.popBackStack() // current fragment will be pop up from the stack
            navController.navigate(R.id.navigation_search)
        }

    }
    /**
     * for setting visibility to the fabs
     *
     */
    private fun fabVisible() {
        binding.addCamFab.visibility = View.VISIBLE
        binding.addCameraTV.visibility = View.VISIBLE
        binding.addSearch.visibility = View.VISIBLE
        binding.searchFabTV.visibility = View.VISIBLE
    }

    /**
     * for hiding the fabs(there is some data in the list)
     *
     */
    private fun fabInvisible() {
        binding.addCamFab.visibility = View.GONE
        binding.addCameraTV.visibility = View.GONE
        binding.addSearch.visibility = View.GONE
        binding.searchFabTV.visibility = View.GONE
    }
    /**
     * for showing delete success message
     *
     */
    private fun showCustomDialogue() {
        customDialog = CustomDialog(
            getString(R.string.delete),
            getString(R.string.drug_deleted_success),
            object : CustomDialog.ContentInterface {
                override fun onSendClicked() {
                    onClose()
                }
            },
            getString(R.string.delete)
        )
        customDialog?.show(
            (activity as FragmentActivity).supportFragmentManager,
            "dialog"
        )
    }
    override fun onDestroy() {
        super.onDestroy()
        viewModel._druglist.value = null
    }
    private fun onClose() {
        customDialog?.dismiss()
        val navController = findNavController()
        navController.popBackStack()
        navController.navigate(R.id.navigation_home)
    }


}