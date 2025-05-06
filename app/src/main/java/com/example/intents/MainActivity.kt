package com.example.intents

import android.Manifest.permission.CALL_PHONE
import android.content.Intent
import android.content.Intent.ACTION_CALL
import android.content.Intent.ACTION_CHOOSER
import android.content.Intent.ACTION_DIAL
import android.content.Intent.ACTION_PICK
import android.content.Intent.ACTION_VIEW
import android.content.Intent.EXTRA_INTENT
import android.content.Intent.EXTRA_TITLE
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.intents.Extras.PARAMETER_EXTRA
import com.example.intents.databinding.ActivityMainBinding
import androidx.core.net.toUri
import com.example.intents.Messages.CALL_ERROR
import com.example.intents.Messages.CHOOSE_BROWSER
import com.example.intents.Messages.OPEN_MESSAGE

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
            Intent(this, ParameterActivity::class.java).let {
                it.putExtra(PARAMETER_EXTRA, binding.parameterTv.text.toString())
                parameterArl.launch(it)
            }
        }

        parameterArl =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    result.data?.getStringExtra(PARAMETER_EXTRA).let {
                        binding.parameterTv.text = it
                    }
                }
            }

        cppArl =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { permissionGranted ->
                if (permissionGranted) {
                    callPhone(true)
                } else {
                    Toast.makeText(
                        this, CALL_ERROR, Toast.LENGTH_SHORT
                    ).show()
                }
            }

        pickImageArl =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    startActivity(Intent(ACTION_VIEW, result.data?.data))
                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.open_activity_mi -> {
                Toast.makeText(this, OPEN_MESSAGE, Toast.LENGTH_SHORT).show()
                true
            }

            R.id.view_mi -> {
                browserIntent()?.let { startActivity(it) }
                true
            }

            R.id.call_mi -> {
                if (checkSelfPermission(CALL_PHONE) == PERMISSION_GRANTED) callPhone(true)
                else cppArl.launch(CALL_PHONE)
                true
            }

            R.id.dial_mi -> {
                callPhone(false)
                true
            }

            R.id.pick_mi -> {
                val imageDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path
                val pickImageIntent = Intent(ACTION_PICK).apply {
                    setDataAndType(imageDir.toUri(), "image/*")
                }
                pickImageArl.launch(pickImageIntent)
                true
            }

            R.id.chooser_mi -> {
                val chooserIntent = Intent(ACTION_CHOOSER).apply {
                    putExtra(EXTRA_TITLE, CHOOSE_BROWSER)
                    putExtra(EXTRA_INTENT, browserIntent())
                }
                startActivity(chooserIntent)
                true
            }

            else -> false
        }
    }

    private fun callPhone(call: Boolean) {
        val number = "tel: ${binding.parameterTv.text}"
        val callIntent = Intent(if (call) ACTION_CALL else ACTION_DIAL)
        callIntent.data = number.toUri()
        startActivity(callIntent)
    }

    private fun browserIntent(): Intent? {
        val urlText = binding.parameterTv.text.toString().trim()
        return if (urlText.startsWith("http://") || urlText.startsWith("https://")) {
            Intent(ACTION_VIEW, urlText.toUri())
        } else {
            Toast.makeText(this, "URL inválida!", Toast.LENGTH_SHORT).show()
            null
        }
    }
}