package com.example.myto_dolist
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Insert
    suspend fun insert(taskEntity: TaskEntity)

    @Update
    suspend fun update(taskEntity: TaskEntity)

    @Delete
    suspend fun delete(taskEntity: TaskEntity)

    @Query("Select * from `task-table`")
    fun fetchAllTasks():Flow<List<TaskEntity>>

    @Query("Select * from `task-table` where date = :currentDate")
    fun fetchAllTasksByDate(currentDate: String):Flow<List<TaskEntity>>

    @Query("Select * from `task-table` where id=:id")
    fun fetchTaskById(id:Int):Flow<TaskEntity>

    @Query("Select * from `task-table` where isCompleted=0 and category = 'Work' and date = :currentDate")
    fun fetchWorkIncompleteTasks(currentDate: String):Flow<List<TaskEntity>>

    @Query("Select * from `task-table` where isCompleted=1 and category = 'Work' and date = :currentDate")
    fun fetchWorkCompleteTasks(currentDate: String):Flow<List<TaskEntity>>

    @Query("Select * from `task-table` where isCompleted=0 and category = 'Personal' and date = :currentDate")
    fun fetchPersonaIncompleteTasks(currentDate: String):Flow<List<TaskEntity>>

    @Query("Select * from `task-table` where isCompleted=1 and category = 'Personal' and date = :currentDate")
    fun fetchPersonalCompleteTasks(currentDate: String):Flow<List<TaskEntity>>

    @Query("Select * from `task-table` where isCompleted=0 and date = :currentDate")
    fun fetchAllIncompleteTasks(currentDate: String):Flow<List<TaskEntity>>

    @Query("Select * from `task-table` where isCompleted=1 and date = :currentDate")
    fun fetchAllCompleteTasks(currentDate: String):Flow<List<TaskEntity>>

    @Query("Select * from `task-table` where isCompleted=0 and date = :currentDate order by  task ")
    fun fetchAllSortedTasks(currentDate: String):Flow<List<TaskEntity>>

    @Query("Select * from `task-table` where isCompleted=1  and date = :currentDate order by  task")
    fun fetchAllSortedCompletedTasks(currentDate: String):Flow<List<TaskEntity>>

    @Query("Select * from `task-table` where isCompleted=0  and date = :currentDate order by  id")
    fun fetchAllSortedTasksByTime(currentDate: String):Flow<List<TaskEntity>>

    @Query("Select * from `task-table` where isCompleted=1 and date = :currentDate order by  id ")
    fun fetchAllSortedCompletedTasksByTime(currentDate: String):Flow<List<TaskEntity>>

    @Query("Select * from `task-table` where isCompleted=0 and category = 'Work'  and date = :currentDate order by  task")
    fun fetchWorkSortedTasks(currentDate: String):Flow<List<TaskEntity>>

    @Query("Select * from `task-table` where isCompleted=1 and category = 'Work'  and date = :currentDate order by  task")
    fun fetchWorkSortedCompletedTasks(currentDate: String):Flow<List<TaskEntity>>

    @Query("Select * from `task-table` where isCompleted=0 and category = 'Work' and date = :currentDate order by  id ")
    fun fetchWorkSortedTasksByTime(currentDate: String):Flow<List<TaskEntity>>

    @Query("Select * from `task-table` where isCompleted=1 and category = 'Work' and date = :currentDate order by  id ")
    fun fetchWorkSortedCompletedTasksByTime(currentDate: String):Flow<List<TaskEntity>>

    @Query("Select * from `task-table` where isCompleted=0 and category = 'Personal' and date = :currentDate order by  task ")
    fun fetchPersonalSortedTasks(currentDate: String):Flow<List<TaskEntity>>

    @Query("Select * from `task-table` where isCompleted=1 and category = 'Personal' and date = :currentDate order by  task ")
    fun fetchPersonalSortedCompletedTasks(currentDate: String):Flow<List<TaskEntity>>

    @Query("Select * from `task-table` where isCompleted=0 and category = 'Personal' and date = :currentDate order by  id ")
    fun fetchPersonalSortedTasksByTime(currentDate: String):Flow<List<TaskEntity>>

    @Query("Select * from `task-table` where isCompleted=1 and category = 'Personal' and date = :currentDate order by  id ")
    fun fetchPersonalSortedCompletedTasksByTime(currentDate: String):Flow<List<TaskEntity>>

    @Query("Select * from `task-table` where task LIKE :searchQuery and isCompleted=0 and date = :currentDate")
    fun searchDatabaseForIncomplete(searchQuery: String, currentDate: String):Flow<List<TaskEntity>>

    @Query("Select * from `task-table` where task LIKE :searchQuery and isCompleted=1 and date = :currentDate")
    fun searchDatabaseForComplete(searchQuery: String, currentDate: String):Flow<List<TaskEntity>>

    @Query("Select * from `task-table` where task LIKE :searchQuery and isCompleted=0 and category = 'Work' and date = :currentDate")
    fun searchDatabaseForWorkIncomplete(searchQuery: String, currentDate: String):Flow<List<TaskEntity>>

    @Query("Select * from `task-table` where task LIKE :searchQuery and isCompleted=1 and category = 'Work' and date = :currentDate")
    fun searchDatabaseForWorkComplete(searchQuery: String, currentDate: String):Flow<List<TaskEntity>>

    @Query("Select * from `task-table` where task LIKE :searchQuery and isCompleted=0 and category = 'Personal' and date = :currentDate")
    fun searchDatabaseForPersonalIncomplete(searchQuery: String, currentDate: String):Flow<List<TaskEntity>>

    @Query("Select * from `task-table` where task LIKE :searchQuery and isCompleted=1 and category = 'Personal' and date = :currentDate")
    fun searchDatabaseForPersonalComplete(searchQuery: String, currentDate: String):Flow<List<TaskEntity>>

    @Query("SELECT COUNT(*) FROM `task-table` WHERE isCompleted = 0 and category = 'All'")
    fun getCountOfIncompleteAllTasks(): Flow<Int>

    @Query("SELECT COUNT(*) FROM `task-table` WHERE isCompleted = 0 and category = 'Work'")
    fun getCountOfIncompleteWorkTasks(): Flow<Int>

    @Query("SELECT COUNT(*) FROM `task-table` WHERE isCompleted = 0 and category = 'Personal'")
    fun getCountOfIncompletePersonalTasks(): Flow<Int>

    @Query("SELECT COUNT(*) FROM `task-table` WHERE isCompleted = 1")
    fun getCountOfCompletedTasks(): Flow<Int>

    @Query("SELECT COUNT(*) FROM `task-table` WHERE isCompleted = 0")
    fun getCountOfIncompleteTasks(): Flow<Int>

    @Query("DELETE FROM `task-table` WHERE isCompleted = 1;")
    fun deletedCompletedTasks()
}