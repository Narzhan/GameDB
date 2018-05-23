package com.example.narzhan.demo


import android.app.Activity
import android.app.AlertDialog
import android.arch.persistence.room.Room
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.FirebaseError
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private val db = Room.databaseBuilder(this, AppDatabase::class.java, "db")
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()

    private val ADD_TASK_REQUEST = 1
    private lateinit var rv :RecyclerView
    private var dbReference: DatabaseReference = FirebaseDatabase.getInstance().reference

    private var typesChoices = mutableListOf("běhací", "přemýšlecí", "malá", "noční")
    private var durationChoices = mutableListOf("30 min", "1 h", "1,5 h", "2 h")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.main_toolbar))

        rv = findViewById<RecyclerView>(R.id.main_recycler)
        rv.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)

//        db.gameDao().destroyAll()
        val dbValues = db.gameDao().getAll()

//        val gameListener = object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                dataSnapshot.children.forEach {
//                    Log.wtf("child",it.child("games").key)
//                    Log.wtf("id", it.child("games").value.toString())
//                }
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                Log.wtf("Error getting data", databaseError.toException())
//            }
//        }
//        dbReference.addListenerForSingleValueEvent(gameListener)

        when {
            dbValues.isEmpty() -> {
//                val gameListener = object : ValueEventListener {
//                    override fun onDataChange(dataSnapshot: DataSnapshot) {
//                        if (!dataSnapshot.hasChild("games")){
                            createDefaultValues()
                            Snackbar.make(rv, "Default values loaded to db and Firebase", Snackbar.LENGTH_LONG).show()
//                        }
//                    }
//
//                    override fun onCancelled(databaseError: DatabaseError) {
//                        Log.wtf("Error getting data", databaseError.toException())
//                    }
//                }
//                dbReference.addListenerForSingleValueEvent(gameListener)
            }
            else -> {
                Snackbar.make(rv, "Default values present, no loading", Snackbar.LENGTH_LONG).show()
            }
        }

        val games = db.gameDao().getAll()
        rv.adapter = Adapter(ArrayList(games), { selectGame : Game -> partItemClicked(selectGame) })


        val newGameFab: FloatingActionButton=findViewById(R.id.main_fab)
        newGameFab.setOnClickListener{
            val intent=Intent(this, NewGame::class.java)
            intent.putExtra("types", typesChoices.joinToString(","))
            intent.putExtra("durations", durationChoices.joinToString(","))
            startActivityForResult(intent, ADD_TASK_REQUEST)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == ADD_TASK_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                val name = data?.getStringExtra(NewGame.EXTRA_GAME_NAME)
                refreshList()
                Snackbar.make(rv, "New game created: ${name}", Snackbar.LENGTH_LONG).show()
                // comment
            }
        }
    }

    private fun createDefaultValues(){
        val defaultGames: List<Game> = mutableListOf(
                Game("Capture the flag", "běhací", "1,5 h","Something truly brutal"),
                Game("Bludiště", "přemýšlecí", "2 h","This is just a placeholder"),
                Game("Something", "something", "30 min","For this, I just didn't know what to put there")
        )
        defaultGames.forEach {
            db.gameDao().insert(it)
            val key=dbReference.child("games").push().key
//            it.id=key
            dbReference.child("games").child(key).setValue(it)
        }
    }

    override fun onResume() {
        super.onResume()
        refreshList()
    }

    override fun onStop() {
        super.onStop()
        val values=db.gameDao().getAll()
        dbReference.child("games").removeValue()
        values.forEach {
            val key=dbReference.child("games").push().key
            dbReference.child("games").child(key).setValue(it)
        }
    }

    private fun partItemClicked(partItem: Game) {
        val intent = Intent(this, GameDetail::class.java)
        intent.putExtra("id", partItem.id.toString())
        intent.putExtra("types", typesChoices.joinToString(","))
        intent.putExtra("durations", durationChoices.joinToString(","))
        startActivity(intent)
    }

    fun addHelpDialog(){
        val alert = AlertDialog.Builder(this)
//        val itemEditText = EditText(this)
        val type= TextView(this)
        type.setText("To creta new type select add new type in menu")
        val duration= TextView(this)
        type.setText("To creta new duration select add new type in menu")
        alert.setTitle("Help")
        val view =View(this)
        alert.setView(type)
        alert.setPositiveButton("Understood") { dialog, positiveButton ->
        }
        alert.show()
    }

    fun addNewItemDialog(cathegory: String, list: MutableList<String>) {
        val alert = AlertDialog.Builder(this)
        val itemEditText = EditText(this)
        itemEditText.hint = "Add New ${cathegory}"
//        alert.setMessage()
        alert.setTitle("Enter new ${cathegory} text")
        alert.setView(itemEditText)
        alert.setPositiveButton("Submit") { dialog, positiveButton ->
            if (!list.contains(itemEditText.text.toString())) {
                list.add(itemEditText.text.toString())
                Snackbar.make(rv, "${itemEditText.text} added to $cathegory", Snackbar.LENGTH_LONG).show()
            } else {
                Snackbar.make(rv, "${itemEditText.text} is already present in $cathegory", Snackbar.LENGTH_LONG).show()
            }
        }
        alert.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.toolbar_type -> {
                addNewItemDialog("type", typesChoices)
                return true
            }
            R.id.toolbar_duration -> {
                addNewItemDialog("duration", durationChoices)
                return true
            }
            R.id.toolbar_help -> {
                addHelpDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun refreshList() {
        val games = db.gameDao().getAll()
        rv.adapter = Adapter(ArrayList(games), { selectGame : Game -> partItemClicked(selectGame) })
    }
}
