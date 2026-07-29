package dev.kalpeshmore.spendwise.ui.tutorial

import android.content.Intent
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator
import dev.kalpeshmore.spendwise.R
import dev.kalpeshmore.spendwise.ui.main.MainActivity
import androidx.core.content.edit
import com.google.android.material.button.MaterialButton
import android.graphics.Outline
import android.view.ViewOutlineProvider

class TutorialActivity : AppCompatActivity() {

    private val slides = listOf(
        TutorialSlide(
            R.mipmap.ic_launcher, "Welcome",
            "Welcome to SpendWise."),

        TutorialSlide(
            R.drawable.dashboard, "Dashboard",
            "Clean Dashboard. Zero Mess."),

        TutorialSlide(
            R.drawable.transactions, "Transactions",
            "View previous transactions anytime."),

        TutorialSlide(
            R.drawable.transaction_details, "Transaction Details",
            "Complete details of transaction."),

        TutorialSlide(
            R.drawable.manage_categories, "Manage Categories",
            "Create or Edit Categories on your will."),

        TutorialSlide(
            R.drawable.ready, "Ready?",
            "Let's get started!")

    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tutorial)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        val nextButton = findViewById<MaterialButton>(R.id.btnNext)
        val btnPrevious = findViewById<MaterialButton>(R.id.btnPrevious)
        val dotsIndicator = findViewById<WormDotsIndicator>(R.id.dotsIndicator)


        viewPager.adapter = TutorialAdapter(slides)
        dotsIndicator.attachTo(viewPager)

        btnPrevious.visibility = View.INVISIBLE
        nextButton.text = getString(R.string.next)

        btnPrevious.setOnClickListener {
            if (viewPager.currentItem > 0) {
                viewPager.currentItem--
            }
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        }

        nextButton.setOnClickListener {
            if (viewPager.currentItem < slides.lastIndex) {
                viewPager.currentItem++
            } else {
                getSharedPreferences("AppPrefs", MODE_PRIVATE)
                    .edit {
                        putBoolean("firstLaunch", false)
                    }
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }

            nextButton.animate()
                .scaleX(1.05f)
                .scaleY(1.05f)
                .setDuration(120)
                .withEndAction {
                    nextButton.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(120)
                        .start()
                }
                .start()

            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {

                btnPrevious.visibility =
                    if (position == 0) View.INVISIBLE else View.VISIBLE

                nextButton.text =
                    if (position == slides.lastIndex)
                        "Get Started"
                    else
                        "Next"
            }
        })

        viewPager.setPageTransformer { page, position ->
            val imageView = page.findViewById<View>(R.id.image)
            imageView?.clipToOutline = true
            imageView?.outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View, outline: Outline) {
                    val radius = 48 * view.resources.displayMetrics.density
                    outline.setRoundRect(0, 0, view.width, view.height, radius)
                }
            }

            page.apply {
                val absPos = kotlin.math.abs(position)
                alpha = 1 - absPos * 0.35f
                scaleX = 0.85f + (1 - absPos) * 0.15f
                scaleY = 0.85f + (1 - absPos) * 0.15f
                translationX = -position * 40
            }
        }

    }
}