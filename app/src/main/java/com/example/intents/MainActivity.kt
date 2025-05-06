package com.example.intents

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.example.intents.Extras.PARAMETER_EXTRA
import com.example.intents.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var parameterArl: ActivityResultLauncher<Intent>
    private lateinit var cppArl: ActivityResultLauncher<String>
    private lateinit var pickImageArl: ActivityResultLauncher<Intent>

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarIn.toolbar)
        supportActionBar?.subtitle = localClassName

        binding.parameterBt.setOnClickListener {
            Intent("OPEN_PARAMETER_ACTIVITY_ACTION").let {
                it.putExtra(PARAMETER_EXTRA, binding.parameterTv.text.toString())
                parameterArl.launch(it)
            }
        }

    }
}