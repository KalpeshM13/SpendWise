package dev.kalpeshmore.spendwise.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import dev.kalpeshmore.spendwise.data.database.AppDatabase
import dev.kalpeshmore.spendwise.data.firebase.FirebaseService
import dev.kalpeshmore.spendwise.data.models.UserProfile
import dev.kalpeshmore.spendwise.data.repository.SpendwiseRepo
import dev.kalpeshmore.spendwise.databinding.ActivityRegisterBinding
import dev.kalpeshmore.spendwise.ui.main.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()

        // Setup gender dropdown
        val genderOptions = arrayOf("Female", "Male")
        val genderAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, genderOptions)
        binding.etGender.setAdapter(genderAdapter)

        binding.btnBackToLogin.setOnClickListener {
            finish()
        }

        binding.btnSubmit.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()
            val gender = binding.etGender.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || gender.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Map gender to avatar
            val avatar = if (gender == "Male") "boy" else "girl"

            binding.progressBar.visibility = View.VISIBLE
            binding.btnSubmit.isEnabled = false

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build()

                        user?.updateProfile(profileUpdates)
                            ?.addOnCompleteListener { updateTask ->
                                if (updateTask.isSuccessful) {
                                    // Save user profile to Firestore
                                    val db = AppDatabase.getDatabase(applicationContext)
                                    val repo = SpendwiseRepo(
                                        db.transactionDao(),
                                        db.categoryDao(),
                                        FirebaseService()
                                    )
                                    CoroutineScope(Dispatchers.IO).launch {
                                        val profile = UserProfile(
                                            name = name,
                                            email = email,
                                            phone = phone,
                                            avatar = avatar
                                        )
                                        repo.saveUserProfile(profile)
                                        // Sync any existing local data to cloud
                                        repo.syncLocalToCloud()
                                    }

                                    binding.progressBar.visibility = View.GONE
                                    binding.btnSubmit.isEnabled = true
                                    Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this, MainActivity::class.java))
                                    finishAffinity()
                                } else {
                                    binding.progressBar.visibility = View.GONE
                                    binding.btnSubmit.isEnabled = true
                                    Toast.makeText(this, "Failed to update profile.", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        binding.progressBar.visibility = View.GONE
                        binding.btnSubmit.isEnabled = true
                        Toast.makeText(this, "Registration failed.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
