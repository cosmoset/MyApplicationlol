package com.example.myapplicationlol
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplicationlol.databinding.ActivityDisplayInfoBinding

class DisplayInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDisplayInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDisplayInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the action bar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = getString(R.string.display_info)
            setDisplayHomeAsUpEnabled(true)
        }

        // Get the data from the intent
        val name = intent.getStringExtra("name") ?: ""
        val email = intent.getStringExtra("email") ?: ""
        val phone = intent.getStringExtra("phone") ?: ""
        val birthDate = intent.getStringExtra("birthDate") ?: ""
        val imageUriString = intent.getStringExtra("imageUri")

        // Display the data
        binding.textViewName.text = "${getString(R.string.full_name)}: $name"
        binding.textViewEmail.text = "${getString(R.string.email)}: $email"
        binding.textViewPhone.text = "${getString(R.string.phone)}: $phone"
        binding.textViewBirthDate.text = "${getString(R.string.birth_date)}: $birthDate"

        // Set the image if available
        imageUriString?.let {
            binding.imageViewDisplay.setImageURI(Uri.parse(it))
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}