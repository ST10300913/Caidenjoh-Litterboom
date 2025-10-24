package com.example.litterboom.data

import kotlinx.coroutines.flow.Flow

interface WasteDao {
    // Category
    suspend fun insertCategory(category: WasteCategory): Long
    suspend fun updateCategory(category: WasteCategory)
    suspend fun getAllCategories(): List<WasteCategory>
    suspend fun getActiveCategories(): List<WasteCategory>
    suspend fun getCategoryByName(name: String): WasteCategory?

    // SubCategory
    suspend fun insertSubCategory(subCategory: WasteSubCategory): Long
    suspend fun updateSubCategory(subCategory: WasteSubCategory)
    suspend fun getSubCategoriesForCategory(categoryId: Int): List<WasteSubCategory>
    suspend fun getActiveSubCategoriesForCategory(categoryId: Int): List<WasteSubCategory>
    suspend fun getSubCategoryByName(name: String, categoryId: Int): WasteSubCategory?

    // Logging Fields
    suspend fun insertLoggingField(field: LoggingField): Long
    suspend fun updateField(field: LoggingField)
    suspend fun getAllLoggingFields(): List<LoggingField>
    suspend fun getActiveLoggingFields(): List<LoggingField>
    suspend fun getFieldByName(name: String): LoggingField?

    // Relationships
    suspend fun assignFieldToSubCategory(join: SubCategoryField)
    suspend fun getFieldsForSubCategory(subCategoryId: Int): List<LoggingField>
    suspend fun isFieldAssignedToSubCategory(subCategoryId: Int, fieldId: Int): Int

    // Photos
    suspend fun insertPhoto(photo: ItemPhoto)
    fun photosFor(subCategoryId: Int): Flow<List<ItemPhoto>>
}
