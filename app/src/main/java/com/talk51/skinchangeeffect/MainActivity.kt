package com.talk51.skinchangeeffect

import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var darkMode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            var uiVisibility = window.decorView.systemUiVisibility
            uiVisibility = uiVisibility.or(View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
                    .or(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                uiVisibility = uiVisibility.or(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
            }
            window.decorView.systemUiVisibility = uiVisibility
            window.statusBarColor = Color.TRANSPARENT
        }

        setContentView(R.layout.activity_main)

        val animView = CircleEffectView(this)

        btn_go_effect.setOnClickListener {
            animView.start(it)
            if (darkMode) {
                root_view.setBackgroundColor(Color.parseColor("#ffffff"))
            } else {
                root_view.setBackgroundColor(Color.parseColor("#000000"))
            }
            darkMode = !darkMode
        }

    }
}
