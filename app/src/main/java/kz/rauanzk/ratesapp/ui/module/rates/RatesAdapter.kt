package kz.rauanzk.ratesapp.ui.module.rates

import android.text.InputFilter
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_rate.view.*
import kz.rauanzk.ratesapp.App
import kz.rauanzk.ratesapp.R
import kz.rauanzk.ratesapp.ui.module.rates.uimodel.RateUiModel
import kz.rauanzk.ratesapp.utils.DecimalDigitsInputFilter
import kz.rauanzk.ratesapp.utils.format
import kz.rauanzk.ratesapp.utils.putCursorAtEnd
import timber.log.Timber


class RatesAdapter : RecyclerView.Adapter<RatesAdapter.RateViewHolder>() {

    private var items = arrayListOf<RateUiModel>()

    private var baseValue: Float = 1F

    private var onItemClickCallback: ((RateUiModel, Int) -> Unit)? = null
    private var onEditCallback: ((RateUiModel, Float) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RateViewHolder {
        return RateViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_rate,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(
        holder: RateViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty()) {
            holder.bind(items[position])
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun onBindViewHolder(holder: RateViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun setOnItemClick(callback: (RateUiModel, Int) -> Unit) {
        onItemClickCallback = callback
    }

    fun setOnEdit(callback: (RateUiModel, Float) -> Unit) {
        onEditCallback = callback
    }

    fun setBaseValue(item: RateUiModel, value: Float) {
        Timber.d("setBaseValue, value=$value")
        item.value = value
        baseValue = value
        notifyItemRangeChanged(1, items.size - 1, value)
    }

    fun moveItemToTop(item: RateUiModel, pos: Int) {
        baseValue = item.value
        item.type = RateUiModel.TYPE_BASE
        items[0].type = RateUiModel.TYPE_NORMAL

        items.removeAt(pos)
        items.add(0, item)
        notifyItemMoved(pos, 0)
    }

    fun update(items: ArrayList<RateUiModel>) {
        this.items = items
        notifyDataSetChanged()
    }

    inner class RateViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(rateUiModel: RateUiModel) {
            view.name.text = rateUiModel.name
            view.description.text = rateUiModel.description
            view.icon.setImageResource(rateUiModel.iconRes)

            fun doOnClick() {
                if (rateUiModel.isBase || !App.isOnline()) {
                    return
                }

                view.valueET.apply {
                    isEnabled = true
                    requestFocus()
                    putCursorAtEnd()
                }

                onItemClickCallback?.invoke(
                    rateUiModel.also { it.value = baseValue * it.value },
                    layoutPosition
                )
            }

            view.valueET.apply {
                isEnabled = rateUiModel.isBase
                inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
                filters = arrayOf<InputFilter>(DecimalDigitsInputFilter())

                tag = "programmatically"
                setText(
                    if (!rateUiModel.isBase) {
                        (rateUiModel.value * baseValue).format()
                    } else {
                        rateUiModel.value.format()
                    }
                )
                tag = null

                putCursorAtEnd()

                if (rateUiModel.isBase && layoutPosition == 0) {
                    doOnTextChanged { text, _, _, _ ->
                        val floatValue = text.toString().toFloatOrNull() ?: 0F
                        if (floatValue != rateUiModel.value && tag == null) {
                            onEditCallback?.invoke(rateUiModel, floatValue)
                        }
                    }
                }
            }

            view.setOnClickListener {
                doOnClick()
            }
        }
    }
}