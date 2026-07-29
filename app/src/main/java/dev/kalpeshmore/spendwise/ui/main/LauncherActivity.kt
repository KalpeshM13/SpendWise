package dev.kalpeshmore.spendwise.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dev.kalpeshmore.spendwise.R
import dev.kalpeshmore.spendwise.ui.tutorial.TutorialActivity

class LauncherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_launcher)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val appVersion =  findViewById<TextView>(R.id.txtVersion)
        val versionName = this
            .packageManager
            .getPackageInfo(this.packageName, 0)
            .versionName
        appVersion.text = getString(R.string.app_version, versionName)


        val prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        val firstLaunch = prefs.getBoolean("firstLaunch", true)

        if (firstLaunch) {
            startActivity(Intent(this, TutorialActivity::class.java))
        } else {
            startActivity(Intent(this, MainActivity::class.java))
        }

        finish()
    }
}