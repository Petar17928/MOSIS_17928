package elfak.mosis.mosis_17928

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.MeasureSpec
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
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
    val user: FirebaseUser?= auth.currentUser
    private var mapFragment: SupportMapFragment?=null
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var db: FirebaseDatabase
    private lateinit var dbRefPlace: DatabaseReference
    private lateinit var dbRefUser: DatabaseReference




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnLogout = findViewById(R.id.btn_logout)
        db= FirebaseDatabase.getInstance("https://mosis17928-default-rtdb.europe-west1.firebasedatabase.app/")
        dbRefPlace= db.reference.child("Place")
        dbRefUser= db.reference.child("User")

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
                        //val objectKey = snapshot.key
                        val txt = snapshot.child("userName").getValue(String::class.java)
                        txtLogged.setText(txt)
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
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this)

        findViewById<Button>(R.id.btn_add).setOnClickListener{
            addLocation()
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
                var newPlace = Place("Searchuj vlasnika po auth uid", it.latitude, it.longitude,null)
                val newChildRef = dbRefPlace.push()
                newChildRef.setValue(newPlace).addOnSuccessListener {

                    Toast.makeText(this, "Location added ${newPlace.latitude}, ${newPlace.longitude}",Toast.LENGTH_SHORT).show()

                }.addOnFailureListener(){
                    Toast.makeText(this, "Failed.", Toast.LENGTH_SHORT).show()
                }

            }
        }

    }
}