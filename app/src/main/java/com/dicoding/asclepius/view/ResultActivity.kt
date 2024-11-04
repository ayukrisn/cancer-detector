package com.dicoding.asclepius.view

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUri = intent.getStringExtra("IMAGE_URI")
        val classificationResults = intent.getStringArrayListExtra("CLASSIFICATION_RESULTS")

        binding.resultImage.setImageURI(Uri.parse(imageUri))
        classificationResults?.let {
            binding.resultText.text = it.joinToString("\n")
        }
    }


}