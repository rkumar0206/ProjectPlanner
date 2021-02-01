package com.rohitthebest.projectplanner.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rohitthebest.projectplanner.Constants.SHOW_UI
import com.rohitthebest.projectplanner.databinding.AdapterFeatureLayoutBinding
import com.rohitthebest.projectplanner.db.entity.Feature
import com.rohitthebest.projectplanner.utils.boldSpan

class FeatureAdapter(var showOrHideUI: String = SHOW_UI) : ListAdapter<Feature, FeatureAdapter.FeatureViewHolder>(DiffUtilCallback()) {

    private var mListener: OnClickListener? = null

    inner class FeatureViewHolder(val binding: AdapterFeatureLayoutBinding) :
            RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun setData(feature: Feature?) {

            feature?.let {

                binding.featureNameTV.text = "${absoluteAdapterPosition + 1}. ${it.name}"

                if (it.description == "") {
                    binding.featureDescriptionTV.text = "no description added!!"
                } else {

                    binding.featureDescriptionTV
                            .boldSpan("Description : ${it.description}",
                                    0, 10
                            )

                          }

                          if (it.implementation == "") {

                              binding.featureImplentation.text = "no implementation added!!"
                          } else {

                              binding.featureImplentation.boldSpan(
                                      "Implementation : ${it.implementation}",
                                      0,
                                      13
                              )
                          }
            }
        }

        init {

            if (showOrHideUI == SHOW_UI) {

                binding.root.setOnClickListener {

                    if (checkForNullability(absoluteAdapterPosition)) {

                        mListener!!.onFeatureClicked(getItem(absoluteAdapterPosition), absoluteAdapterPosition)
                    }
                }
            }
        }

        private fun checkForNullability(position: Int): Boolean {

            return position != RecyclerView.NO_POSITION && mListener != null
        }

    }

    class DiffUtilCallback : DiffUtil.ItemCallback<Feature>() {

        override fun areItemsTheSame(oldItem: Feature, newItem: Feature): Boolean = oldItem == newItem

        override fun areContentsTheSame(oldItem: Feature, newItem: Feature): Boolean = oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeatureViewHolder {

        val binding = AdapterFeatureLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return FeatureViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FeatureViewHolder, position: Int) {

        holder.setData(getItem(position))
    }

    interface OnClickListener {

        fun onFeatureClicked(feature: Feature, position: Int)
    }

    fun setOnClickListener(listener: OnClickListener) {
        mListener = listener
    }
}
