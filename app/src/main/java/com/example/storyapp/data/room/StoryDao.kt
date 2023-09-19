package com.example.storyapp.data.room

import androidx.lifecycle.LiveData
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.storyapp.data.local.StoryEntity

interface StoryDao {
    @Query("SELECT * FROM story ORDER BY id ASC")
    fun getStories(): LiveData<List<StoryEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertStories(stories: List<StoryEntity>)

    @Update
    fun updateStories(stories: StoryEntity)

    @Query("DELETE FROM story")
    fun deleteAll()
}