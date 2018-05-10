package com.example.narzhan.demo

import android.arch.persistence.room.Room
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_game_detail.*

class GameDetail : AppCompatActivity() {


    private val db = Room.databaseBuilder(this, AppDatabase::class.java, "db")
            .allowMainThreadQueries()
            .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_detail)
        setSupportActionBar(findViewById(R.id.detail_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val values = intent.extras
        var game = db.gameDao().getGame(values.getString("id"))

        title = game.name

        val editType: EditText = findViewById(R.id.detail_type)
        editType.setText(game.type)

        val rulesButton: Button = findViewById(R.id.detail_rules_button)
        rulesButton.setOnClickListener {
            val intent = Intent(this, RulesDetail::class.java)
            intent.putExtra("id", game.id.toString())
            saveData()
            startActivity(intent)
        }

    }

    fun saveData(){
        val values = intent.extras
        var game = db.gameDao().getGame(values.getString("id"))
        game.type = detail_type.text.toString()
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
