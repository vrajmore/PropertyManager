package com.propertymanager.domain.usecase.location.society

import com.propertymanager.domain.repository.LocationRepository
import javax.inject.Inject

class DeleteSocietyUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(cityId: Int, societyId: Int): Result<Unit> =
        repository.deleteSociety(cityId, societyId)
}
