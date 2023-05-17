package com.urbn.android.flickster.character.usecase

import arrow.core.Either
import kotlinx.coroutines.flow.Flow
import com.urbn.android.flickster.character.model.Character
import com.urbn.android.flickster.character.service.CharacterRepository
import com.urbn.android.flickster.utils.NetworkResult
import javax.inject.Inject

class GetCharactersUseCase @Inject constructor(private val characterRepository: CharacterRepository) {
    operator fun invoke(): Flow<NetworkResult<List<Character>>> = characterRepository.getCharacters("the+wire+characters")
}