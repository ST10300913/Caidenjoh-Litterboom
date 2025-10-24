package com.example.litterboom.data.firebase

import com.example.litterboom.data.ItemPhoto
import com.example.litterboom.data.LoggingField
import com.example.litterboom.data.SubCategoryField
import com.example.litterboom.data.WasteCategory
import com.example.litterboom.data.WasteDao
import com.example.litterboom.data.WasteSubCategory
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import android.net.Uri
import com.google.firebase.storage.ktx.component1
import com.google.firebase.storage.ktx.component2
import kotlinx.coroutines.channels.awaitClose

class FirestoreWasteDao : WasteDao {

    private val db = FirebaseModule.db
    private val storage = FirebaseModule.storage

    private val categoriesCol = db.collection("waste_categories")
    private val subCategoriesCol = db.collection("waste_subcategories")
    private val fieldsCol = db.collection("logging_fields")
    private val joinCol = db.collection("subcategory_fields")
    private val photosCol = db.collection("item_photos")

    // -------- Category --------

    override suspend fun insertCategory(category: WasteCategory): Long {
        val id = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
        categoriesCol.document(id.toString()).set(
            mapOf("id" to id, "name" to category.name, "isActive" to category.isActive)
        ).await()
        return id.toLong()
    }

    override suspend fun updateCategory(category: WasteCategory) {
        val id = category.id.takeIf { it != 0 }?.toString() ?: return
        categoriesCol.document(id).update(
            mapOf("name" to category.name, "isActive" to category.isActive)
        ).await()
    }

    override suspend fun getAllCategories(): List<WasteCategory> {
        val snap = categoriesCol.orderBy("name", Query.Direction.ASCENDING).get().await()
        return snap.documents.map { d ->
            WasteCategory(
                id = (d.getLong("id") ?: 0L).toInt(),
                name = d.getString("name") ?: "",
                isActive = d.getBoolean("isActive") ?: true
            )
        }
    }

    override suspend fun getActiveCategories(): List<WasteCategory> {
        val snap = categoriesCol.whereEqualTo("isActive", true)
            .orderBy("name", Query.Direction.ASCENDING).get().await()
        return snap.documents.map { d ->
            WasteCategory(
                id = (d.getLong("id") ?: 0L).toInt(),
                name = d.getString("name") ?: "",
                isActive = true
            )
        }
    }

    override suspend fun getCategoryByName(name: String): WasteCategory? {
        val snap = categoriesCol.whereEqualTo("name", name).limit(1).get().await()
        val d = snap.documents.firstOrNull() ?: return null
        return WasteCategory(
            id = (d.getLong("id") ?: 0L).toInt(),
            name = d.getString("name") ?: "",
            isActive = d.getBoolean("isActive") ?: true
        )
    }

    // -------- SubCategory --------

    override suspend fun insertSubCategory(subCategory: WasteSubCategory): Long {
        val id = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
        subCategoriesCol.document(id.toString()).set(
            mapOf(
                "id" to id,
                "name" to subCategory.name,
                "categoryId" to subCategory.categoryId,
                "isActive" to subCategory.isActive
            )
        ).await()
        return id.toLong()
    }

    override suspend fun updateSubCategory(subCategory: WasteSubCategory) {
        val id = subCategory.id.takeIf { it != 0 }?.toString() ?: return
        subCategoriesCol.document(id).update(
            mapOf(
                "name" to subCategory.name,
                "categoryId" to subCategory.categoryId,
                "isActive" to subCategory.isActive
            )
        ).await()
    }

    override suspend fun getSubCategoriesForCategory(categoryId: Int): List<WasteSubCategory> {
        val snap = subCategoriesCol.whereEqualTo("categoryId", categoryId)
            .orderBy("name", Query.Direction.ASCENDING).get().await()
        return snap.documents.map { d ->
            WasteSubCategory(
                id = (d.getLong("id") ?: 0L).toInt(),
                name = d.getString("name") ?: "",
                categoryId = (d.getLong("categoryId") ?: 0L).toInt(),
                isActive = d.getBoolean("isActive") ?: true
            )
        }
    }

    override suspend fun getActiveSubCategoriesForCategory(categoryId: Int): List<WasteSubCategory> {
        val snap = subCategoriesCol.whereEqualTo("categoryId", categoryId)
            .whereEqualTo("isActive", true)
            .orderBy("name", Query.Direction.ASCENDING).get().await()
        return snap.documents.map { d ->
            WasteSubCategory(
                id = (d.getLong("id") ?: 0L).toInt(),
                name = d.getString("name") ?: "",
                categoryId = (d.getLong("categoryId") ?: 0L).toInt(),
                isActive = true
            )
        }
    }

    override suspend fun getSubCategoryByName(name: String, categoryId: Int): WasteSubCategory? {
        val snap = subCategoriesCol
            .whereEqualTo("categoryId", categoryId)
            .whereEqualTo("name", name)
            .limit(1).get().await()
        val d = snap.documents.firstOrNull() ?: return null
        return WasteSubCategory(
            id = (d.getLong("id") ?: 0L).toInt(),
            name = d.getString("name") ?: "",
            categoryId = (d.getLong("categoryId") ?: 0L).toInt(),
            isActive = d.getBoolean("isActive") ?: true
        )
    }

