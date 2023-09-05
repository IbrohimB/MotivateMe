package com.botirov.motivateme

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import java.util.Timer
import java.util.TimerTask

class MainActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var saveButton: ImageButton
    private lateinit var likeButton: ImageButton

    private val likedQuotes = HashSet<Int>()

    private val quotes = listOf(
        "The only way to do great work is to love what you do.",
        "Life is what happens when you’re busy making other plans.",
        "The world breaks everyone, and afterward, some are strong at the broken places.",
        "Don't count the days, make the days count.",
        "Success is not final, failure is not fatal: it is the courage to continue that counts.",
        "Act as if what you do makes a difference. It does.",
        "Do not wait to strike till the iron is hot, but make it hot by striking.",
        "Believe you can and you're halfway there.",
        "The harder you work for something, the greater you'll feel when you achieve it.",
        "Don't stop when you're tired. Stop when you're done.",
        "Success is not in what you have, but who you are.",
        "It always seems impossible until it's done.",
        "The only place where success comes before work is in the dictionary.",
        "The road to success and the road to failure are almost exactly the same.",
        "The best time to plant a tree was 20 years ago. The second best time is now.",
        "You are never too old to set another goal or to dream a new dream.",
        "Your time is limited, don’t waste it living someone else’s life.",
        "Success usually comes to those who are too busy to be looking for it.",
        "The future belongs to those who believe in the beauty of their dreams.",
        "If you can dream it, you can do it."
    )

    private val autoSwitchTask = object : TimerTask() {
        override fun run() {
            runOnUiThread {
                if (viewPager.currentItem < quotes.size - 1) {
                    viewPager.currentItem = viewPager.currentItem + 1
                } else {
                    viewPager.currentItem = 0
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        viewPager = findViewById(R.id.viewPager)
        viewPager.adapter = QuoteAdapter(quotes)

        saveButton = findViewById(R.id.saveButton)
        likeButton = findViewById(R.id.likeButton)

        saveButton.setOnClickListener {
            val bitmap = captureScreenshot()
            saveBitmapToGallery(this, bitmap, "motivational_quote_${System.currentTimeMillis()}.png")
        }

        likeButton.setOnClickListener {
            val currentIndex = viewPager.currentItem
            if (likedQuotes.contains(currentIndex)) {
                likedQuotes.remove(currentIndex)
                likeButton.setImageResource(R.drawable.ic_favorite)
            } else {
                likedQuotes.add(currentIndex)
                likeButton.setImageResource(R.drawable.ic_favorite_selected)
            }
        }

        val timer = Timer()
        timer.schedule(autoSwitchTask, 10000, 10000)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (this@MainActivity.likedQuotes.contains(position)) {
                    this@MainActivity.likeButton.setImageResource(R.drawable.ic_favorite_selected)
                } else {
                    this@MainActivity.likeButton.setImageResource(R.drawable.ic_favorite)
                }
            }
        })
    }

    private fun captureScreenshot(): Bitmap {
        val rootView = findViewById<View>(R.id.rootLayout)
        val bitmap = Bitmap.createBitmap(rootView.width, rootView.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        rootView.draw(canvas)
        return bitmap
    }

    private fun saveBitmapToGallery(context: Context, bitmap: Bitmap, filename: String) {
        val savedImageURL = MediaStore.Images.Media.insertImage(
            context.contentResolver,
            bitmap,
            filename,
            "Motivational Quote"
        )
        Toast.makeText(context, "Saved to gallery!", Toast.LENGTH_SHORT).show()
    }


    override fun onDestroy() {
        autoSwitchTask.cancel() // Ensure to cancel the timer when the activity is destroyed to prevent leaks
        super.onDestroy()
    }

}
