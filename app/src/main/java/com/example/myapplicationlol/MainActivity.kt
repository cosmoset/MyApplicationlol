package com.example.myapplicationlol

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import android.content.res.Configuration
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplicationlol.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var selectedImageUri: Uri? = null
    private val calendar = Calendar.getInstance()
    private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    // Using Activity Result API for image picking
    private val pickImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedImageUri = uri
                binding.imageViewProfile.setImageURI(uri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the action bar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = getString(R.string.form_title)
            setDisplayHomeAsUpEnabled(false)
        }

        setupDatePicker()
        setupImagePicker()
        setupSubmitButton()
        registerForContextMenu(binding.imageViewProfile)
    }

    private fun setupDatePicker() {
        binding.editTextBirthDate.setOnClickListener {
            DatePickerDialog(
                this,
                { _, year, month, day ->
                    calendar.set(year, month, day)
                    binding.editTextBirthDate.setText(dateFormatter.format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setupImagePicker() {
        binding.buttonSelectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImage.launch(intent)
        }
    }

    private fun setupSubmitButton() {
        binding.buttonSubmit.setOnClickListener {
            if (validateForm()) {
                val intent = Intent(this, DisplayInfoActivity::class.java).apply {
                    putExtra("name", binding.editTextName.text.toString())
                    putExtra("email", binding.editTextEmail.text.toString())
                    putExtra("phone", binding.editTextPhone.text.toString())
                    putExtra("birthDate", binding.editTextBirthDate.text.toString())
                    selectedImageUri?.let { putExtra("imageUri", it.toString()) }
                }
                startActivity(intent)
            }
        }
    }

    private fun validateForm(): Boolean {
        var isValid = true

        if (binding.editTextName.text.isNullOrEmpty()) {
            binding.editTextName.error = getString(R.string.error_field_required)
            isValid = false
        }

        if (binding.editTextEmail.text.isNullOrEmpty()) {
            binding.editTextEmail.error = getString(R.string.error_field_required)
            isValid = false
        }

        if (binding.editTextPhone.text.isNullOrEmpty()) {
            binding.editTextPhone.error = getString(R.string.error_field_required)
            isValid = false
        }

        if (binding.editTextBirthDate.text.isNullOrEmpty()) {
            binding.editTextBirthDate.error = getString(R.string.error_field_required)
            isValid = false
        }

        if (selectedImageUri == null) {
            Toast.makeText(this, getString(R.string.error_field_required), Toast.LENGTH_SHORT).show()
            isValid = false
        }

        return isValid
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                // Toggle between English and Amharic
                val currentLocale = resources.configuration.locales[0]
                val newLocale = if (currentLocale.language == "en") {
                    Locale("am")
                } else {
                    Locale("en")
                }

                // Update locale
                Locale.setDefault(newLocale)
                val config = Configuration(resources.configuration)
                config.setLocale(newLocale)
                resources.updateConfiguration(config, resources.displayMetrics)

                // Show toast message
                val message = if (newLocale.language == "am") {
                    "Switched to Amharic"
                } else {
                    "Switched to English"
                }
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

                // Recreate activity to apply changes
                recreate()
                true
            }
            R.id.action_help -> {
                Toast.makeText(this, getString(R.string.menu_help), Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_about -> {
                Toast.makeText(this, getString(R.string.menu_about), Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.context_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.context_edit -> {
                setupImagePicker()
                true
            }
            R.id.context_delete -> {
                selectedImageUri = null
                binding.imageViewProfile.setImageResource(android.R.drawable.ic_menu_gallery)
                true
            }
            R.id.context_copy -> {
                Toast.makeText(this, getString(R.string.context_copy), Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        selectedImageUri?.let {
            outState.putString("selectedImageUri", it.toString())
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        savedInstanceState.getString("selectedImageUri")?.let {
            selectedImageUri = Uri.parse(it)
            binding.imageViewProfile.setImageURI(selectedImageUri)
        }
    }
}