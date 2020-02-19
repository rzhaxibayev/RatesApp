package kz.rauanzk.ratesapp.ui.module.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

abstract class BaseFragment<VM : BaseViewModel> : Fragment(), CoroutineScope {

    private var job: Job = Job()

    abstract val viewModel: VM

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    @LayoutRes
    abstract fun getLayoutRes(): Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(getLayoutRes(), container, false)
        init()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.showToastEvent.observe(viewLifecycleOwner, Observer { event ->
            event.getContentIfNotHandled()?.let { message ->
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    open fun init() {}

    fun navigate(@IdRes navViewId: Int, action: NavDirections) {
        try {
            val activity = activity as BaseActivity

            val navigationInfo =
                "navigate: navViewId=${activity.resources.getResourceEntryName(navViewId)}, " +
                        "action={actionId=${activity.resources.getResourceEntryName(action.actionId)}, " +
                        "args=${action.arguments}}"

            Timber.d(navigationInfo)

            val navController = Navigation.findNavController(activity, navViewId)
            navController.navigate(action)
        } catch (e: Exception) {
            Timber.e(e)
        }
    }
}