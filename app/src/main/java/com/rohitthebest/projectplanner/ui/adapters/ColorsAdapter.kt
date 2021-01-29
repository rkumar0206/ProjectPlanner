package com.rohitthebest.projectplanner.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rohitthebest.projectplanner.databinding.AdapterColorsLayoutBinding
import com.rohitthebest.projectplanner.db.entity.Colors

class ColorsAdapter : ListAdapter<Colors, ColorsAdapter.ColorsViewHolder>(DiffUtilCallback()) {

    private var mListener: OnClickListener? = null

    inner class ColorsViewHolder(val binding: AdapterColorsLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        fun setData(color: Colors?) {

            color?.let {

                binding.colorNameTV.text = color.colorName
                binding.colorHexCodeTV.text = color.colorHexCode
                binding.viewToBeColored.setBackgroundColor(Color.parseColor(color.colorHexCode))
            }
        }

        init {

            binding.root.setOnClickListener {

                if (checkForNullability(absoluteAdapterPosition)) {

                    mListener!!.onColorClicked(
                            getItem(absoluteAdapterPosition),
                            absoluteAdapterPosition
                    )
                }
            }
        }

        private fun checkForNullability(position: Int): Boolean {

            return position != RecyclerView.NO_POSITION && mListener != null
        }

    }

    class DiffUtilCallback : DiffUtil.ItemCallback<Colors>() {

        override fun areItemsTheSame(oldItem: Colors, newItem: Colors): Boolean = oldItem == newItem

        override fun areContentsTheSame(oldItem: Colors, newItem: Colors): Boolean = oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorsViewHolder {

        val binding = AdapterColorsLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ColorsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ColorsViewHolder, position: Int) {

        holder.setData(getItem(position))
    }

    interface OnClickListener {

        fun onColorClicked(color: Colors, position: Int)
    }

    fun setOnClickListener(listener: OnClickListener) {
        mListener = listener
    }
}
