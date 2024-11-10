package com.example.metricconverter

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var spinnerMetrik: Spinner
    private lateinit var spinnerAsal: Spinner
    private lateinit var spinnerTujuan: Spinner
    private lateinit var inputNilai: EditText
    private lateinit var teksPerhitungan: TextView

    private val daftarSatuan = mapOf(
        "Panjang" to listOf(
            Satuan("Nanometer", 1e-9),
            Satuan("Mikrometer", 1e-6),
            Satuan("Milimeter", 1e-3),
            Satuan("Sentimeter", 1e-2),
            Satuan("Desimeter", 1e-1),
            Satuan("Meter", 1.0),
            Satuan("Kilometer", 1e3),
            Satuan("Mil", 1609.34),
            Satuan("Yard", 0.9144),
            Satuan("Kaki", 0.3048),
            Satuan("Inci", 0.0254)
        ),
        "Massa" to listOf(
            Satuan("Mikrogram", 1e-9),
            Satuan("Miligram", 1e-6),
            Satuan("Gram", 1e-3),
            Satuan("Kilogram", 1.0),
            Satuan("Ton Metrik", 1e3),
            Satuan("Pon", 0.453592),
            Satuan("Ons", 0.0283495)
        ),
        "Waktu" to listOf(
            Satuan("Nanodetik", 1e-9),
            Satuan("Mikrodetik", 1e-6),
            Satuan("Milidetik", 1e-3),
            Satuan("Detik", 1.0),
            Satuan("Menit", 60.0),
            Satuan("Jam", 3600.0),
            Satuan("Hari", 86400.0),
            Satuan("Minggu", 604800.0),
            Satuan("Bulan", 2.628e6),
            Satuan("Tahun", 3.156e7)
        ),
        "Suhu" to listOf(
            Satuan("Celsius", 1.0),
            Satuan("Fahrenheit", 1.0),
            Satuan("Kelvin", 1.0)
        ),
        "Luas" to listOf(
            Satuan("Milimeter Persegi", 1e-6),
            Satuan("Sentimeter Persegi", 1e-4),
            Satuan("Meter Persegi", 1.0),
            Satuan("Hektar", 1e4),
            Satuan("Kilometer Persegi", 1e6),
            Satuan("Mil Persegi", 2.59e6),
            Satuan("Acre", 4046.86)
        ),
        "Volume" to listOf(
            Satuan("Mililiter", 1e-6),
            Satuan("Sentimeter Kubik", 1e-6),
            Satuan("Liter", 1e-3),
            Satuan("Meter Kubik", 1.0),
            Satuan("Galon (AS)", 0.00378541),
            Satuan("Quart (AS)", 0.000946353),
            Satuan("Pint (AS)", 0.000473176),
            Satuan("Gelas (AS)", 0.000236588)
        ),
        "Kecepatan" to listOf(
            Satuan("Meter per Detik", 1.0),
            Satuan("Kilometer per Jam", 0.277778),
            Satuan("Mil per Jam", 0.44704),
            Satuan("Knot", 0.514444)
        )
    )

    data class Satuan(val nama: String, val faktorKonversi: Double)

    private fun konversiNilai(nilai: Double, dari: Satuan, ke: Satuan, kategori: String): Double {
        return when (kategori) {
            "Suhu" -> konversiSuhu(nilai, dari.nama, ke.nama)
            else -> nilai * dari.faktorKonversi / ke.faktorKonversi
        }
    }

    private fun konversiSuhu(nilai: Double, dari: String, ke: String): Double {
        return when {
            dari == ke -> nilai
            dari == "Celsius" && ke == "Fahrenheit" -> (nilai * 9 / 5) + 32
            dari == "Celsius" && ke == "Kelvin" -> nilai + 273.15
            dari == "Fahrenheit" && ke == "Celsius" -> (nilai - 32) * 5 / 9
            dari == "Fahrenheit" && ke == "Kelvin" -> (nilai - 32) * 5 / 9 + 273.15
            dari == "Kelvin" && ke == "Celsius" -> nilai - 273.15
            dari == "Kelvin" && ke == "Fahrenheit" -> (nilai - 273.15) * 9 / 5 + 32
            else -> nilai
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        spinnerMetrik = findViewById(R.id.spMetrics)
        spinnerAsal = findViewById(R.id.spOriginal)
        spinnerTujuan = findViewById(R.id.spConvert)
        inputNilai = findViewById(R.id.getInputValue)
        teksPerhitungan = findViewById(R.id.resultText)

        val kategori = listOf("Pilih Kategori") + daftarSatuan.keys.toList()
        spinnerMetrik.adapter = ArrayAdapter(this, R.layout.spinner_item, kategori)

        spinnerAsal.isEnabled = false
        spinnerTujuan.isEnabled = false

        aturListenerSpinner()
        aturListenerInput()
    }

    private fun aturListenerSpinner() {
        spinnerMetrik.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, posisi: Int, id: Long) {
                if (posisi == 0) {
                    spinnerAsal.isEnabled = false
                    spinnerTujuan.isEnabled = false
                    return
                }

                val kategori = spinnerMetrik.selectedItem.toString()
                val satuan = listOf("Pilih Satuan") + (daftarSatuan[kategori]?.map { it.nama } ?: listOf())
                val adapter = ArrayAdapter(this@MainActivity, R.layout.spinner_item, satuan)

                spinnerAsal.adapter = adapter
                spinnerTujuan.adapter = adapter
                spinnerAsal.isEnabled = true
                spinnerTujuan.isEnabled = true

                hitungDanTampilkanHasil()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val listenerItem = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, posisi: Int, id: Long) {
                hitungDanTampilkanHasil()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerAsal.onItemSelectedListener = listenerItem
        spinnerTujuan.onItemSelectedListener = listenerItem
    }

    private fun aturListenerInput() {
        inputNilai.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                hitungDanTampilkanHasil()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun hitungDanTampilkanHasil() {
        if (spinnerMetrik.selectedItemPosition == 0 ||
            spinnerAsal.selectedItemPosition == 0 ||
            spinnerTujuan.selectedItemPosition == 0 ||
            inputNilai.text.isEmpty()
        ) {
            teksPerhitungan.text = "0"
            return
        }

        try {
            val nilai = inputNilai.text.toString().toDouble()
            val kategori = spinnerMetrik.selectedItem.toString()
            val satuan = daftarSatuan[kategori] ?: return
            val satuanAsal = satuan.find { it.nama == spinnerAsal.selectedItem.toString() } ?: return
            val satuanTujuan = satuan.find { it.nama == spinnerTujuan.selectedItem.toString() } ?: return

            val hasil = konversiNilai(nilai, satuanAsal, satuanTujuan, kategori)

            val hasilFormat = when {
                hasil % 1.0 == 0.0 -> hasil.toInt().toString()
                else -> String.format(Locale.US, "%.4f", hasil)
            }

            teksPerhitungan.text = getString(R.string.conversion_result, hasilFormat, satuanTujuan.nama)
        } catch (e: NumberFormatException) {
            teksPerhitungan.text = "Input tidak valid"
        }
    }
}