    // -------- Logging Fields --------

    override suspend fun insertLoggingField(field: LoggingField): Long {
        val id = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
        fieldsCol.document(id.toString()).set(
            mapOf("id" to id, "fieldName" to field.fieldName, "isActive" to field.isActive)
        ).await()
        return id.toLong()
    }

    override suspend fun updateField(field: LoggingField) {
        val id = field.id.takeIf { it != 0 }?.toString() ?: return
        fieldsCol.document(id).update(
            mapOf("fieldName" to field.fieldName, "isActive" to field.isActive)
        ).await()
    }

    override suspend fun getAllLoggingFields(): List<LoggingField> {
        val snap = fieldsCol.orderBy("fieldName").get().await()
        return snap.documents.map { d ->
            LoggingField(
                id = (d.getLong("id") ?: 0L).toInt(),
                fieldName = d.getString("fieldName") ?: "",
                isActive = d.getBoolean("isActive") ?: true
            )
        }
    }

    override suspend fun getActiveLoggingFields(): List<LoggingField> {
        val snap = fieldsCol.whereEqualTo("isActive", true).orderBy("fieldName").get().await()
        return snap.documents.map { d ->
            LoggingField(
                id = (d.getLong("id") ?: 0L).toInt(),
                fieldName = d.getString("fieldName") ?: "",
                isActive = true
            )
        }
    }

    override suspend fun getFieldByName(name: String): LoggingField? {
        val snap = fieldsCol.whereEqualTo("fieldName", name).limit(1).get().await()
        val d = snap.documents.firstOrNull() ?: return null
        return LoggingField(
            id = (d.getLong("id") ?: 0L).toInt(),
            fieldName = d.getString("fieldName") ?: "",
            isActive = d.getBoolean("isActive") ?: true
        )
    }

    // -------- Relationships --------

    override suspend fun assignFieldToSubCategory(join: SubCategoryField) {
        val id = "${join.subCategoryId}-${join.fieldId}"
        joinCol.document(id).set(
            mapOf("subCategoryId" to join.subCategoryId, "fieldId" to join.fieldId)
        ).await()
    }

    override suspend fun getFieldsForSubCategory(subCategoryId: Int): List<LoggingField> {
        val links = joinCol.whereEqualTo("subCategoryId", subCategoryId).get().await()
        val fieldIds = links.documents.mapNotNull { it.getLong("fieldId")?.toInt() }.toSet()
        if (fieldIds.isEmpty()) return emptyList()

        // Firestore doesn't support IN with >10 items well; this is fine for small sets
        val all = fieldsCol.get().await()
        return all.documents.mapNotNull { d ->
            val id = (d.getLong("id") ?: 0L).toInt()
            if (id in fieldIds) LoggingField(
                id = id,
                fieldName = d.getString("fieldName") ?: "",
                isActive = d.getBoolean("isActive") ?: true
            ) else null
        }
    }

    override suspend fun isFieldAssignedToSubCategory(subCategoryId: Int, fieldId: Int): Int {
        val snap = joinCol
            .whereEqualTo("subCategoryId", subCategoryId)
            .whereEqualTo("fieldId", fieldId)
            .limit(1).get().await()
        return if (snap.isEmpty) 0 else 1
    }

    // -------- Photos (Flow) --------

    override suspend fun insertPhoto(photo: ItemPhoto) {
        // Upload to Storage if uri is a local content Uri; else assume it's already a download URL.
        val uri = Uri.parse(photo.uri)
        val isRemote = photo.uri.startsWith("http")

        val url = if (isRemote) {
            photo.uri
        } else {
            val path = "subcategories/${photo.subCategoryId}/photos/${System.currentTimeMillis()}.jpg"
            val ref = storage.reference.child(path)
            ref.putFile(uri).await()
            ref.downloadUrl.await().toString()
        }

        val id = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
        photosCol.document(id.toString()).set(
            mapOf(
                "id" to id,
                "subCategoryId" to photo.subCategoryId,
                "uri" to url,
                "takenAt" to photo.takenAt
            )
        ).await()
    }

    override fun photosFor(subCategoryId: Int): Flow<List<ItemPhoto>> = callbackFlow {
        val reg = photosCol
            .whereEqualTo("subCategoryId", subCategoryId)
            .orderBy("takenAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snap?.documents?.mapNotNull { d ->
                    ItemPhoto(
                        id = (d.getLong("id") ?: 0L).toInt(),
                        subCategoryId = (d.getLong("subCategoryId") ?: 0L).toInt(),
                        uri = d.getString("uri") ?: return@mapNotNull null,
                        takenAt = d.getLong("takenAt") ?: System.currentTimeMillis()
                    )
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { reg.remove() }
    }
}
