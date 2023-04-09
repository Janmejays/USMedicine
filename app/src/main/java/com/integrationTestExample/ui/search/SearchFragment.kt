package com.integrationTestExample.ui.search

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.integrationTestExample.R
import com.integrationTestExample.adapters.AddAdapter
import com.integrationTestExample.adapters.SuggestionAdapter
import com.integrationTestExample.data.models.editTextFlow.EditTextFlow
import com.integrationTestExample.databinding.FragmentSearchBinding
import com.integrationTestExample.room.entities.ConceptProperty
import com.integrationTestExample.room.entities.Interaction
import com.integrationTestExample.utils.AppKeyBoard
import com.integrationTestExample.utils.Constants.Companion.SEARCH_LIMIT
import com.integrationTestExample.utils.CustomDialog
import com.integrationTestExample.viewmodels.SearchViewModel
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class SearchFragment : Fragment(), KodeinAware {
    override val kodein by kodein()

    /**
     * kodein dependency injection is used so that it will provide
     * required dependencies for creating the view model.
     */
//    companion object {
//        fun newInstance() = SearchFragment()
//    }

    private lateinit var binding: FragmentSearchBinding

    //view model is injected from instance of kodein

    private val viewModel: SearchViewModel by instance()
    private var suggestionDrugs: ArrayList<String> = ArrayList()
    private var drugNames: ArrayList<ConceptProperty> = ArrayList()
    private lateinit var drug: ConceptProperty
    private lateinit var iteractionItem: Interaction
    private lateinit var conceptProperty: ConceptProperty
    private var interactionList: ArrayList<Interaction> = ArrayList()
    private lateinit var msuggestionAdapter: SuggestionAdapter
    private lateinit var maddAdapter: AddAdapter
    private var customDialog: CustomDialog? = null
    private var selectedPosition = -1
    private var scanedText = ""
    private var suggestionTriggered: Boolean = false
    private var appKeyBoard = AppKeyBoard()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
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
                 * Observer for suggestions from search
                 */
                viewModel._suggestionList.observe(viewLifecycleOwner) { suggestion ->
                    binding.progressBar.visibility = View.GONE
                    if (suggestion != null) {
                        println(suggestionTriggered)
                        if (!suggestionTriggered) {
                            suggestionTriggered = true
                            hideDrugRecyclerView()
                            if (!suggestion.suggestionGroup.suggestionList.suggestion.isNullOrEmpty()) {
                                suggestionDrugs.addAll(suggestion.suggestionGroup.suggestionList.suggestion)
                                ///for notifying the adapter for some thing is added to the list
                                //so it will update the entire list without any leakage.
                                msuggestionAdapter.notifyItemChanged(0)
                            } else {
                                msuggestionAdapter.drugList.add(requireActivity().getString(R.string.nosuggestion)) //"No suggestions found!!")
                                msuggestionAdapter.notifyItemChanged(0)
                            }
                        }
                    }
                }
                /**
                 * Observer for drug list after selecting drug from suggestions.
                 */
                viewModel.drugLiveData.observe(viewLifecycleOwner) {
                    if (it != null) {
                        hideSuggestionRecyclerView()
                        if (!it.drugGroup.conceptGroup.isNullOrEmpty()) {
                            val size = it.drugGroup.conceptGroup.size
                            for (i in 0 until size) {
                                val itemfound =
                                    it.drugGroup.conceptGroup[i].conceptProperties
                                if (!itemfound.isNullOrEmpty()) {
                                    drugNames.addAll(it.drugGroup.conceptGroup[i].conceptProperties)
                                }
                            }
                            if (drugNames.size == 0) {
                                showNoDrugsFound()
                            } else {
                                binding.errorMsgTV.visibility = View.GONE
                                binding.recyclerViewDrug.visibility = View.VISIBLE
                                suggestionTriggered = true
                                ///for notifying the adapter for some thing is added to the list
                                //so it will update the entire list without any leakage.
                                msuggestionAdapter.notifyItemChanged(0)
                            }
                        } else {
                            showNoDrugsFound()
                        }

                    }
                }
                /**
                 * Observer for errors
                 */
                viewModel.errorLiveData.observe(viewLifecycleOwner) { error ->
                    binding.recyclerViewSuggestions.visibility = View.GONE
                    binding.recyclerViewDrug.visibility = View.GONE
                    binding.errorMsgTV.visibility = View.VISIBLE
                    binding.errorMsgTV.text = error
                    binding.progressBar.visibility = View.GONE
                }
                /**
                 * Observer for Save result(it will got invoked after successful save
                 */
                viewModel.saveLivedata.observe(viewLifecycleOwner) { msg ->
                    if (!msg.isNullOrEmpty())
                        showCustomDialogue()
                }
                /**
                 * Observer for interactions
                 * we are normalizing the api results to table interaction fields by looping the response.
                 */
                viewModel.interactionLiveData.observe(viewLifecycleOwner) { interaction ->
                    try {
                        if (interaction != null) {
                            if (!interaction.fullInteractionTypeGroup.isNullOrEmpty()) {
                                val size =
                                    interaction.fullInteractionTypeGroup[0].fullInteractionType.size
                                if (size > 0) {

                                    for (i in 0 until size) {
                                        /**
                                         * if item found then
                                         * adding specific items to local table.
                                         */
                                        val itemfound =
                                            interaction.fullInteractionTypeGroup[0].fullInteractionType[i]
                                        val minConceptName1 = itemfound.minConcept[0].name
                                        val minConceptId1 = itemfound.minConcept[0].rxcui
                                        val minConceptName2 = itemfound.minConcept[1].name
                                        val minConceptId2 = itemfound.minConcept[1].rxcui
                                        val severity = itemfound.interactionPair[0].severity
                                        val description =
                                            itemfound.interactionPair[0].description
                                        if (minConceptId1 != minConceptId2) {
                                            iteractionItem = Interaction(
                                                id = 0,
                                                minConceptId1 = minConceptId1,
                                                minConceptName1 = minConceptName1,
                                                minConceptId2 = minConceptId2,
                                                minConceptName2 = minConceptName2,
                                                severity = severity,
                                                description = description
                                            )
                                            interactionList.addAll(listOf(iteractionItem))
                                        }
                                    }
                                    val sizes = interactionList.size
                                    if (sizes > 0)
                                    /**
                                     * if found interactions then deleting the tables and adding the list
                                     * (for eg: if the table has ids(1000345,384598)  then we are deleting the
                                     * table and adding new results to it
                                     *
                                     */
                                        viewModel.deleteInteraction(interactionList)
                                    if (!drug.rxcui.isNullOrEmpty())
                                    /**
                                     * inserting the drug details
                                     *
                                     */
                                        viewModel.insertDrugData(drug)

                                }
                                binding.progressBar.visibility = View.GONE
                            } else {
                                if (!drug.rxcui.isNullOrEmpty())
                                    binding.progressBar.visibility = View.GONE
                                /**
                                 * inserting the drug details if interactions is empty.
                                 *
                                 */
                                viewModel.insertDrugData(drug)

                            }
                        }
                    } catch (Ex: Exception) {
                        cleaFields()
                    }
                }
                /**
                 * this is got invoked by the barcode data is present.
                 */
                viewModel.NDCRelationLiveData.observe(viewLifecycleOwner) {
                    if (it.ndcInfoList != null) {
                        binding.progressBar.visibility = View.GONE
                        hideSuggestionRecyclerView()
                        conceptProperty = ConceptProperty(
                            id = 0,
                            name = it.ndcInfoList.ndcInfo[0].conceptName,
                            rxcui = it.ndcInfoList.ndcInfo[0].rxcui,
                            suppress = "",
                            interaction = false,
                            language = "",
                            synonym = "",
                            tty = "",
                            umlscui = ""
                        )
                        drugNames.addAll(listOf(conceptProperty))
                    }
                    if (drugNames.size == 0) {
                        showNoDrugsFound()
                    } else {
                        binding.errorMsgTV.visibility = View.GONE
                        binding.recyclerViewDrug.visibility = View.VISIBLE
                        suggestionTriggered = true
                        msuggestionAdapter.notifyItemChanged(0)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * dialogue box added to save.
     */
    private fun showCustomDialogue() {
        customDialog = CustomDialog(
            getString(R.string.success),
            getString(R.string.drug_added_success),
            object : CustomDialog.ContentInterface {
                override fun onSendClicked() {
                    onClose()
                }
            },
            getString(R.string.success)
        )
        customDialog?.show(
            (activity as FragmentActivity).supportFragmentManager,
            "dialog"
        )
    }

    //closing the dialogue box
    private fun onClose() {
        customDialog?.dismiss()
        cleaFields()
        val navController = findNavController()
        navController.popBackStack()
        navController.navigate(R.id.navigation_home)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun cleaFields() {
        binding.progressBar.visibility = View.GONE
        binding.recyclerViewDrug.visibility = View.GONE
        binding.recyclerViewSuggestions.visibility = View.VISIBLE
        binding.editTextTextDrugName.text?.clear()
        maddAdapter.drugList.clear()
        msuggestionAdapter.drugList.clear()
        msuggestionAdapter.notifyItemChanged(0)
        msuggestionAdapter.notifyItemChanged(0)
    }

    @SuppressLint("CheckResult")
    private fun initViews() {
        try {
            val numeric: Boolean
            filterEditText()
            //for showing the keyboard when opens the screen
            appKeyBoard.showKey(binding.editTextTextDrugName)

            /**
             * Text watcher for edit text in native methode(without RXTextview)
             * will subscribe the emitted text from edit text.
             */
            binding.editTextTextDrugName.addTextWatcher()
                .filter { it.type == EditTextFlow.Type.ON }
                .map { it.query }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onNext = {
                        getSuggestion(it)
                    },
                    onError = {
                        it.printStackTrace()
                    }
                )
            /**
             * suggestion recyclerview and adapter initialization
             */
            binding.recyclerViewSuggestions.layoutManager = LinearLayoutManager(requireActivity())
            msuggestionAdapter = SuggestionAdapter(suggestionDrugs)
            binding.recyclerViewSuggestions.adapter = msuggestionAdapter
            /**
             * Drug list recyclerview and adapter initialization
             */
            binding.recyclerViewDrug.layoutManager = LinearLayoutManager(requireActivity())
            maddAdapter = AddAdapter(drugNames)// AddAdapter(drugNames)
            binding.recyclerViewDrug.adapter = maddAdapter
            /**
             * if we found some suggestions after searching suggestion recyclerview is visible.
             * and pass to drug details api
             */
            msuggestionAdapter.onItemClicked = { pos, suggestion ->
                selectedPosition = pos
                appKeyBoard.hideKeyboard(binding.editTextTextDrugName)
                if (!binding.editTextTextDrugName.text.isNullOrEmpty())
                    binding.editTextTextDrugName.text!!.clear()
                binding.editTextTextDrugName.setText(suggestion)
                binding.editTextTextDrugName.text?.let {
                    binding.editTextTextDrugName.setSelection(
                        it.length
                    )
                }
                binding.editTextTextDrugName.setSelectAllOnFocus(true)
                binding.editTextTextDrugName.requestFocus()
                binding.editTextTextDrugName.selectAll()
                /**
                 * the selected suggestion is passed to the view model and checking
                 * if any drugs available for this suggestion if we found any then observing _drugLiveData
                 *
                 */
                viewModel.getDrugList(suggestion)
            }
            /**
             * if we found drug then for adding this function is used.
             */
            maddAdapter.onAddClicked = { drugs, _ ->
                drug = drugs
                viewModel.getInteractionList(drugs.rxcui)

            }
            /**
             * this fragment gets invoked by scan results(barcode/text)
             * navArgs is used for fragment communication.
             * the result text is checked for numeric (Barcode) or for Text
             * if it is numeric then get barcode api is initiated.
             * else get drugs from approximate item api is called.
             */
            val args: SearchFragmentArgs by navArgs()
            scanedText = args.scannedText
            if (!scanedText.isNullOrEmpty()) {

                scanedText = scanedText.trim()
                appKeyBoard.hideKeyboard(binding.editTextTextDrugName)
                binding.editTextTextDrugName.setText(scanedText.replaceFirstChar { it.uppercaseChar() })
                binding.progressBar.visibility = View.VISIBLE
                suggestionTriggered = false
                numeric = scanedText.matches("-?\\d+(\\.\\d+)?".toRegex())
                scanedText = if (numeric) {

                    viewModel.getndcproperties(scanedText)
                    ""
                } else {
                    println(suggestionTriggered)
                    viewModel.getapproximateTerm(scanedText)
                    ""
                }
            }


        } catch (Ex: Exception) {

        }
    }

    /**
     * When the  user types on the Edit  text,Type event happens,
     * call onNext on the emitter and pass it the current text value of edit text .
     * which will subscribed by the subscriber.
     */
    private fun EditText.addTextWatcher(): Flowable<EditTextFlow> {
        return Flowable.create({ emitter ->
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    emitter.onNext(EditTextFlow(p0.toString(), EditTextFlow.Type.ON))
                }

                override fun afterTextChanged(p0: Editable?) {}
            })
        }, BackpressureStrategy.BUFFER)
    }

    /***
     * allowing only characters and space in keyboard and max search limit is 200
     */
    private fun filterEditText() {
        binding.editTextTextDrugName.filters = arrayOf(
            LengthFilter(SEARCH_LIMIT), InputFilter { source, start, end, _, _, _ ->
                for (i in start until end) {
                    if (!Character.isLetterOrDigit(source[i]) && !Character.isSpaceChar(source[i])) { // Accept only letter & digits ; otherwise just return
                        return@InputFilter ""
                    }
                }
                null
            }
        )

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showNoDrugsFound() {
        binding.errorMsgTV.visibility = View.VISIBLE
        suggestionTriggered = true
        binding.errorMsgTV.text = resources.getString(R.string.empty)
        binding.recyclerViewDrug.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        maddAdapter.notifyDataSetChanged()
    }

    //hide the drug  list and show suggestion list
    private fun hideDrugRecyclerView() {
        binding.recyclerViewDrug.visibility = View.GONE
        maddAdapter.drugList.clear()
        drugNames.clear()
        binding.recyclerViewSuggestions.visibility = View.VISIBLE
        msuggestionAdapter.drugList.clear()
        suggestionDrugs.clear()
    }

    //hide the suggestion list and show drug list
    private fun hideSuggestionRecyclerView() {
        binding.recyclerViewSuggestions.visibility = View.GONE
        msuggestionAdapter.drugList.clear()
        suggestionDrugs.clear()
        binding.recyclerViewDrug.visibility = View.VISIBLE
        maddAdapter.drugList.clear()
        drugNames.clear()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getSuggestion(s: String) {
        binding.recyclerViewDrug.visibility = View.GONE
        if (s.length > 2) {
            suggestionTriggered = false
            binding.errorMsgTV.visibility = View.GONE
            binding.recyclerViewSuggestions.visibility = View.VISIBLE
            viewModel.getSuggestionListRxKotlin(s)
        } else {
            msuggestionAdapter.drugList.clear()
            binding.recyclerViewSuggestions.visibility = View.GONE
            msuggestionAdapter.notifyDataSetChanged()
        }
    }


}