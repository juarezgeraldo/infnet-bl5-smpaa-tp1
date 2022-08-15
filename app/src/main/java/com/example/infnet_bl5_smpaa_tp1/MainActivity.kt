package com.example.infnet_bl5_smpaa_tp1

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*
import java.util.TimeZone.*

class MainActivity : AppCompatActivity(), LocationListener {
    val REQUEST_PERMISSIONS_CODE = 100
    val COARSE_REQUEST = 12345
    val FINE_REQUEST = 54321

    @SuppressLint("SimpleDateFormat")
    val dateFormat = SimpleDateFormat("yyyy-MM-dd")

    @SuppressLint("SimpleDateFormat")
    val horaFormat = SimpleDateFormat("HH-mm-ss")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        horaFormat.setTimeZone(TimeZone.getTimeZone("America/Sao_Paulo"))

        val btnGravar = this.findViewById<Button>(R.id.btnGravar)
        val btnListar = this.findViewById<Button>(R.id.btnListar)

        btnGravar.setOnClickListener() {
            acessaLocalizacao()
        }

        btnListar.setOnClickListener() {
            val listarIntent = Intent(this, ListaArquivos::class.java)
            startActivity(listarIntent)
        }
    }

    private fun acessaLocalizacao(): View.OnClickListener? {
//        this.getLocation("NET")
        this.getLocation("GPS")
        return null
    }

    private fun getLocation(tipo: String) {
        var location: Location? = null
        val locationManager =
            this.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager

        val isServiceEnable =
            locationManager.isProviderEnabled(if (tipo == "NET") LocationManager.NETWORK_PROVIDER else LocationManager.GPS_PROVIDER)
        if (isServiceEnable) {
            Log.i("DR4", "Indo pela Rede")
            if (checkSelfPermission(
                    if (tipo == "NET") android.Manifest.permission.ACCESS_COARSE_LOCATION else Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                locationManager.requestLocationUpdates(
                    if (tipo == "NET") LocationManager.NETWORK_PROVIDER else LocationManager.GPS_PROVIDER,
                    2000L,
                    0f,
                    this
                )
                location =
                    locationManager.getLastKnownLocation(if (tipo == "NET") LocationManager.NETWORK_PROVIDER else LocationManager.GPS_PROVIDER)
                val longitudeValor = location?.latitude.toString()
                val latitudeValor = location?.longitude.toString()
                gravaRegistro(longitudeValor, latitudeValor)
            } else {
                requestPermissions(
                    arrayOf(if (tipo == "NET") android.Manifest.permission.ACCESS_COARSE_LOCATION else Manifest.permission.ACCESS_FINE_LOCATION),
                    if (tipo == "NET") COARSE_REQUEST else FINE_REQUEST
                )
            }
        }
    }

    override fun onLocationChanged(p0: Location) {
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if ((requestCode == COARSE_REQUEST || requestCode == FINE_REQUEST) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            acessaLocalizacao()
        }
        if (requestCode == REQUEST_PERMISSIONS_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isExternalStorageWritable()) {
                    acessaLocalizacao()
                }
            }
        }
    }

    private fun gravaRegistro(longitude: String, latitude: String) {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this, Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                Toast.makeText(this, "É preciso liberar WRITE_EXTERNAL_STORAGE", Toast.LENGTH_LONG)
                    .show()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_PERMISSIONS_CODE
                )
            }
        } else {
            val dataAtual = dateFormat.format(Date())
            val horaAtual = horaFormat.format(Date())
            val filename = "GPS " + dataAtual + " " + horaAtual + ".crd"

            val registro = "Longitude: " + longitude + " Latitude: " + latitude

            try {
                val fileOutputStream = openFileOutput(filename, MODE_APPEND)
                fileOutputStream.write(registro.toByteArray())
                fileOutputStream.close()
            } catch (e: IOException) {
                Toast.makeText(this, "Erro de escrita em arquivo", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun isExternalStorageWritable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

}