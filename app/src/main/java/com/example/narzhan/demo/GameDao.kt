package com.example.narzhan.demo

import android.arch.persistence.room.*

/**
 * Created by Narzhan on 15/04/2018.
 */

@Dao
interface GameDao {
    @Query("SELECT * FROM game LIMIT 1 OFFSET :arg0")
    fun getByPosition(offset: Int): Game

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(game: Game)

    @Delete
    fun removeGame(game: Game)

    @Query("DELETE FROM game")
    fun destroyAll()

    @Update
    fun updateGame(game:Game)
//    @Query("UPDATE game SET :column = :value WHERE id = :id")
//    fun updateGame(id: String, column: String, value: String): Game

    @Query("SELECT * FROM game WHERE id = :arg0")
    fun getGame(id: String): Game

    @Query("SELECT * FROM game")
    fun getAll(): List<Game>
}

