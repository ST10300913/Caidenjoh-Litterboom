package com.example.litterboom.data.api

import com.example.litterboom.data.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class ApiWasteDao : WasteDao {

    // Category
    override suspend fun insertCategory(category: WasteCategory): Long {
        ApiClient.apiService.createWasteCategory(category)
        return 0L
    }
    override suspend fun updateCategory(category: WasteCategory) {
        val response = ApiClient.apiService.updateWasteCategory(category.id, category)
        if (!response.isSuccessful) {
            throw Exception("Failed to update category: ${response.message()}")
        }
    }
    override suspend fun getAllCategories(): List<WasteCategory> = ApiClient.apiService.getWasteCategories()
    override suspend fun getActiveCategories(): List<WasteCategory> = ApiClient.apiService.getActiveWasteCategories()
    override suspend fun getCategoryByName(name: String): WasteCategory? = null

    // SubCategory
    override suspend fun insertSubCategory(subCategory: WasteSubCategory): Long {
        val created = ApiClient.apiService.createWasteSubCategory(subCategory)
        return created.id.toLong()
    }
    override suspend fun updateSubCategory(subCategory: WasteSubCategory) {
        val response = ApiClient.apiService.updateWasteSubCategory(subCategory.id, subCategory)
        if (!response.isSuccessful) {
            throw Exception("Failed to update subcategory: ${response.message()}")
        }
    }
    override suspend fun getSubCategoriesForCategory(categoryId: Int): List<WasteSubCategory> = ApiClient.apiService.getWasteSubCategories(categoryId)
    override suspend fun getActiveSubCategoriesForCategory(categoryId: Int): List<WasteSubCategory> = ApiClient.apiService.getActiveWasteSubCategories(categoryId)
    override suspend fun getSubCategoryByName(name: String, categoryId: Int): WasteSubCategory? = null

    // Logging Fields
    override suspend fun insertLoggingField(field: LoggingField): Long {
        val created = ApiClient.apiService.createLoggingField(field)
        return created.id.toLong()
    }
    override suspend fun updateField(field: LoggingField) {
        val response = ApiClient.apiService.updateLoggingField(field.id, field)
        if (!response.isSuccessful) {
            throw Exception("Failed to update field: ${response.message()}")
        }
    }
    override suspend fun getAllLoggingFields(): List<LoggingField> = ApiClient.apiService.getLoggingFields()
    override suspend fun getActiveLoggingFields(): List<LoggingField> = ApiClient.apiService.getActiveLoggingFields()
    override suspend fun getFieldByName(name: String): LoggingField? = null

    // Relationships
    override suspend fun assignFieldToSubCategory(join: SubCategoryField) {
        ApiClient.apiService.createSubCategoryField(join)
    }
    override suspend fun getFieldsForSubCategory(subCategoryId: Int): List<LoggingField> = ApiClient.apiService.getFieldsForSubCategory(subCategoryId)
    override suspend fun isFieldAssignedToSubCategory(subCategoryId: Int, fieldId: Int): Int {
        val fields = getFieldsForSubCategory(subCategoryId)
        return if (fields.any { it.id == fieldId }) 1 else 0
    }

    // Photos
    override suspend fun insertPhoto(photo: ItemPhoto) = Unit
    override fun photosFor(subCategoryId: Int): Flow<List<ItemPhoto>> = flowOf(emptyList())
}
