package elfak.mosis.mosis_17928

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import org.osmdroid.util.GeoPoint


class MainActivity : AppCompatActivity() {

    var auth: FirebaseAuth= FirebaseAuth.getInstance()
    lateinit var btnLogout: Button
    lateinit var btnFind: Button
    lateinit var btnAdd: Button
    lateinit var btnScores: Button

    val user: FirebaseUser?= auth.currentUser
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var db: FirebaseDatabase
    private lateinit var dbRefPlace: DatabaseReference
    private lateinit var dbRefUser: DatabaseReference
    private lateinit var thisUser: User
    private var userLat:Double=0.0
    private var userLon:Double=0.0

    val getUserLat:Double
        get() {
            return userLat
        }

    val getUserLon:Double
        get() {
            return userLon
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnLogout = findViewById(R.id.btn_logout)
        btnFind = findViewById(R.id.btn_find)
        btnAdd=findViewById(R.id.btn_add)
        btnScores=findViewById(R.id.btn_scores)
        db= FirebaseDatabase.getInstance("https://mosis17928-default-rtdb.europe-west1.firebasedatabase.app/")
        dbRefPlace= db.reference.child("Place")
        dbRefUser= db.reference.child("User")

        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this)
//

        val loc = fusedLocationProviderClient.lastLocation

        if(ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)!=
            PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)!=
            PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),101)
        }

        loc.addOnSuccessListener {
            if(it!=null){
                userLat=it.latitude
                userLon=it.longitude
            }
        }


        if(user==null){
            val intent = Intent(applicationContext, Login::class.java)
            startActivity(intent)
            finish()
        }
        else{
            val txtLogged: TextView = findViewById(R.id.logged)
            val query: Query = dbRefUser.orderByChild("uid").equalTo(auth.currentUser!!.uid)
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.children) {
                            thisUser=User(snapshot.key,
                                snapshot.child("userName").getValue(String::class.java),
                                snapshot.child("score").getValue(Int::class.java),
                                snapshot.child("password").getValue(String::class.java),
                                snapshot.child("firstName").getValue(String::class.java),
                                snapshot.child("lastName").getValue(String::class.java),
                                snapshot.child("phone").getValue(String::class.java),
                                snapshot.child("mail").getValue(String::class.java))
                        txtLogged.setText(thisUser.userName)
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    println("Firebase Error: ${databaseError.message}")
                }
            })
        }

        btnLogout.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(applicationContext, Login::class.java)
            startActivity(intent)
            finish()
        }
///
        btnAdd.setOnClickListener{
            addLocation()
        }

        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        val myFragment = MapFragment()
        val btnStart:Button = findViewById(R.id.btn_start)
        btnStart.setOnClickListener {

            val bundle = Bundle()
            bundle.putString("message", thisUser.mail)
            myFragment.arguments = bundle
            fragmentTransaction.add(R.id.mapFrag, myFragment).commit()
            btnStart.visibility = View.GONE
            btnFind.visibility = View.VISIBLE
            btnAdd.visibility = View.VISIBLE
            btnScores.visibility = View.VISIBLE
        }

