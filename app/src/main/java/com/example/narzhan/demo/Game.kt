package com.example.narzhan.demo

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "game")
data class Game(
        @ColumnInfo(name = "name")
        var name: String="",
        @ColumnInfo(name = "type")
        var type: String="",
        @ColumnInfo(name = "duration")
        var duration: String="",
        @ColumnInfo(name = "rules")
        var rules: String="",
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id")
        var id: Int=0

)	{
//    override fun toString(): String {
//        return name
//    }
}

