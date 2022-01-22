package com.codergang.directchat.ui.start

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.OneShotPreDrawListener
import com.codergang.directchat.R
import com.codergang.directchat.data.preferences.UserPreferences
import com.codergang.directchat.databinding.ActivityStartBinding
import com.codergang.directchat.ui.main.MainActivity
import com.codergang.directchat.ui.presentation.PresentationActivity

class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        splashValidation()
    }

    private fun splashValidation(){
        // Set up an OnPreDrawListener to the root view.
        val content: View = findViewById(android.R.id.content)
        OneShotPreDrawListener.add(content) {
            if (UserPreferences.get(applicationContext) == null) {
                startActivity(Intent(this@StartActivity, PresentationActivity::class.java))
                finish()
            }else {
                startActivity(Intent(this@StartActivity, MainActivity::class.java))
                finish()
            }
        }
    }

}