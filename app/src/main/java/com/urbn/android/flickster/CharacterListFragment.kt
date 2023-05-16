package com.urbn.android.flickster

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.urbn.android.flickster.data.Character
import com.urbn.android.flickster.databinding.CharacterItemContentBinding
import com.urbn.android.flickster.data.PlaceholderContent.CHARACTERS
import com.urbn.android.flickster.databinding.FragmentCharacterListBinding

class CharacterListFragment : Fragment() {

    private lateinit var binding: FragmentCharacterListBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentCharacterListBinding.inflate(inflater, container, false)
        return binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = binding.itemList

        val adapter = CharacterAdapter()
        recyclerView.adapter = adapter
        adapter.submitList(CHARACTERS)
    }
}


class CharacterAdapter: ListAdapter<Character, CharacterViewHolder>(CharactersDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {

        val binding =
            CharacterItemContentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CharacterViewHolder(binding)
    }


    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        holder.onBind(currentList[position])
    }
}


class CharacterViewHolder(binding: CharacterItemContentBinding) :
    RecyclerView.ViewHolder(binding.root) {
    private val idView: TextView = binding.characterName


    fun onBind(character: Character) {
        idView.text = character.name

        itemView.setOnClickListener { itemView ->
            val bundle = Bundle()

            bundle.putParcelable(
                CharacterDetailFragment.ARG_CHARACTER,
                character
            )

            itemView.findNavController().navigate(R.id.show_character_detail, bundle)
        }
    }
}


object CharactersDiffUtil: DiffUtil.ItemCallback<Character>() {


    override fun areItemsTheSame(oldItem: Character, newItem: Character): Boolean {
       return oldItem.name == newItem.name
    }


    override fun areContentsTheSame(oldItem: Character, newItem: Character): Boolean {
        return oldItem.details == newItem.details &&
                oldItem.imageUrl == newItem.imageUrl &&
                oldItem.isFavorite == newItem.isFavorite
    }
}
