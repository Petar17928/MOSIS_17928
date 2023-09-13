package elfak.mosis.mosis_17928

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {

    var auth: FirebaseAuth= FirebaseAuth.getInstance()
    lateinit var btnLogout: Button
    val user: FirebaseUser?= auth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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


}