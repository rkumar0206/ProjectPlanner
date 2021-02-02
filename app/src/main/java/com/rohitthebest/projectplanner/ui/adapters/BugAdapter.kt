package com.rohitthebest.projectplanner.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rohitthebest.projectplanner.Constants.FALSE
import com.rohitthebest.projectplanner.R
import com.rohitthebest.projectplanner.databinding.AdapterBugReportLayoutBinding
import com.rohitthebest.projectplanner.db.entity.Bug
import com.rohitthebest.projectplanner.utils.*

class BugAdapter : ListAdapter<Bug, BugAdapter.BugViewHolder>(DiffUtilCallback()) {

    private var mListener: OnClickListener? = null

    inner class BugViewHolder(val binding: AdapterBugReportLayoutBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        fun setData(bug: Bug?) {

            bug?.let {

                binding.apply {

                    if (it.isResolved == FALSE) {

                        binding.root.strokeColor = ContextCompat.getColor(itemView.context, R.color.error_color)
                        bugDescriptionCB.text = it.bugDescription
                        binding.bugDescriptionCB.changeTextColor(itemView.context, R.color.primary_text_color)
                        bugDescriptionCB.isChecked = false
                    } else {

                        binding.root.strokeColor = ContextCompat.getColor(itemView.context, R.color.success_color)
                        bugDescriptionCB.strikeThrough(it.bugDescription)
                        binding.bugDescriptionCB.changeTextColor(itemView.context, R.color.secondary_text_color)
                        bugDescriptionCB.isChecked = true
                    }

                    if (it.possibleSolution != "") {

                        bugSolutionTV.show()
                        bugSolutionTV.boldSpan("Possible solution : ${it.possibleSolution}", 0, 16)
                    } else {

                        bugSolutionTV.hide()
                    }

                    addedOnTV.setDateInTextView(it.timestamp, "dd-MM-yyyy, hh:mm a", "Added on : ")
                }
            }
        }

        init {

            binding.bugDescriptionCB.setOnClickListener(this)
            binding.editBtn.setOnClickListener(this)
            binding.deleteBugBtn.setOnClickListener(this)
        }

        override fun onClick(v: View?) {

            if (checkForNullability(absoluteAdapterPosition)) {

                when (v?.id) {

                    binding.bugDescriptionCB.id -> {

                        mListener!!.onBugDescriptionCBClicked(
                                getItem(absoluteAdapterPosition),
                                absoluteAdapterPosition
                        )
                    }

                    binding.editBtn.id -> {

                        mListener!!.onEditBugBtnClicked(
                                getItem(absoluteAdapterPosition),
                                absoluteAdapterPosition
                        )

                    }

                    binding.deleteBugBtn.id -> {

                        mListener!!.onDeleteBugBtnClicked(
                                getItem(absoluteAdapterPosition),
                                absoluteAdapterPosition
                        )
                    }

                }
            }

        }

        private fun checkForNullability(position: Int): Boolean {

            return position != RecyclerView.NO_POSITION && mListener != null
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

        fun onBugDescriptionCBClicked(bug: Bug, position: Int)
        fun onEditBugBtnClicked(bug: Bug, position: Int)
        fun onDeleteBugBtnClicked(bug: Bug, position: Int)
    }

    fun setOnClickListener(listener: OnClickListener) {
        mListener = listener
    }
}
