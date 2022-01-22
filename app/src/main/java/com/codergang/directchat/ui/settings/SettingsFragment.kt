package com.codergang.directchat.ui.settings

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.codergang.directchat.BuildConfig
import com.codergang.directchat.R
import com.codergang.directchat.databinding.SettingsFragmentBinding
import com.codergang.directchat.ui.util.setSafeOnClickListener
import android.content.Intent

import android.content.ActivityNotFoundException
import android.net.Uri


class SettingsFragment : Fragment() {

    private lateinit var binding: SettingsFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SettingsFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
    }

    private fun setup() {
        binding.version.text = BuildConfig.VERSION_NAME
        binding.lnReview.setSafeOnClickListener {
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID)
                    )
                )
            } catch (e: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)
                    )
                )
            }
        }
        binding.lnShare.setSafeOnClickListener {
            shareApp()
        }
        binding.lnOtherApps.setSafeOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/dev?id=7102806663778142225")
                )
            )
        }
        binding.lnPrivacyPolicy.setSafeOnClickListener {
            openPolicy()
        }
        binding.lnTerms.setSafeOnClickListener {
            openTerms()
        }
    }

    private fun shareApp() {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = "text/plain"
        shareIntent.putExtra(
            Intent.EXTRA_TEXT,
            """
                ${getString(R.string.text_download_app)}
                
                https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}
            """.trimIndent()
        )
        startActivity(Intent.createChooser(shareIntent, getString(R.string.text_share_the_app)))
    }

    private fun openPolicy() {
        try {
            val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://codergangteam.com/?page_id=3"))
            startActivity(intent)
        }catch (e: Exception){ }
    }

    private fun openTerms() {
        try {
            val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://codergangteam.com/?page_id=10"))
            startActivity(intent)
        }catch (e: Exception){ }
    }
}