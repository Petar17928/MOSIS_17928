package elfak.mosis.mosis_17928

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.preference.PreferenceManager
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


class MapFragment : Fragment() {
    private lateinit var main: MainActivity
    lateinit var map: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_map, container, false) as ViewGroup
        main = activity as MainActivity

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view,savedInstanceState)

        val ctx: Context?= activity?.applicationContext
        Configuration.getInstance().load(ctx,PreferenceManager.getDefaultSharedPreferences((ctx)))
        map=requireView().findViewById(R.id.map)
        map.setMultiTouchControls(true)

        var longitude = 21.8958
        var latitude = 43.3209

        map.controller.setZoom(15.0)
        Toast.makeText(activity, main.getUserLat.toString(), Toast.LENGTH_SHORT).show()

        val startPoint = GeoPoint(main.getUserLat, main.getUserLon)
        map.controller.setCenter(startPoint)

        val startMarker = Marker(map)
        startMarker.setPosition(startPoint)
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        map.getOverlays().add(startMarker)
    }

    private fun setMyLocationOverlay(){
        var myLocationOverlay=MyLocationNewOverlay(GpsMyLocationProvider(getActivity()),map)
        myLocationOverlay.enableMyLocation()
        map.overlays.add(myLocationOverlay)
    }

    private val requestPermissionLauncher=
        registerForActivityResult(ActivityResultContracts.RequestPermission())
        {
            isGranted:Boolean->
            if(isGranted){
                setMyLocationOverlay()
            }
        }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }


}