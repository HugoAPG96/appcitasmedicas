package com.example.appcitasmedicas.Activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.appcitasmedicas.AppointmentActivity
import com.example.appcitasmedicas.Domain.DoctorsModel
import com.example.appcitasmedicas.databinding.ActivityDetailBinding

class DetailActivity : BaseActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var item: DoctorsModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getBundle()
    }

    private fun getBundle() {
        item = intent.getParcelableExtra<DoctorsModel>("object")!!

        binding.apply {
            titleTxt.text = item.Name
            specialTxt.text = item.Special
            patiensTxt.text = item.Patients
            bioTxt.text = item.Biography
            addressTxt.text = item.Address

            experienceTxt.text = "${item.Expriense} Años"
            ratingTxt.text = item.Rating.toString()

            backBtn.setOnClickListener { finish() }

            websiteBtn.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(item.Site)
                startActivity(intent)
            }

            messageBtn.setOnClickListener {
                val uri = Uri.parse("smsto:${item.Mobile}")
                val intent = Intent(Intent.ACTION_SENDTO, uri)
                intent.putExtra("sms_body", "the SMS text")
                startActivity(intent)
            }

            callBtn.setOnClickListener {
                val uri = "tel:${item.Mobile.trim()}"
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse(uri))
                startActivity(intent)
            }

            directionBtn.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.Location))
                startActivity(intent)
            }

            shareBtn.setOnClickListener {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_SUBJECT, item.Name)
                intent.putExtra(Intent.EXTRA_TEXT, "${item.Name} ${item.Address} ${item.Mobile}")
                startActivity(Intent.createChooser(intent, "Choose one"))
            }

            makeBtn.setOnClickListener {
                // Inicia la actividad del formulario de citas
                val intent = Intent(this@DetailActivity, AppointmentActivity::class.java)

                // Pasa el doctorId como Int directamente
                intent.putExtra("doctorId", item.Id) // No es necesario convertir, ya que item.Id es un Int

                // Pasa los demás valores
                intent.putExtra("doctorName", item.Name)
                //intent.putExtra("doctorSpecialty", item.Special)
                intent.putExtra("especialidad", item.Special)

                startActivity(intent)
            }

            Glide.with(this@DetailActivity)
                .load(item.Picture)
                .into(img)
        }
    }
}
