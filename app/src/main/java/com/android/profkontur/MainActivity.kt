package com.android.profkontur

import android.os.Build
import android.os.Bundle
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.android.profkontur.databinding.ActivityMainBinding
import com.google.android.material.color.MaterialColors.isColorLight

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        setStatusBarColor(this,R.color.main_gray_background_color)

        val navController = findNavController(R.id.nav_host_fragment_activity_main)


        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_dashboard,R.id.navigation_home, R.id.navigation_notifications
            )
        )
        navView.setupWithNavController(navController)
    }

    // Предполагается, что у вас есть доступ к Activity context
    fun setStatusBarColor(activity: AppCompatActivity, colorResId: Int) {
        val window = activity.window
        val color = ContextCompat.getColor(activity, colorResId)
        window.statusBarColor = color

        // Для Android 6.0 (API level 23) и выше, также необходимо настроить флаги, чтобы текст оставался видимым
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Проверяем, светлый ли цвет фона
            val isLightColor = isColorLight(color)

            // Устанавливаем флаги в зависимости от светлоты цвета
            if (isLightColor) {
                // Для светлого фона устанавливаем SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                // Для темного фона убираем SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                window.decorView.systemUiVisibility = window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
        }
    }
}