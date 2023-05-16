package com.urbn.android.flickster

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.urbn.android.flickster.data.Character
import com.urbn.android.flickster.databinding.FragmentCharacterDetailBinding
import kotlinx.coroutines.GlobalScope

class CharacterDetailFragment : Fragment() {

    private lateinit var binding: FragmentCharacterDetailBinding
    private val item: Character by lazy {
        arguments!!.getParcelable<Character>(ARG_CHARACTER)!!
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentCharacterDetailBinding.inflate(inflater, container, false)

        return binding.root
    }


    companion object {
        const val ARG_CHARACTER = "character"
    }
}