///
        btnFind.setOnClickListener{
            findPlaces()
        }

        btnScores.setOnClickListener{
            showScores()
        }

    }
    private fun addLocation(){
        val task = fusedLocationProviderClient.lastLocation

        if(ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)!=
            PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)!=
            PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),101)
        }

        task.addOnSuccessListener {
            if(it!=null){
                val builder: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
                val input = EditText(this@MainActivity)
                builder.setView(input)
               ///////////////////////
                thisUser.score = thisUser.score?.plus(1) ?: 0
                val userReference = dbRefUser.child(thisUser.uid!!)
                userReference.child("score").setValue(thisUser.score)
/////////////////////
                builder.apply {
                    setTitle("Adding new place")
                    setMessage("Choose place name")
                    setPositiveButton("Add") { dialog, which ->
                        val inputTxt = input.text.toString()
                        val newPlace = Place(inputTxt, userLat, userLon,null)
                        val newChildRef = dbRefPlace.push()
                        newChildRef.setValue(newPlace)
                    }
                    setNegativeButton("Cancel") { dialog, which ->
                        dialog.cancel()
                    }
                }
                val dialog = builder.create()
                dialog.show()
            }
        }

    }

    private fun findPlaces(){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
        val layout = LinearLayout(this@MainActivity)
        layout.orientation = LinearLayout.VERTICAL
        var places:MutableList<Place> = mutableListOf()
        var plc:Place
        val query: Query = dbRefPlace
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    plc=Place(
                        snapshot.child("naziv").getValue(String::class.java),
                        snapshot.child("latitude").getValue(Double::class.java),
                        snapshot.child("longitude").getValue(Double::class.java))
                    places.add(plc)
                    val disp = TextView(this@MainActivity)
                    disp.setText(plc.naziv)
                    disp.setPadding(20,20,20,20)
                    layout.addView(disp)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                println("Firebase Error: ${databaseError.message}")
            }
        })



        val input = EditText(this@MainActivity)
        input.setInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_NORMAL)
        input.setHint("Distance")
        layout.addView(input)

        val inputTxt = EditText(this@MainActivity)
        inputTxt.setHint("Filter")
        layout.addView(inputTxt)

        builder.setView(layout)
        builder.apply {
            setTitle("Find places")
            setMessage("")
            setPositiveButton("Find") { dialog, which ->
                if(input.text.toString().isEmpty())
                {
                    findNearPlaces(20010000, inputTxt.text.toString())
                    dialog.cancel()
                }
                else{
                    findNearPlaces(input.text.toString().toInt(), inputTxt.text.toString())
                    dialog.cancel()
                }

            }
            setNegativeButton("Cancel") { dialog, which ->
                dialog.cancel()
            }
        }
        val dialog = builder.create()
        dialog.show()

    }

    private fun showScores(){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
        val layout = LinearLayout(this@MainActivity)
        layout.orientation = LinearLayout.VERTICAL
        var users:MutableList<User> = mutableListOf()
        var usr:User
        val query: Query = dbRefUser
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    usr=User(snapshot.key,
                        snapshot.child("userName").getValue(String::class.java),
                        snapshot.child("score").getValue(Int::class.java) )
                    users.add(usr)

                }

                val sortedList = users.sortedByDescending { it.score }

                for(u in sortedList){
                    val disp = TextView(this@MainActivity)
                    val txt:String
                    if(u.score==null)
                    {
                        txt = u.userName +": 0"
                    }else{
                        txt = u.userName +": "+ u.score
                    }
                    disp.setText(txt)
                    disp.setPadding(20,20,20,20)
                    layout.addView(disp)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                println("Firebase Error: ${databaseError.message}")
            }
        })

        builder.setView(layout)
        builder.apply {
            setTitle("User scores")
            setMessage("")
            setNegativeButton("Cancel") { dialog, which ->
                dialog.cancel()
            }
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun findNearPlaces(dist: Int, str: String){

        val gp= GeoPoint(userLat,userLon)
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
        val layout = LinearLayout(this@MainActivity)
        layout.orientation = LinearLayout.VERTICAL
        var places:MutableList<Place> = mutableListOf()
        var plc:Place
        val query: Query = dbRefPlace
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    plc=Place(//snapshot.key,
                        snapshot.child("naziv").getValue(String::class.java),
                        snapshot.child("latitude").getValue(Double::class.java),
                        snapshot.child("longitude").getValue(Double::class.java))
                    places.add(plc)
                }


                for(p in places){
                    val gp2 = GeoPoint(p.latitude!!,p.longitude!!)
                    var d = gp.distanceToAsDouble(gp2)
                    d = String.format("%.0f",d ).toDouble()
                    if(dist>=d){
                        if (p.naziv!!.startsWith(str)) {
                            val disp = TextView(this@MainActivity)

                            disp.setText("${p.naziv} udaljenost: ${d} m")
                            disp.setPadding(20,20,20,20)
                            layout.addView(disp)
                        }

                    }

                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                println("Firebase Error: ${databaseError.message}")
            }
        })

        val input = EditText(this@MainActivity)
        input.setInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_NORMAL)
        input.setHint("Distance")
        layout.addView(input)

        val inputTxt = EditText(this@MainActivity)
        inputTxt.setHint("Filter")
        layout.addView(inputTxt)

        builder.setView(layout)
        builder.apply {
            setTitle("Find places")
            setMessage("Results closer than: $dist meters \n Starting with: $str ")
            setPositiveButton("Find") { dialog, which ->
                if(input.text.toString().isEmpty())
                {
                    findNearPlaces(20010000, inputTxt.text.toString())
                    dialog.cancel()
                }
                else{
                    findNearPlaces(input.text.toString().toInt(), inputTxt.text.toString())
                    dialog.cancel()
                }

            }
            setNegativeButton("Cancel") { dialog, which ->
                dialog.cancel()
            }
        }
        val dialog = builder.create()
        dialog.show()
    }
}