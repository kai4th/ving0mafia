package ving0.mafia.ving0mafia

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import java.io.InputStreamReader
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.InfoWindowAdapter {

    private lateinit var map: GoogleMap

    private val data: List<MapData> by lazy { parseData() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {

        map = googleMap

        // Add a marker in Sydney and move the camera

        data.forEachIndexed { index, spot ->
            val position = LatLng(spot.position.first, spot.position.second)
            val title = spot.company.replace("_", " ")
            val names: Array<String> = spot.names.filter { !TextUtils.isEmpty(it) }.toTypedArray()

            val marker = MarkerOptions().position(position).title(title).snippet(Arrays.toString(names)).icon(getIcon(title))
            map.addMarker(marker)
        }

//        val naver = LatLng(37.359468, 127.105357)
//        val naverMarker = MarkerOptions().position(naver).title("Naver GreenFactory")
//        map.addMarker(naverMarker)
        val vingle = LatLng(37.505281, 127.048383)
//        map.addMarker(MarkerOptions().position(vingle).title("Vingle"))

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(vingle, 12.0f))
        map.uiSettings.isZoomControlsEnabled = true
//        map.setInfoWindowAdapter(this)
    }

    private fun getIcon(title: String): BitmapDescriptor {
        return try {
            BitmapDescriptorFactory.fromResource(IconMap.valueOf(title.replace(" ", "_").toUpperCase()).res)
        } catch (e: Throwable) {
            BitmapDescriptorFactory.defaultMarker()
        }
    }

    private fun parseData(): List<MapData> {
        val mapData = ArrayList<MapData>()
        assets.open("ving0_map_data.csv").use {
            val reader = InputStreamReader(it)
            reader.readLines().forEach { line ->
                val data = line.split(",").toTypedArray()
                if (data.size > 3) {

                    val company = data[0]
                    val position = Pair(data[1].toDouble(), data[2].toDouble())

                    val names = data.copyOfRange(3, data.size)
                    mapData.add(MapData(company, position, names))
                }

            }
        }
        return mapData
    }

    override fun getInfoContents(marker: Marker?): View {
        val view = LayoutInflater.from(this).inflate(R.layout.infowindow, null)
        view.findViewById<ImageView>(R.id.thumb).setImageResource(R.drawable.naver)
        view.findViewById<TextView>(R.id.name).setText("wonsik\nyounghoon")
        return view

    }

    override fun getInfoWindow(marker: Marker?): View? {
        return null;
    }

    data class MapData(val company: String, val position: Pair<Double, Double>, val names: Array<String>)
}
