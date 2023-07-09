package com.joekomputer.android.mvifun.character.usecase

import kotlinx.coroutines.flow.Flow
import com.joekomputer.android.mvifun.character.model.Character
import com.joekomputer.android.mvifun.character.service.CharacterRepository
import com.joekomputer.android.mvifun.utils.NetworkResult
import javax.inject.Inject

class GetCharactersUseCase @Inject constructor(private val characterRepository: CharacterRepository) {
    operator fun invoke(): Flow<NetworkResult<List<Character>>> = characterRepository.getCharacters("the+wire+characters")
}