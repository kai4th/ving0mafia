package ving0.mafia

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

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap

    private val data: List<MapData> by lazy { parseData() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {

        map = googleMap

        data.forEach { spot ->
            val position = LatLng(spot.position.first, spot.position.second)
            val title = spot.company.replace("_", " ")
            val names: Array<String> = spot.names.filter { !TextUtils.isEmpty(it) }.toTypedArray()
            val nameArrayString = Arrays.toString(names)
            val nameString = nameArrayString.substring(1, nameArrayString.length - 1)
            val marker = MarkerOptions().position(position).title(title).snippet(nameString).icon(getIcon(title))
            map.addMarker(marker)
        }

        val vingle = LatLng(37.505281, 127.048383)

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(vingle, 12.0f))
        map.uiSettings.isZoomControlsEnabled = true
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

    data class MapData(val company: String, val position: Pair<Double, Double>, val names: Array<String>)
}
