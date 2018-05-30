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

        var typesChoices: List<String> = values.get("types").toString().split(",").map { it.trim() }
        if (!typesChoices.contains(game.type)){
            typesChoices+=game.type
        }
        val typesAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, typesChoices)
        val typeSpinner: Spinner = findViewById(R.id.detail_type)
        typesAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        typeSpinner.adapter = typesAdapter
        typeSpinner.setSelection(typesChoices.indexOf(game.type))

        var durationChoices: List<String> = values.get("durations").toString().split(",").map { it.trim() }
        if (!durationChoices.contains(game.duration)){
            durationChoices+=game.duration
        }
        val durationAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, durationChoices)
        val durationSpinner: Spinner = findViewById(R.id.detail_duration)
        durationAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        durationSpinner.adapter = durationAdapter
        durationSpinner.setSelection(durationChoices.indexOf(game.duration))

        val rulesButton: Button = findViewById(R.id.detail_rules_button)
        rulesButton.setOnClickListener {
            val intent = Intent(this, RulesDetail::class.java)
            intent.putExtra("id", game.id.toString())
            saveData()
            startActivity(intent)
        }
//        val images = hashMapOf("běhací" to R.drawable.running, "přemýšlecí" to R.drawable.thinking, "malá" to R.drawable.quick, "noční" to R.drawable.night)
//        val picture: ImageView = findViewById(R.id.detail_picture)
//        if (images.containsKey(game.type)) {
//            picture.setImageResource(images.getValue(game.type))
//        } else {
//            picture.setImageResource(R.drawable.custom)
//        }
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
