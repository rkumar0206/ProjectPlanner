package com.rohitthebest.projectplanner.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rohitthebest.projectplanner.Constants.FALSE
import com.rohitthebest.projectplanner.databinding.AdapterBugReportLayoutBinding
import com.rohitthebest.projectplanner.db.entity.Bug
import com.rohitthebest.projectplanner.utils.boldSpan
import com.rohitthebest.projectplanner.utils.hide
import com.rohitthebest.projectplanner.utils.show
import com.rohitthebest.projectplanner.utils.strikeThrough

class BugAdapter : ListAdapter<Bug, BugAdapter.BugViewHolder>(DiffUtilCallback()) {

    private var mListener: OnClickListener? = null

    inner class BugViewHolder(val binding: AdapterBugReportLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        fun setData(bug: Bug?) {

            bug?.let {

                binding.apply {

                    if (it.isResolved == FALSE) {

                        bugDescriptionCB.text = it.bugDescription
                        bugDescriptionCB.isChecked = false
                    } else {

                        bugDescriptionCB.strikeThrough(it.bugDescription)
                        bugDescriptionCB.isChecked = false
                    }

                    if (bugSolutionTV.text != "") {

                        bugSolutionTV.show()
                        bugSolutionTV.boldSpan("Solution : ${it.possibleSolution}", 0, 6)
                    } else {

                        bugSolutionTV.hide()
                    }

                }
            }
        }
    }

    class DiffUtilCallback : DiffUtil.ItemCallback<Bug>() {

        override fun areItemsTheSame(oldItem: Bug, newItem: Bug): Boolean = oldItem.bugKey == newItem.bugKey

        override fun areContentsTheSame(oldItem: Bug, newItem: Bug): Boolean = oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BugViewHolder {

        val binding = AdapterBugReportLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return BugViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BugViewHolder, position: Int) {

        holder.setData(getItem(position))
    }

    interface OnClickListener {

        fun onItemClick(bug: Bug)
    }

    fun setOnClickListener(listener: OnClickListener) {
        mListener = listener
    }
}
