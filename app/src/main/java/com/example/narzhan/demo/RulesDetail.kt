package com.example.narzhan.demo

import android.arch.persistence.room.Room
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_game_detail.*
import kotlinx.android.synthetic.main.activity_rules_detail.*

class RulesDetail : AppCompatActivity() {

    private val db = Room.databaseBuilder(this, AppDatabase::class.java, "db")
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rules_detail)
        setSupportActionBar(findViewById(R.id.rules_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val values = intent.extras
        val game = db.gameDao().getGame(values.getString("id"))

        title = game.name

        val editText: EditText = findViewById(R.id.rules_text)
        editText.setText(game.rules)
        saveData()
    }

    fun saveData(){
        val values = intent.extras
        var game = db.gameDao().getGame(values.getString("id"))
        game.rules = rules_text.text.toString()
        db.gameDao().updateGame(game)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        saveData()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (item?.itemId == android.R.id.home) {
            saveData()
            finish()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}
