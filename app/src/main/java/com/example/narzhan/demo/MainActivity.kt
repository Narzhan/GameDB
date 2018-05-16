package com.example.narzhan.demo


import android.app.Activity
import android.arch.persistence.room.Room
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.LinearLayout
import android.widget.Toast
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private val db = Room.databaseBuilder(this, AppDatabase::class.java, "db")
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()

    private val ADD_TASK_REQUEST = 1
    private lateinit var rv :RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.main_toolbar))

        rv = findViewById<RecyclerView>(R.id.main_recycler)
        rv.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)


//        val database: FirebaseDatabase= FirebaseDatabase.getInstance()
//        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("games")

        val dbValues = db.gameDao().getAll()
//        val values = databaseReference.equalTo(FirebaseAuth.getInstance().get)
        when {
            dbValues.isEmpty() -> {
               createDefaultValues()
                Snackbar.make(rv, "Default values loaded to db and Firebase", Snackbar.LENGTH_LONG).show()
            }
//            dbValues.isEmpty() && true -> {
//          && databaseReference.child("games")->
//            }
            else -> {
                Snackbar.make(rv, "Default values present, no loading", Snackbar.LENGTH_LONG).show()
            }
        }

        val games = db.gameDao().getAll()
        rv.adapter = Adapter(ArrayList(games), { selectGame : Game -> partItemClicked(selectGame) })


        val newGameFab: FloatingActionButton=findViewById(R.id.main_fab)
        newGameFab.setOnClickListener{
            val intent=Intent(this, NewGame::class.java)
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
//            val key=dbRef.child("games").push().key dbRef:DatabaseReference
//            it.id=key
//            dbRef.child("games").child(key).setValue(it)
        }
    }

    override fun onResume() {
        super.onResume()
        refreshList()
    }

    private fun partItemClicked(partItem: Game) {
//        Toast.makeText(this, "Clicked: ${partItem.id}", Toast.LENGTH_LONG).show()
        val intent = Intent(this, GameDetail::class.java)
        intent.putExtra("id", partItem.id.toString())
        startActivity(intent)
    }

//    private fun getData(){
//        val api = Api()
//        val service = api.getGames()
//        service.getGames().enqueue(object : Callback<Game> {
//            override fun onResponse(call: retrofit2.Call<Game>?, response: Response<Game>?) {
//                Toast.makeText(this@MainActivity, "success", Toast.LENGTH_LONG).show()
//// return response?.body()
//            }
//
//            override fun onFailure(call: retrofit2.Call<Game>?, t: Throwable?) {
//                Toast.makeText(this@MainActivity, "failure", Toast.LENGTH_LONG).show()
//            }
//        })
//    }

    private fun refreshList() {
        val games = db.gameDao().getAll()
        rv.adapter = Adapter(ArrayList(games), { selectGame : Game -> partItemClicked(selectGame) })
    }
}
