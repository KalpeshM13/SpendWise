package dev.kalpeshmore.spendwise.ui.tutorial

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.kalpeshmore.spendwise.R
import android.graphics.Outline
import android.view.ViewOutlineProvider

class TutorialAdapter(
    private val slides: List<TutorialSlide>
) : RecyclerView.Adapter<TutorialAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image = view.findViewById<ImageView>(R.id.image)
        val title = view.findViewById<TextView>(R.id.title)
        val description = view.findViewById<TextView>(R.id.description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tutorial, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val slide = slides[position]
        holder.image.setImageResource(slide.image)
        holder.title.text = slide.title
        holder.description.text = slide.description

        holder.image.layoutParams = holder.image.layoutParams.apply {
            width = 800
            height = 1300
        }
        holder.image.requestLayout()

        holder.image.apply {
            clipToOutline = true
            outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View, outline: Outline) {
                    val radius = 32 * view.resources.displayMetrics.density
                    outline.setRoundRect(0, 0, view.width, view.height, radius)
                }
            }

            alpha = 0f
            translationY = 80f

            animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(800)
                .start()
        }

        holder.title.apply {
            alpha = 0f

            animate()
                .alpha(1f)
                .setStartDelay(150)
                .setDuration(400)
                .start()
        }

        holder.description.apply {
            alpha = 0f

            animate()
                .alpha(1f)
                .setStartDelay(300)
                .setDuration(400)
                .start()
        }
    }

    override fun getItemCount() = slides.size
}