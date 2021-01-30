package com.rohitthebest.projectplanner.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rohitthebest.projectplanner.Constants.SHOW_UI
import com.rohitthebest.projectplanner.databinding.AdapterStringLayoutBinding
import com.rohitthebest.projectplanner.utils.hide

class StringAdapter(var showOrHideView: String = SHOW_UI) : ListAdapter<String, StringAdapter.SkillsViewHolder>(DiffUtilCallback()) {

    private var mListener: OnClickListener? = null

    inner class SkillsViewHolder(val binding: AdapterStringLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun setData(text: String?) {

            text?.let {

                binding.textView5.text = it
                binding.stringNumberTV.text = "${absoluteAdapterPosition + 1}. "
            }
        }

        init {

            if (showOrHideView != SHOW_UI) {

                binding.deleteStringBtn.hide()
            } else {

                binding.root.setOnClickListener {

                    if (checkForNullability(absoluteAdapterPosition)) {

                        mListener!!.onStringClicked(getItem(absoluteAdapterPosition), absoluteAdapterPosition)
                    }
                }

                binding.deleteStringBtn.setOnClickListener {

                    if (checkForNullability(absoluteAdapterPosition)) {

                        mListener!!.onDeleteStringClicked(getItem(absoluteAdapterPosition)!!, absoluteAdapterPosition)
                    }
                }
            }
        }

        private fun checkForNullability(position: Int): Boolean {

            return position != RecyclerView.NO_POSITION && mListener != null
        }
    }

    class DiffUtilCallback : DiffUtil.ItemCallback<String>() {

        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean = oldItem == newItem

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean = oldItem == newItem

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkillsViewHolder {

        val binding = AdapterStringLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return SkillsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SkillsViewHolder, position: Int) {

        holder.setData(getItem(position))
    }

    interface OnClickListener {

        fun onStringClicked(text: String?, position: Int)
        fun onDeleteStringClicked(text: String ,position: Int)
    }

    fun setOnClickListener(listener: OnClickListener) {
        mListener = listener
    }
}
