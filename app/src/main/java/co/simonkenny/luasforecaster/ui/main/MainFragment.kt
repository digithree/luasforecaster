package co.simonkenny.luasforecaster.ui.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import co.simonkenny.luasforecaster.api.PublicInfoDisplaySource
import co.simonkenny.luasforecaster.databinding.MainFragmentBinding

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private val publicInfoDisplaySource = PublicInfoDisplaySource.getInstance()

    private lateinit var binding: MainFragmentBinding

    private val adapter = TramsAdapter()

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(requireActivity().viewModelStore, object: ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                if (modelClass == MainViewModel::class.java) {
                    return MainViewModel(publicInfoDisplaySource) as T
                }
                throw IllegalArgumentException("Cannot create ViewMode of class ${modelClass.canonicalName}")
            }
        }).get(MainViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = MainFragmentBinding.inflate(LayoutInflater.from(requireContext()), container, false).apply {
            viewModel = this@MainFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            with(rvTrams) {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                adapter = this@MainFragment.adapter
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // fetch on start up, further fetches made using refresh button, bound to ViewModel in layout XML
        viewModel.fetchStopInfo()

        with(viewModel) {
            tramsDataList.observe(viewLifecycleOwner) { tramsList ->
                tramsList.forEach { Log.d("DEBUG", "Tram:: destination: ${it.destination}, dueMins: ${it.dueMins}") }
                adapter.submitList(tramsList)
            }

            alertText.observe(viewLifecycleOwner) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

}