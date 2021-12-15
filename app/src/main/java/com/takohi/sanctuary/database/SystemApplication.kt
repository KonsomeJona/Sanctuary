package com.takohi.sanctuary.database

import androidx.annotation.NonNull
import androidx.room.*

private const val TABLE_NAME: String = "system_application"

@Entity(tableName = TABLE_NAME)
data class SystemApplication(
        @PrimaryKey @NonNull @ColumnInfo(name = "package_name") val packageName: String,
        @ColumnInfo(name = "label") val label: String,
        @ColumnInfo(name = "enabled") var enabled: Boolean = false
)

@Dao
interface SystemApplicationDao {
    @Query("SELECT * FROM $TABLE_NAME ORDER BY label")
    suspend fun getAll(): List<SystemApplication>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(applications: List<SystemApplication>)

    @Update
    suspend fun update(application: SystemApplication)
}