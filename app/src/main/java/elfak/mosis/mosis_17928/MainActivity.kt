package elfak.mosis.mosis_17928

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity(),OnMapReadyCallback {

    var auth: FirebaseAuth= FirebaseAuth.getInstance()
    lateinit var btnLogout: Button
    val user: FirebaseUser?= auth.currentUser
    private var mapFragment: SupportMapFragment?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupMap()
        btnLogout = findViewById(R.id.btn_logout)

        if(user==null){
            val intent = Intent(applicationContext, Login::class.java)
            startActivity(intent)
            finish()
        }
        else{
            var txtLogged: TextView = findViewById(R.id.logged)
            val originalString = user.email
            val txt = originalString?.substringBefore('@')
            txtLogged.setText(txt)
        }

        btnLogout.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(applicationContext, Login::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun setupMap(){
        mapFragment=supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        var places:List<Place> ?= null
        val latLon1 = LatLng(43.3209, 21.8958)
        val latLon2 = LatLng(43.3509, 21.8458)
    }

}