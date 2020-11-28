package co.simonkenny.luasforecaster.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import co.simonkenny.luasforecaster.R
import co.simonkenny.luasforecaster.api.Tram
import co.simonkenny.luasforecaster.databinding.ViewTramBinding
import java.lang.NumberFormatException

class TramsAdapter: ListAdapter<Tram, TramsAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<Tram>() {
        override fun areItemsTheSame(oldItem: Tram, newItem: Tram): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: Tram, newItem: Tram): Boolean =
            oldItem.destination == newItem.destination && oldItem.dueMins == newItem.dueMins
    }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ViewTramBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        with(getItem(position)) {
            with(holder.binding) {
                val resources = tvDueContent.context.resources
                tvDestinationContent.text = destination
                tvDueContent.text = dueMins?.let {
                    try {
                        Integer.parseInt(it).let { num ->
                            "$num ${resources.getQuantityString(R.plurals.minutes_short, num)}"
                        }
                    } catch (e: NumberFormatException) {
                        it
                    }
                } ?: resources.getString(R.string.unavailable)
            }
        }

    class ViewHolder(val binding: ViewTramBinding): RecyclerView.ViewHolder(binding.root)
}