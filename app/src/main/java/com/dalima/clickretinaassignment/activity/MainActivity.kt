package com.dalima.clickretinaassignment.activity

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.dalima.clickretinaassignment.data.User
import com.dalima.clickretinaassignment.databinding.ActivityMainBinding
import com.dalima.clickretinaassignment.network.ProfileRepository
import com.dalima.clickretinaassignment.viewmodel.ProfileViewModel
import com.dalima.clickretinaassignment.viewmodel.ProfileViewModelFactory
import com.dalima.clickretinaassignment.viewmodel.UiState

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val repo = ProfileRepository()
        viewModel = ViewModelProvider(this, ProfileViewModelFactory(repo))
            .get(ProfileViewModel::class.java)

        observe()
        setListeners()
        viewModel.fetchProfile()
    }

    private fun observe() {
        viewModel.uiState.observe(this) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.contentGroup.visibility = View.GONE
                    binding.errorGroup.visibility = View.GONE
                }
                is UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.contentGroup.visibility = View.VISIBLE
                    binding.errorGroup.visibility = View.GONE
                    bindProfile(state.user)
                }
                is UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.contentGroup.visibility = View.GONE
                    binding.errorGroup.visibility = View.VISIBLE
                    binding.tvError.text = state.message
                }
            }
        }
    }

    private fun bindProfile(user: User) {
        // Basic info
        binding.tvName.text = user.name
        binding.tvUsername.text = user.username
        binding.tvLocation.text = "${user.location?.city}, ${user.location?.country}"

        // Stats
        binding.tvFollowersCount.text = user.statistics?.followers.toString()
        binding.tvFollowingCount.text = user.statistics?.following.toString()
        binding.tvShotsCount.text = user.statistics?.activity?.shots.toString()
        binding.tvCollectionsCount.text = user.statistics?.activity?.collections.toString()

        // Avatar
        Glide.with(this)
            .load(user.avatar)
            .centerCrop()
            .into(binding.ivAvatar)

        // Website link
        binding.tvWebsite.text = user.social?.website
        binding.tvWebsite.setOnClickListener {
            openLink(user.social?.website)
        }

        // Social profiles (Instagram, Facebook, etc.)
        // Hide all first
        binding.btnInstagram.visibility = View.GONE
        binding.btnFacebook.visibility = View.GONE

        user.social?.profiles?.forEach { profile ->
            when (profile.platform?.lowercase()) {
                "instagram" -> {
                    binding.btnInstagram.visibility = View.VISIBLE
                    binding.btnInstagram.setOnClickListener { openLink(profile.url) }
                }
                "facebook" -> {
                    binding.btnFacebook.visibility = View.VISIBLE
                    binding.btnFacebook.setOnClickListener { openLink(profile.url) }
                }
            }
        }
    }

    private fun setListeners() {
        binding.btnRetry.setOnClickListener {
            viewModel.fetchProfile()
        }
    }

    private fun openLink(url: String?) {
        try {
            if (url.isNullOrBlank()) {
                Toast.makeText(this, "Invalid link", Toast.LENGTH_SHORT).show()
                return
            }

            val uri = if (url.startsWith("http")) {
                Uri.parse(url)
            } else {
                Uri.parse("https://$url")
            }

            val customTabsIntent = CustomTabsIntent.Builder().build()
            customTabsIntent.launchUrl(this, uri)
        } catch (e: Exception) {
            Toast.makeText(this, "Cannot open link", Toast.LENGTH_SHORT).show()
        }
    }

}