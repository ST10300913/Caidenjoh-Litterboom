package com.example.litterboom.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WasteDao {
    @Insert
    suspend fun insertCategory(category: WasteCategory)

    @Insert
    suspend fun insertSubCategory(subCategory: WasteSubCategory)

    @Query("SELECT * FROM waste_categories ORDER BY name ASC")
    suspend fun getAllCategories(): List<WasteCategory>

    @Query("SELECT * FROM waste_subcategories WHERE categoryId = :categoryId ORDER BY name ASC")
    suspend fun getSubCategoriesForCategory(categoryId: Int): List<WasteSubCategory>

    @Insert
    suspend fun insertLoggingField(field: LoggingField)

    @Query("SELECT * FROM logging_fields ORDER BY fieldName ASC")
    suspend fun getAllLoggingFields(): List<LoggingField>

    @Insert
    suspend fun assignFieldToSubCategory(join: SubCategoryField)

    @Query("""
        SELECT lf.* FROM logging_fields lf
        INNER JOIN subcategory_fields scf ON lf.id = scf.fieldId
        WHERE scf.subCategoryId = :subCategoryId
    """)
    suspend fun getFieldsForSubCategory(subCategoryId: Int): List<LoggingField>

    // --- Photos ---
    @Insert
    suspend fun insertPhoto(photo: ItemPhoto)

    @Query("SELECT * FROM item_photos WHERE subCategoryId = :subCategoryId ORDER BY takenAt DESC")
    fun photosFor(subCategoryId: Int): Flow<List<ItemPhoto>>
}
