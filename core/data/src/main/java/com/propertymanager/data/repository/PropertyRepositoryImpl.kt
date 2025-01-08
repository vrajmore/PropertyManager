package com.propertymanager.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.propertymanager.domain.model.Property
import com.propertymanager.domain.repository.PropertyRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PropertyRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : PropertyRepository {
    private val propertyCollection = firestore.collection("properties")

    override suspend fun getProperties(): List<Property> {
        val snapshot = propertyCollection.get().await()
        return snapshot.documents.mapNotNull { document ->
            try {
                document.toObject(Property::class.java)?.copy(id = document.id)
            } catch (e: Exception) {
                // Fallback manual conversion if automatic conversion fails
                val data = document.data ?: return@mapNotNull null
                val address = (data["address"] as? Map<*, *>)?.let { addressMap ->
                    Property.Address(
                        country = addressMap["country"] as? String ?: "",
                        state = addressMap["state"] as? String ?: "",
                        city = addressMap["city"] as? String ?: "",
                        society = addressMap["society"] as? String ?: "",
                        building = try {
                            Property.Building.valueOf((addressMap["building"] as? String ?: "FLAT").uppercase())
                        } catch (e: IllegalArgumentException) {
                            Property.Building.FLAT
                        },
                        flatNo = addressMap["flatNo"] as? String ?: ""
                    )
                } ?: Property.Address()

                Property(
                    id = document.id,
                    address = address,
                    ownerId = data["ownerId"] as? String ?: "",
                    currentTenantId = data["currentTenantId"] as? String ?: "",
                    maintenanceRequests = (data["maintenanceRequests"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                    createdAt = data["createdAt"] as? Timestamp
                )
            }
        }
    }

    override suspend fun addProperty(property: Property): String {
        val propertyMap = hashMapOf(
            "address" to hashMapOf(
                "country" to property.address.country,
                "state" to property.address.state,
                "city" to property.address.city,
                "society" to property.address.society,
                "building" to property.address.building.name,
                "flatNo" to property.address.flatNo
            ),
            "ownerId" to property.ownerId,
            "currentTenantId" to property.currentTenantId,
            "maintenanceRequests" to property.maintenanceRequests,
            "createdAt" to property.createdAt
        )

        val docRef = propertyCollection.add(propertyMap).await()
        return docRef.id
    }

    override suspend fun updateProperty(property: Property) {
        val propertyMap = hashMapOf(
            "address" to hashMapOf(
                "country" to property.address.country,
                "state" to property.address.state,
                "city" to property.address.city,
                "society" to property.address.society,
                "building" to property.address.building.name,
                "flatNo" to property.address.flatNo
            ),
            "ownerId" to property.ownerId,
            "currentTenantId" to property.currentTenantId,
            "maintenanceRequests" to property.maintenanceRequests,
            "createdAt" to property.createdAt
        )

        propertyCollection.document(property.id).set(propertyMap).await()
    }

    override suspend fun deleteProperty(propertyId: String) {
        propertyCollection.document(propertyId).delete().await()
    }

    override suspend fun getPropertyById(propertyId: String): Property? {
        val snapshot = propertyCollection.document(propertyId).get().await()
        return try {
            snapshot.toObject(Property::class.java)?.copy(id = snapshot.id)
        } catch (e: Exception) {
            // Fallback manual conversion
            val data = snapshot.data ?: return null
            val address = (data["address"] as? Map<*, *>)?.let { addressMap ->
                Property.Address(
                    country = addressMap["country"] as? String ?: "",
                    state = addressMap["state"] as? String ?: "",
                    city = addressMap["city"] as? String ?: "",
                    society = addressMap["society"] as? String ?: "",
                    building = try {
                        Property.Building.valueOf((addressMap["building"] as? String ?: "FLAT").uppercase())
                    } catch (e: IllegalArgumentException) {
                        Property.Building.FLAT
                    },
                    flatNo = addressMap["flatNo"] as? String ?: ""
                )
            } ?: Property.Address()

            Property(
                id = snapshot.id,
                address = address,
                ownerId = data["ownerId"] as? String ?: "",
                currentTenantId = data["currentTenantId"] as? String ?: "",
                maintenanceRequests = (data["maintenanceRequests"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                createdAt = data["createdAt"] as? Timestamp
            )
        }
    }

    override suspend fun addAddress(propertyId: String, address: Property.Address) {
        val addressMap = hashMapOf(
            "address" to hashMapOf(
                "country" to address.country,
                "state" to address.state,
                "city" to address.city,
                "society" to address.society,
                "building" to address.building.name,
                "flatNo" to address.flatNo
            )
        )
        propertyCollection.document(propertyId).set(addressMap, SetOptions.merge()).await()
    }

    override suspend fun deleteAddress(propertyId: String) {
        val clearedAddress = hashMapOf(
            "address" to hashMapOf(
                "country" to "",
                "state" to "",
                "city" to "",
                "society" to "",
                "building" to Property.Building.FLAT.name,
                "flatNo" to ""
            )
        )
        propertyCollection.document(propertyId).set(clearedAddress, SetOptions.merge()).await()
    }

    override suspend fun updateAddress(propertyId: String, address: Property.Address) {
        val addressMap = hashMapOf(
            "address" to hashMapOf(
                "country" to address.country,
                "state" to address.state,
                "city" to address.city,
                "society" to address.society,
                "building" to address.building.name,
                "flatNo" to address.flatNo
            )
        )
        propertyCollection.document(propertyId).set(addressMap, SetOptions.merge()).await()
    }
}

