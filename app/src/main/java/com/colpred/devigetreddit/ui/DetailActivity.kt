package com.colpred.devigetreddit.ui

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.colpred.devigetreddit.R
import com.colpred.devigetreddit.ui.fragments.DetailFragment
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // If the screen is now in landscape mode, this activity is not needed.
            finish()
            return
        }

        if (savedInstanceState == null) {
            // During initial setup, plug in the details fragment.
            val details = DetailFragment().apply {
                arguments = intent.extras
            }
            supportFragmentManager.beginTransaction()
                .add(R.id.detail, details)
                .commit()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return false
    }
}