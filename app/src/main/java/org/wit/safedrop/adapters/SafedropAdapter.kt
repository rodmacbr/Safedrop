package org.wit.safedrop.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import org.wit.safedrop.databinding.CardSafedropBinding
import org.wit.safedrop.models.SafedropModel

interface SafedropListener {
    fun onSafedropClick(safedrop: SafedropModel, position : Int)
}

class SafedropAdapter constructor(private var safedrops: List<SafedropModel>,
                                   private val listener: SafedropListener) :
        RecyclerView.Adapter<SafedropAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val binding = CardSafedropBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)

        return MainHolder(binding)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val safedrop = safedrops[holder.adapterPosition]
        holder.bind(safedrop, listener)
    }

    override fun getItemCount(): Int = safedrops.size

    class MainHolder(private val binding : CardSafedropBinding) :
            RecyclerView.ViewHolder(binding.root) {

        fun bind(safedrop: SafedropModel, listener: SafedropListener) {
            binding.safedropTitle.text = safedrop.title
            binding.description.text = safedrop.description
            Picasso.get().load(safedrop.image).resize(200,200).into(binding.imageIcon)
            binding.root.setOnClickListener { listener.onSafedropClick(safedrop,adapterPosition) }
        }
    }
}
