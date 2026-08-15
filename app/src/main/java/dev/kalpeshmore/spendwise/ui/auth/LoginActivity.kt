package dev.kalpeshmore.spendwise.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import dev.kalpeshmore.spendwise.R
import dev.kalpeshmore.spendwise.data.database.AppDatabase
import dev.kalpeshmore.spendwise.data.firebase.FirebaseService
import dev.kalpeshmore.spendwise.data.repository.SpendwiseRepo
import dev.kalpeshmore.spendwise.databinding.ActivityLoginBinding
import dev.kalpeshmore.spendwise.ui.main.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private var isEmailMode = true
    private var verificationId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()

        // binding.toggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
        //     if (isChecked) {
        //         isEmailMode = checkedId == R.id.btnEmailMode
        //         updateUI()
        //     }
        // }

        binding.btnCreateAccount.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // binding.btnSendOtp.setOnClickListener {
        //     sendOtp()
        // }

        binding.btnSubmit.setOnClickListener {
            loginWithEmail()
        }
    }

    private fun updateUI() {
        if (isEmailMode) {
            binding.emailContainer.visibility = View.VISIBLE
            binding.phoneContainer.visibility = View.GONE
            binding.btnSubmit.text = "Login"
        } else {
            binding.emailContainer.visibility = View.GONE
            binding.phoneContainer.visibility = View.VISIBLE
            binding.btnSubmit.text = "Verify & Login"
        }
    }

    private fun loginWithEmail() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            return
        }

        showLoading(true)

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                showLoading(false)
                if (task.isSuccessful) {
                    navigateToMain()
                } else {
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun sendOtp() {
        var phoneNumber = binding.etPhone.text.toString().trim()
        if (phoneNumber.isEmpty()) {
            Toast.makeText(this, "Please enter phone number", Toast.LENGTH_SHORT).show()
            return
        }

        // Automatically prepend +91 if the user didn't enter a country code
        if (!phoneNumber.startsWith("+")) {
            phoneNumber = "+91$phoneNumber"
        }

        showLoading(true)

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    showLoading(false)
                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    showLoading(false)
                    Toast.makeText(this@LoginActivity, "Verification failed: ${e.message}", Toast.LENGTH_LONG).show()
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    showLoading(false)
                    this@LoginActivity.verificationId = verificationId
                    binding.tilOtp.visibility = View.VISIBLE
                    Toast.makeText(this@LoginActivity, "OTP Sent", Toast.LENGTH_SHORT).show()
                }
            })
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun verifyOtpAndLogin() {
        val otp = binding.etOtp.text.toString().trim()
        if (otp.isEmpty() || verificationId.isEmpty()) {
            Toast.makeText(this, "Please enter valid OTP", Toast.LENGTH_SHORT).show()
            return
        }

        showLoading(true)
        val credential = PhoneAuthProvider.getCredential(verificationId, otp)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                showLoading(false)
                if (task.isSuccessful) {
                    navigateToMain()
                } else {
                    Toast.makeText(this, "Login failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnSubmit.isEnabled = !isLoading
    }

    private fun navigateToMain() {
        // Sync data from cloud to local Room DB on login
        val db = AppDatabase.getDatabase(applicationContext)
        val repo = SpendwiseRepo(
            db.transactionDao(),
            db.categoryDao(),
            FirebaseService()
        )
        CoroutineScope(Dispatchers.IO).launch {
            repo.syncFromCloud()
        }
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
