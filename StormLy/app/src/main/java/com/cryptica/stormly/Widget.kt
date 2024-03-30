package com.cryptica.stormly

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.widget.Space
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.res.FontResourcesParserCompat.FontFileResourceEntry
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.proto.LayoutProto.HorizontalAlignment
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.absolutePadding
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontStyle
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import java.text.SimpleDateFormat
import kotlin.math.roundToInt

object Widget : GlanceAppWidget() {
    @SuppressLint("ResourceType")
    @Composable
    override fun Content() {
        val context = LocalContext.current.applicationContext
        val sharedPreferences = context.getSharedPreferences("prefs", 0)
        
        LazyColumn(modifier = GlanceModifier.fillMaxSize().appWidgetBackground().background(
            SetBackgroundColor()
        ), horizontalAlignment = Alignment.CenterHorizontally){
            items(1) {index: Int ->
                Column(modifier = GlanceModifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(modifier = GlanceModifier.fillMaxWidth().height(30.dp))
                    androidx.glance.text.Text(text = sharedPreferences.getString("City", "")!!,
                        style = TextStyle(fontSize = 15.sp, fontStyle = FontStyle.Normal, color = ColorProvider(Color.White)
                            , textAlign = TextAlign.Center))
                    Spacer(modifier = GlanceModifier.fillMaxWidth().height(30.dp))

                    androidx.glance.text.Text(text = converToCelsius(sharedPreferences.getString("temperature", "")!!) + "°",
                        style = TextStyle(fontSize = 50.sp, fontStyle = FontStyle.Normal, color = ColorProvider(Color.White)
                            , textAlign = TextAlign.Center))
                    Spacer(modifier = GlanceModifier.fillMaxWidth().height(30.dp))

                    androidx.glance.text.Text(text = sharedPreferences.getString("descriptionCondition", "")!!,
                        style = TextStyle(fontSize = 15.sp, fontStyle = FontStyle.Normal, color = ColorProvider(Color.White)
                            , textAlign = TextAlign.Center))

                    Spacer(modifier = GlanceModifier.fillMaxWidth().height(30.dp))
                    Spacer(modifier = GlanceModifier.fillMaxWidth().height(30.dp))
                    androidx.glance.text.Text(text = "ʌ " + converToCelsius(sharedPreferences.getString("temperatureMax", "")!!) + "°",
                        style = TextStyle(fontSize = 50.sp, fontStyle = FontStyle.Normal, color = ColorProvider(Color.White)
                            , textAlign = TextAlign.Center))
                    Spacer(modifier = GlanceModifier.fillMaxWidth().height(30.dp))
                    androidx.glance.text.Text(text = "v " + converToCelsius(sharedPreferences.getString("temperatureMin", "")!!) + "°",
                        style = TextStyle(fontSize = 50.sp, fontStyle = FontStyle.Normal, color = ColorProvider(Color.White)
                            , textAlign = TextAlign.Center))
                    Spacer(modifier = GlanceModifier.fillMaxWidth().height(30.dp))
                }

            }
        }
    }

    private fun converToCelsius(temp : String) : String
    {
        return (temp.toDouble()).roundToInt().toString()
    }


    @SuppressLint("SimpleDateFormat")
    private fun SetBackgroundColor() : Color
    {
        var returnColor : Color = Color.DarkGray
        val simpleDateFormat = SimpleDateFormat("HH:mm a")
        val currentHour = GetCurrentTime().split(":")[0].toInt()

        if(currentHour in 8..17)
        {
            returnColor = Color(android.graphics.Color.parseColor("#3498DB"))
        } else if ((currentHour in 18..20) || (currentHour in 6..7))
        {
            returnColor = Color(android.graphics.Color.parseColor("#EC7063"))
        } else if(currentHour >= 21)
        {
            returnColor = Color(android.graphics.Color.parseColor("#34495E"))
        }
        return returnColor
    }
}

class SimpleWidgetRecevier : GlanceAppWidgetReceiver()
{
    override val glanceAppWidget : GlanceAppWidget
        get() = Widget
}