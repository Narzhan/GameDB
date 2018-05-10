package com.example.narzhan.demo

import android.app.Activity
import android.arch.persistence.room.Room
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_new_game.*

class NewGame : AppCompatActivity() {


    private val db = Room.databaseBuilder(this, AppDatabase::class.java, "db")
            .allowMainThreadQueries()
            .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_game)
        setSupportActionBar(findViewById(R.id.new_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setTitle("New Game Creation")
    }

    companion object {
        val EXTRA_GAME_NAME = "game"
    }

    fun doneClicked(view: View) {
//        val newGame= Game(new_name.text.toString(), new_type.text.toString(), new_rules.text.toString())
        val inputs = listOf(new_name, new_type, new_rules)
        var helper = false
        for (input in inputs) {
            if (input.text.toString().isEmpty()) {
                input.error = "This field is mandatory"
                helper = true
            }
        }
        if (!helper) {
            val result = Intent()
            result.putExtra(EXTRA_GAME_NAME, new_name.text.toString())
            db.gameDao().insert(Game(new_name.text.toString(), new_type.text.toString(), new_rules.text.toString()))
            setResult(Activity.RESULT_OK, result)
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (item?.itemId == android.R.id.home) {
            finish()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}
