package io.stacrypt.stadroid.data

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

abstract class BaseListingPagedAdapter<T> :
    PagedListAdapter<T, RecyclerView.ViewHolder>(object : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
        // override fun areContentsTheSame(oldItem: T, newItem: T): Boolean = areContentsTheSame(oldItem, newItem)
                // override fun areItemsTheSame(oldItem: T, newItem: T): Boolean = areItemsTheSame(oldItem, newItem)
        }) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent, viewType)

    fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        if(item == null) (holder as ViewHolder).clear()
        else (holder as ViewHolder).bindTo(getItem(position))
    }

    abstract fun areContentsTheSame(oldItem: T, newItem: T): Boolean
    abstract fun areItemsTheSame(oldItem: T, newItem: T): Boolean
    abstract fun ViewHolder.bindTo(item: T?)
    abstract fun ViewHolder.clear()

    inner class ViewHolder(val parent: ViewGroup, layoutId: Int) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(layoutId, parent, false)
    ) {
        // fun bindTo(item: T?) = this@BaseListingPagedAdapter.bindTo(item)
        //
        // fun clear() = this@BaseListingPagedAdapter.clear()
    }
}
