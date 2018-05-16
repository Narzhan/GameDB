package com.example.narzhan.demo

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

/**
 * Created by Narzhan on 15/04/2018.
 */

@Database(entities = arrayOf(Game::class), version = 2)
abstract class AppDatabase: RoomDatabase(){
    abstract fun gameDao(): GameDao
}