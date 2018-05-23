package com.example.narzhan.demo

import android.arch.persistence.room.Room
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_game_detail.*

class GameDetail : AppCompatActivity() {


    private val db = Room.databaseBuilder(this, AppDatabase::class.java, "db")
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_detail)
        setSupportActionBar(findViewById(R.id.detail_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val values = intent.extras
        var game = db.gameDao().getGame(values.getString("id"))

        title = game.name

//        val newVarious: EditText=findViewById(R.id.detail_cathegory_duration)
//        newVarious.visibility= View.INVISIBLE

//        var typesChoices = mutableListOf("běhací", "přemýšlecí", "malá", "noční")
        var typesChoices: List<String> = values.get("types").toString().split(",").map { it.trim() }
        if (!typesChoices.contains(game.type)){
            typesChoices+=game.type
//            typesChoices.add(game.type)
        }
        val typesAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, typesChoices)
        val typeSpinner: Spinner = findViewById(R.id.detail_type)
        typesAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        typeSpinner.adapter = typesAdapter
        typeSpinner.setSelection(typesChoices.indexOf(game.type))

//        var durationChoices = mutableListOf("30 min", "1 h", "1,5 h", "2 h")
        var durationChoices: List<String> = values.get("durations").toString().split(",").map { it.trim() }
        if (!durationChoices.contains(game.duration)){
            durationChoices+=game.duration
//            durationChoices.add(game.duration)
        }
        val durationAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, durationChoices)
        val durationSpinner: Spinner = findViewById(R.id.detail_duration)
        durationAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        durationSpinner.adapter = durationAdapter
        durationSpinner.setSelection(durationChoices.indexOf(game.duration))

//        val newType: Switch = findViewById(R.id.detail_type_switch)
//        val newDuration: Switch = findViewById(R.id.detail_duration_switch)
//
//        newType.setOnCheckedChangeListener { _, isChecked ->
//            if (isChecked) {
//                if (newDuration.isChecked) {
//                    newDuration.isChecked = false
//                }
//                newVarious.visibility = View.VISIBLE
//            } else {
//                newVarious.visibility = View.INVISIBLE
//                if (!newVarious.text.toString().isEmpty()) {
//                    typesChoices.add(newVarious.text.toString())
//                    typesAdapter.notifyDataSetChanged()
//                    typeSpinner.setSelection(typesChoices.indexOf(newVarious.text.toString()))
//                    newVarious.setText("")
//                }
//            }
//        }
//
//        newDuration.setOnCheckedChangeListener { _, isChecked ->
//            if (isChecked) {
//                if (newType.isChecked) {
//                    newType.isChecked = false
//                }
//                newVarious.visibility = View.VISIBLE
//            } else {
//                newVarious.visibility = View.INVISIBLE
//                if (!newVarious.text.toString().isEmpty()) {
//                    durationChoices.add(newVarious.text.toString())
//                    durationAdapter.notifyDataSetChanged()
//                    durationSpinner.setSelection(durationChoices.indexOf(newVarious.text.toString()))
//                    newVarious.setText("")
//                }
//            }
//        }

        val rulesButton: Button = findViewById(R.id.detail_rules_button)
        rulesButton.setOnClickListener {
            val intent = Intent(this, RulesDetail::class.java)
            intent.putExtra("id", game.id.toString())
//            when {
//                newType.isChecked -> newType.isChecked = false
//                newDuration.isChecked -> newDuration.isChecked = false
//            }
            saveData()
            startActivity(intent)
        }
    }

    fun saveData(){
        val values = intent.extras
        var game = db.gameDao().getGame(values.getString("id"))
        game.type = detail_type.selectedItem.toString()
        game.duration= detail_duration.selectedItem.toString()
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
