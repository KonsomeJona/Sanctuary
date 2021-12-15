package com.takohi.sanctuary.database

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.annotation.NonNull
import androidx.room.*

private const val TABLE_NAME: String = "application"

@Entity(tableName = TABLE_NAME)
data class Application(
    @PrimaryKey @NonNull @ColumnInfo(name = "package_name") val packageName: String,
    @ColumnInfo(name = "label") val label: String,
    @ColumnInfo(name = "hidden") var hidden: Boolean = false,
    @ColumnInfo(name = "deleted") var deleted: Boolean = false
)

@Dao
interface ApplicationDao {
    @Query("SELECT * FROM $TABLE_NAME ORDER BY label")
    suspend fun getAll(): List<Application>

    @Query("SELECT * FROM $TABLE_NAME WHERE hidden = 0 ORDER BY label")
    suspend fun getVisibleOnly(): List<Application>

    @Transaction
    suspend fun synchronize(applications: List<Application>) {
        insertAll(applications)
        softDelete(applications.map { it.packageName })
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(applications: List<Application>)

    @Update
    suspend fun update(application: Application)

    @Query("UPDATE $TABLE_NAME SET deleted = 1 WHERE package_name NOT IN (:packageNames) AND hidden = 0")
    fun softDelete(packageNames: List<String>)
}

@SuppressLint("QueryPermissionsNeeded")
suspend fun synchronizeApplications(context: Context) {
    val ignoredPackageNames = arrayOf(context.packageName, "com.android.traceur")

    val applications = ArrayList<Application>()
    val packageManager = context.packageManager
    val installedPackages = packageManager.getInstalledPackages(PackageManager.GET_META_DATA)
        .filter { packageManager.getLaunchIntentForPackage(it.packageName) != null && it.packageName !in ignoredPackageNames }
    installedPackages.forEach {
        // Will ignore if package name is already in the database
        applications.add(
            Application(
                packageName = it.packageName,
                label = it.applicationInfo.loadLabel(packageManager).toString()
            )
        )
    }

    SanctuaryDatabase.getInstance(context).applicationDao().synchronize(applications)
}