package com.sae.wavetime

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.sae.wavetime.databinding.ActivityBlockedBinding

class BlockedActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_APP_NAME = "extra_app_name"

        const val EXTRA_PACKAGE_NAME = "extra_package_name"
    }

    private lateinit var binding: ActivityBlockedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBlockedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val appName = intent.getStringExtra(EXTRA_APP_NAME)
        val packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME)

        binding.tvBlockedTitle.text = getString(R.string.blocked_title, appName)

        binding.tvBlockedMessage.text =
            getString(R.string.blocked_message, packageName)

        binding.btnGoHome.setOnClickListener {
            goHome()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                goHome()
            }
        })
    }

    private fun goHome() {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        startActivity(intent)
        finishAndRemoveTask()
    }
}