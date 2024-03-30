package com.cryptica.stormly

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.glance.LocalContext
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition

object Widget : GlanceAppWidget() {
    @SuppressLint("ResourceType")
    @Composable
    override fun Content() {
        val context = LocalContext.current.applicationContext
        val sharedPreferences = context.getSharedPreferences("prefs", 0)
        val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(context.resources.getInteger(R.raw.snow)))
        LottieAnimation(
            composition = composition, modifier = Modifier.size(200.dp, 200.dp), iterations = LottieConstants.IterateForever
        )
        androidx.glance.text.Text(text = sharedPreferences.getString("City", "")!!
        )
    }
}

class SimpleWidgetRecevier : GlanceAppWidgetReceiver()
{
    override val glanceAppWidget : GlanceAppWidget
        get() = Widget
}