package com.cryptica.stormly

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.icu.util.Calendar
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.cryptica.stormly.ui.theme.StormlyTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment
import java.sql.Date
import java.sql.Timestamp
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StormlyTheme {
                val systemUiController = rememberSystemUiController()
                val sharedPrefs = getSharedPreferences("prefs", MODE_PRIVATE)
                SideEffect {
                    systemUiController.setSystemBarsColor(SetBackgroundColor(sharedPrefs))
                }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = SetBackgroundColor(sharedPrefs)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        
                        Spacer(modifier = Modifier.padding(top = 50.dp))
                        TimeAndLocationSection(prefs = sharedPrefs)
                        Column(modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())){
                            CurrentConditionsSection(prefs = sharedPrefs)
                            Spacer(modifier = Modifier.padding(top = 25.dp))
                            presentDetails(prefs = sharedPrefs)
                            sunStatus(prefs = sharedPrefs)
                        }
                        }
                    }
                }
            }
        }
    }



    @SuppressLint("SimpleDateFormat")
    private fun SetBackgroundColor(prefs: SharedPreferences) : Color
    {
        var returnColor : Color = Color.DarkGray
        val simpleDateFormat = SimpleDateFormat("HH:mm a")
        val currentHour = GetCurrentTime().split(":")[0].toInt()

        if(currentHour in 8..17)
        {
            returnColor = Color(android.graphics.Color.parseColor("#3498DB"))
        } else if ((currentHour in 18..21) || (currentHour in 6..7))
        {
            returnColor = Color(android.graphics.Color.parseColor("#EC7063"))
        } else if(currentHour >= 22)
        {
            returnColor = Color(android.graphics.Color.parseColor("#34495E"))
        }
        return returnColor
    }

    private fun GetCurrentTime() : String
    {
        val simpleDateFormat = SimpleDateFormat("HH:mm a")
        return simpleDateFormat.format(Calendar.getInstance().time)
    }

    @Composable
    private fun TimeAndLocationSection(prefs : SharedPreferences)
    {
        Column(modifier = Modifier.wrapContentWidth()) {
            Text(
                text = GetCurrentTime().substring(0, 5),
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Light,
                fontSize = 25.sp,
                color = Color.White,
                modifier = Modifier.padding(start = 10.dp)
            )

            Spacer(modifier = Modifier.padding(top = 15.dp))

            Text(
                text = prefs.getString("City", "")!!,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Light,
                fontSize = 40.sp,
                color = Color.White,
                modifier = Modifier.padding(start = 10.dp)
            )
        }
}

    @Composable
    private fun CurrentConditionsSection(prefs : SharedPreferences)
    {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(modifier = Modifier.fillMaxWidth()) {
                val composition by rememberLottieComposition(spec = LottieCompositionSpec.Url(getLottieUrl(prefs = prefs)))
                LottieAnimation(
                    composition = composition, modifier = Modifier.size(200.dp, 200.dp), iterations = LottieConstants.IterateForever
                )
                Spacer(modifier = Modifier.padding(start = 10.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = (converToCelsius(prefs.getString("temperature", "")!!) + "°"),
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Light,
                        fontSize = 80.sp,
                        color = Color.White,
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.padding(top = 10.dp))
                    Text(text = prefs.getString("descriptionCondition", "")!!,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Light,
                        fontSize = 25.sp,
                        color = Color.White,
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .align(Alignment.CenterHorizontally)
                        )
                }
            }
            Spacer(modifier = Modifier.padding(top = 10.dp))
            Row (modifier = Modifier
                .fillMaxWidth(), horizontalArrangement = Arrangement.Center
                ){
                Column {
                    Text(text = "Feels like",
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Light,
                        fontSize = 25.sp,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    Spacer(modifier = Modifier.padding(5.dp))
                    Text(text = converToCelsius(prefs.getString("temperatureFeel", "")!!) + "°",
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Light,
                        fontSize = 25.sp,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                
                Spacer(modifier = Modifier.padding(start = 20.dp))
                
                Column {
                    Text(text = "Minimal",
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Light,
                        fontSize = 25.sp,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.padding(5.dp))
                    Text(text = converToCelsius(prefs.getString("temperatureMin", "")!!) + "°",
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Light,
                        fontSize = 25.sp,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                
                Spacer(modifier = Modifier.padding(start = 20.dp))
                
                Column {
                    Text(text = "Maximum",
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Light,
                        fontSize = 25.sp,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.padding(5.dp))
                    Text(text = converToCelsius(prefs.getString("temperatureMax", "")!!) + "°",
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Light,
                        fontSize = 25.sp,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }

    private fun converToCelsius(temp : String) : String
    {
        return (temp.toDouble()).roundToInt().toString()
    }

    @Composable
    private fun getLottieUrl(prefs : SharedPreferences): String {
        val conditionsMain = prefs.getString("iconCondition", "")
        val conditionDescription = prefs.getString("descriptionCondition", "")
        val currentHour = GetCurrentTime().split(":")[0].toInt()
        var url = ""

        if(conditionsMain == "Clouds")
        {
            if(conditionDescription!!.contains("few clouds") || conditionDescription!!.contains("scattered clouds"))
            {
                if(currentHour in 7..21)
                {
                    url = "https://lottie.host/c99298ed-ddfe-4a55-8de0-00d605c0f846/moS4iovBas.json"
                } else
                {
                    url = "https://lottie.host/0735e185-55e2-4d61-9708-edf0b481b55b/vY4K0QK1nu.json"
                }
            } else
            {
                url = "https://lottie.host/ff0e0053-9b02-4b57-86b5-5669bdbc7d62/u0ThHzikBl.json"
            }
        }else if(conditionsMain == "Thunderstorm")
            url = "https://lottie.host/bb706008-1910-4c71-8c5d-ef2b09e099cd/c61oeedT4a.json"
        else if(conditionsMain == "Drizzle" || conditionsMain == "Atmosphere")
            url = "https://lottie.host/101e4ef7-d64c-4a59-92a7-9a83c6165ed4/DP8BdqvqEA.json"
        else if(conditionsMain == "Rain")
        {
            if(currentHour in 7..21)
            {
                url = "https://lottie.host/afba70f8-04b1-4cad-8163-9be77c5ae242/hh55umxHrO.json"
            } else
            {
                url = "https://lottie.host/2cd75658-99db-45e0-94f4-56eef5ff5033/2SU6glNfWm.json"
            }
        }else if(conditionsMain == "Snow")
            url = "https://lottie.host/c4b930e2-1827-4a72-ae25-c649e662eba6/TJsV4wtv55.json"

        return url
    }

    @Composable
    private fun presentDetails(prefs : SharedPreferences)
    {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ){
                Column {
                    Text(text = "Pressure",
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Light,
                        fontSize = 25.sp,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.padding(5.dp))
                    Text(text = converToCelsius(prefs.getString("pressure", "")!!) + " hPa",
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Light,
                        fontSize = 25.sp,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                Spacer(modifier = Modifier.padding(start = 20.dp))

                Column {
                    Text(text = "Humidity",
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Light,
                        fontSize = 25.sp,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.padding(5.dp))
                    
                    Text(text = converToCelsius(prefs.getString("humidity", "")!!) + " %",
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Light,
                        fontSize = 25.sp,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }

            Spacer(modifier = Modifier.padding(top = 25.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ){
                Column {
                    Text(text = "Wind speed",
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Light,
                        fontSize = 25.sp,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.padding(5.dp))
                    Text(text = converToCelsius(prefs.getString("windSpeed", "")!!) + " m/s",
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Light,
                        fontSize = 25.sp,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                Spacer(modifier = Modifier.padding(start = 20.dp))

                Column {
                    Text(text = "Cloudiness",
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Light,
                        fontSize = 25.sp,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.padding(5.dp))

                    Text(text = converToCelsius(prefs.getString("cloudiness", "")!!) + " %",
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Light,
                        fontSize = 25.sp,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }

    @Composable
    private fun sunStatus(prefs : SharedPreferences)
    {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val composition by rememberLottieComposition(spec = LottieCompositionSpec.Url("https://lottie.host/c0a8feaf-c045-4ac9-8d77-2fb5059d78f4/RpBbGRajQ3.json"))
            LottieAnimation(
                composition = composition, modifier = Modifier.size(400.dp, 400.dp), iterations = LottieConstants.IterateForever
            )

            Row (modifier = Modifier
                .fillMaxWidth(), horizontalArrangement = Arrangement.Center
            ) {
                Column {
                    Text(
                        text = "Sunrise",
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Light,
                        fontSize = 25.sp,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.padding(5.dp))
                    Text(
                        text = epochToTimeZone(prefs.getString("sunrise", "")!!.toLong()).substring(11,16),
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Light,
                        fontSize = 25.sp,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                Spacer(modifier = Modifier.padding(start = 20.dp))

                Column {
                    Text(
                        text = "Sunset",
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Light,
                        fontSize = 25.sp,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.padding(5.dp))
                    Text(
                        text = epochToTimeZone(prefs.getString("sunset", "")!!.toLong()).substring(11,16),
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Light,
                        fontSize = 25.sp,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }

        }
    }

    private fun epochToTimeZone(epoch : Long) : String
    {
        val dt = Instant.ofEpochSecond(epoch)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()

        return dt.toString()
    }