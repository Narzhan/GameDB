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
        val newVarious: EditText=findViewById(R.id.new_cathegory_duration)
        newVarious.visibility= View.INVISIBLE

        var typesChoices = mutableListOf("běhací", "přemýšlecí", "malá", "noční")
        val typesAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, typesChoices)
        val typeSpinner: Spinner = findViewById(R.id.new_type)
        typesAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        typeSpinner.adapter = typesAdapter

        var durationChoices = mutableListOf("30 min", "1 h", "1,5 h", "2 h")
        val durationAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, durationChoices)
        val durationSpinner: Spinner = findViewById(R.id.new_duration)
        durationAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        durationSpinner.adapter = durationAdapter

        val newType: Switch = findViewById(R.id.new_type_switch)
        val newDuration: Switch = findViewById(R.id.new_duration_switch)

        newType.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (newDuration.isChecked) {
                    newDuration.isChecked = false
                }
                newVarious.visibility = View.VISIBLE
            } else {
                newVarious.visibility = View.INVISIBLE
                if (!newVarious.text.toString().isEmpty()) {
                    typesChoices.add(newVarious.text.toString())
                    typesAdapter.notifyDataSetChanged()
                    typeSpinner.setSelection(typesChoices.indexOf(newVarious.text.toString()))
                    newVarious.setText("")
                }
            }
        }

        newDuration.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (newType.isChecked) {
                    newType.isChecked = false
                }
                newVarious.visibility = View.VISIBLE
            } else {
                newVarious.visibility = View.INVISIBLE
                if (!newVarious.text.toString().isEmpty()) {
                    durationChoices.add(newVarious.text.toString())
                    durationAdapter.notifyDataSetChanged()
                    durationSpinner.setSelection(durationChoices.indexOf(newVarious.text.toString()))
                    newVarious.setText("")
                }
            }
        }

        val newGameFab: FloatingActionButton = findViewById(R.id.new_fab)
        newGameFab.setOnClickListener {
            doneClicked()
        }
    }

    companion object {
        val EXTRA_GAME_NAME = "game"
    }


    fun doneClicked() {
//        val newGame= Game(new_name.text.toString(), new_type.text.toString(), new_rules.text.toString())
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

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (item?.itemId == android.R.id.home) {
            finish()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}
