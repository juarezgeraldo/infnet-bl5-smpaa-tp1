package com.example.infnet_bl5_smpaa_tp1

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import java.io.File


class ListaArquivos : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_arquivos)

        val btnVoltar = this.findViewById<Button>(R.id.btnVoltar)

        btnVoltar.setOnClickListener() {
            val profileIntent = Intent(this, MainActivity::class.java)
            startActivity(profileIntent)
        }
    }

    override fun onResume() {
        super.onResume()

        val file: File
        val minhaLista: MutableList<String>
        minhaLista = ArrayList()

        val root_sd = Environment.getExternalStorageDirectory().toString()
        file = File("//data/data/com.example.infnet_bl5_smpaa_tp1/files")
        val list = file.listFiles()

        for (i in list.indices) {
            minhaLista.add(list[i].name)
        }

        val adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1, minhaLista
        )

        val lstArquivos = this.findViewById<ListView>(R.id.lstArquivos)
        lstArquivos.adapter = adapter

    }
}
