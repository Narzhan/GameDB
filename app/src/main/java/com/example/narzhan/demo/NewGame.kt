package com.example.narzhan.demo

import android.app.Activity
import android.app.AlertDialog
import android.arch.persistence.room.Room
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_new_game.*
import java.lang.reflect.Array

class NewGame : AppCompatActivity() {


    private val db = Room.databaseBuilder(this, AppDatabase::class.java, "db")
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_game)
        setSupportActionBar(findViewById(R.id.new_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setTitle("New Game Creation")

        val values = intent.extras

        val typesChoices: List<String> = values.get("types").toString().split(",").map { it.trim() }
        val typesAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, typesChoices)
        val typeSpinner: Spinner = findViewById(R.id.new_type)
        typesAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        typeSpinner.adapter = typesAdapter

        val durationChoices: List<String> = values.get("durations").toString().split(",").map { it.trim() }
        val durationAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, durationChoices)
        val durationSpinner: Spinner = findViewById(R.id.new_duration)
        durationAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        durationSpinner.adapter = durationAdapter

        val newGameFab: FloatingActionButton = findViewById(R.id.new_fab)
        newGameFab.setOnClickListener {
            doneClicked()
        }
    }

    companion object {
        val EXTRA_GAME_NAME = "game"
    }


    fun doneClicked() {
        val inputs = listOf(new_name, new_rules)
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
            db.gameDao().insert(Game(new_name.text.toString(), new_type.selectedItem.toString(), new_duration.selectedItem.toString(), new_rules.text.toString()))
            setResult(Activity.RESULT_OK, result)
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
