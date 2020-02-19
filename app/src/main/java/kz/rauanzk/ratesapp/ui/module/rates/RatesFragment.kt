package kz.rauanzk.ratesapp.ui.module.rates

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_rates.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kz.rauanzk.ratesapp.R
import kz.rauanzk.ratesapp.ui.module.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

private const val ONE_SECOND = 1000L

class RatesFragment : BaseFragment<RatesViewModel>() {

    override val viewModel: RatesViewModel by viewModel()

    private val ratesAdapter = RatesAdapter()
    private var ratesJob: Job? = null
    private var onEditJob: Job? = null
    private var verticalScrollListener: RecyclerView.OnScrollListener? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(context)
        ratesRV.layoutManager = layoutManager
        ratesRV.adapter = ratesAdapter
        ratesRV.setHasFixedSize(true)
        ratesRV.isMotionEventSplittingEnabled = false

        verticalScrollListener?.let {
            ratesRV.removeOnScrollListener(it)
        }

        verticalScrollListener = object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy != 0) {
                    toolbarShadow.visibility =
                        if (layoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
                            GONE
                        } else {
                            VISIBLE
                        }
                    onEditJob?.cancel()
                    stopFetchingRates()
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        startFetchingRates()
                    }
                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                        onEditJob?.cancel()
                        stopFetchingRates()
                    }
                }
            }
        }.also {
            ratesRV.addOnScrollListener(it)
        }


        ratesAdapter.setOnItemClick { rateUiModel, pos ->
            Timber.d("clicked rateUiModel=$rateUiModel, pos=$pos")

            launch {
                stopFetchingRates()
                viewModel.updateCurrentBase(rateUiModel.name)
                ratesAdapter.moveItemToTop(rateUiModel, pos)
                delay(ONE_SECOND)
                startFetchingRates()
            }
        }

        ratesAdapter.setOnEdit { rateUiModel, value ->
            onEditJob?.cancel()
            onEditJob = launch {
                stopFetchingRates()
                ratesAdapter.setBaseValue(rateUiModel, value)
                delay(ONE_SECOND)
                startFetchingRates()
            }
        }

        tryAgain.setOnClickListener {
            viewModel.showErrorLayout.value = false
            startFetchingRates()
        }

        observeViewModel()
    }

    override fun onStart() {
        super.onStart()
        startFetchingRates()
    }

    override fun onStop() {
        super.onStop()
        stopFetchingRates()
    }

    private fun observeViewModel() {
        viewModel.updateList.observe(viewLifecycleOwner, Observer { event ->
            event.getContentIfNotHandled()?.let {
                ratesAdapter.update(viewModel.rateUiModels)
            }
        })
        viewModel.showErrorLayout.observe(viewLifecycleOwner, Observer { show ->
            if (show) {
                stopFetchingRates()
            }
            errorLayout.visibility = if (show) VISIBLE else GONE
        })
        viewModel.loading.observe(viewLifecycleOwner, Observer { isLoading ->
            loadingLayout.visibility = if (isLoading) VISIBLE else GONE
        })
    }

    private fun startFetchingRates() {
        ratesJob?.cancel()
        ratesJob = launch {
            while (true) {
                viewModel.fetchLatestRates()
                delay(ONE_SECOND)
            }
        }
    }

    private fun stopFetchingRates() {
        viewModel.updateListJob?.cancel()
        ratesJob?.cancel()
    }

    override fun getLayoutRes(): Int = R.layout.fragment_rates
}