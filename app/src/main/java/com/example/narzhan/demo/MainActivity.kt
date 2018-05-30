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
import android.widget.EditText
import android.widget.LinearLayout
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private val db = Room.databaseBuilder(this, AppDatabase::class.java, "db")
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()

    private val ADD_TASK_REQUEST = 1
    private var DELETE = false
    private lateinit var rv: RecyclerView
    private var dbReference: DatabaseReference = FirebaseDatabase.getInstance().reference

    private var typesChoices = mutableListOf("running", "thinking", "small", "night")
    private var durationChoices = mutableListOf("30 min", "1 h", "1,5 h", "2 h")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.main_toolbar))

        rv = findViewById<RecyclerView>(R.id.main_recycler)
        rv.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)

        var games = db.gameDao().getAll()

        if (games.isEmpty()) {
            val menuListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (game in dataSnapshot.children) {
                            db.gameDao().insert(game.getValue(Game::class.java)!!)
                            refreshList()
                            Snackbar.make(rv, "Default values loaded from Firebase", Snackbar.LENGTH_LONG).show()
                        }
                    } else {
                        createDefaultValues()
                        refreshList()
                        Snackbar.make(rv, "Default values loaded to db and Firebase", Snackbar.LENGTH_LONG).show()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    createDefaultValues()
                    Snackbar.make(rv, "Default values failed to load from db and Firebase", Snackbar.LENGTH_LONG).show()
                    Log.wtf("Firebase error", "loadPost:onCancelled ${databaseError.toException()}")
                }
            }
            dbReference.child("games").addListenerForSingleValueEvent(menuListener)
        } else {
            val menuListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val ids: MutableList<Int> = mutableListOf()
                        for (game in db.gameDao().getAll()) {
                            ids.add(game.id)
                        }
                        var gameAdded = false
                        for (game in dataSnapshot.children) {
                            if (!ids.contains(game.getValue(Game::class.java)!!.id)) {
                                db.gameDao().insert(game.getValue(Game::class.java)!!)
                                gameAdded = true
                            }
                        }
                        if (gameAdded) {
                            refreshList()
                            Snackbar.make(rv, "Values loaded from local storage and Firebase", Snackbar.LENGTH_LONG).show()
                        } else {
                            Snackbar.make(rv, "All values present, no loading", Snackbar.LENGTH_LONG).show()
                        }

                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    createDefaultValues()
                    Snackbar.make(rv, "Default values loaded to db and Firebase", Snackbar.LENGTH_LONG).show()
                    Log.wtf("Firebase error", "loadPost:onCancelled ${databaseError.toException()}")
                }
            }
            dbReference.child("games").addListenerForSingleValueEvent(menuListener)
        }

        rv.adapter = Adapter(ArrayList(games), { selectGame: Game -> gameClicked(selectGame) })

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
            }
        }
    }

    private fun createDefaultValues(){
        val defaultGames: List<Game> = mutableListOf(
                Game("Capture the flag", "running", "1,5 h","Something truly brutal"),
                Game("Maze", "thinking", "2 h","This is just a placeholder"),
                Game("Something", "something", "30 min","For this, I just didn't know what to put there")
        )
        defaultGames.forEach {
            db.gameDao().insert(it)
            val key=dbReference.child("games").push().key
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

    private fun gameClicked(game: Game) {
        if (!DELETE){
            val intent = Intent(this, GameDetail::class.java)
            intent.putExtra("id", game.id.toString())
            intent.putExtra("types", typesChoices.joinToString(","))
            intent.putExtra("durations", durationChoices.joinToString(","))
            startActivity(intent)
        } else {
            val alert = AlertDialog.Builder(this)
            alert.setTitle("Delete confirmation")
            alert.setMessage("Are you sure you want to delete this game.")
            alert.setPositiveButton("Yes", { _, _ ->
                db.gameDao().removeGame(game)
                refreshList()
            } )
            alert.setNegativeButton("Cancel", { _, _ -> })
            alert.show()
        }

    }


    fun addHelpDialog(){
        val alert = AlertDialog.Builder(this)
        alert.setTitle("Help information")
        alert.setMessage("To see the game details click it.\nTo create new game click +.\nTo delete games click delete icon.\nTo add categories click settings")
        alert.setPositiveButton("Understood") { _, _ ->
        }
        alert.show()
    }

    fun addNewItemDialog(cathegory: String, list: MutableList<String>) {
        val alert = AlertDialog.Builder(this)
        val itemEditText = EditText(this)
        itemEditText.hint = "Add New $cathegory"
//        alert.setMessage()
        alert.setTitle("Enter new $cathegory text")
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
        if (!DELETE){
            val inflater = menuInflater
            inflater.inflate(R.menu.toolbar_menu, menu)
            return super.onCreateOptionsMenu(menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                DELETE = false
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
                supportActionBar?.title = "Game DB"
                setSupportActionBar(findViewById(R.id.main_toolbar))
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
            R.id.toolbar_delete -> {
                DELETE = true
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                setSupportActionBar(findViewById(R.id.main_toolbar))
                supportActionBar?.title = "Deleting games..."
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun refreshList() {
        val games = db.gameDao().getAll()
        rv.adapter = Adapter(ArrayList(games), { selectGame: Game -> gameClicked(selectGame) })
    }
}
