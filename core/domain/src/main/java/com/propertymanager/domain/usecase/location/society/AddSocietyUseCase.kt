package com.propertymanager.domain.usecase.location.society

import com.propertymanager.domain.model.location.Society
import com.propertymanager.domain.repository.LocationRepository
import javax.inject.Inject

class AddSocietyUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(cityId: Int, society: Society): Result<Unit> =
        repository.addSociety(cityId, society)
}
