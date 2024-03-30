package com.cryptica.stormly

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Criteria
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.cryptica.stormly.data.models.ForecastModel
import com.cryptica.stormly.remote.CurrentWeatherApi
import com.cryptica.stormly.remote.RetrofitHelper
import com.cryptica.stormly.ui.theme.StormlyTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Response
import java.util.Locale
import java.util.function.Consumer

class PermissionsActivity : ComponentActivity() {
    @OptIn(DelicateCoroutinesApi::class, ExperimentalPermissionsApi::class)
    @SuppressLint("CoroutineCreationDuringComposition", "UnrememberedMutableState",
        "CommitPrefEdits"
    )
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StormlyTheme {
                // A surface container using the 'background' color from the theme
                val systemUiController = rememberSystemUiController()
                SideEffect {
                    systemUiController.setSystemBarsColor(Color.DarkGray)
                }
                var token = if (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED)  "Collecting Data..." else "Permissions verification..."


                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.DarkGray
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Loader()
                        Spacer(modifier = Modifier.padding(20.dp))

                        var indicationText by remember{ mutableStateOf(token) }
                        Text(text = indicationText,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Light,
                            fontSize = 30.sp,
                            color = Color.White
                        )
                        
                        val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestMultiplePermissions(), onResult = {isGranted ->
                            if(isGranted[Manifest.permission.ACCESS_COARSE_LOCATION] == true && isGranted[Manifest.permission.ACCESS_FINE_LOCATION] == true && isGranted[Manifest.permission.POST_NOTIFICATIONS] == true) {
                                indicationText = "Collecting data..."
                            }
                        })

                        if(ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                            ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                            ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS) !=PackageManager.PERMISSION_GRANTED
                            )
                        {
                            SideEffect {
                                Handler(Looper.getMainLooper()).postDelayed({
                                    launcher.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.POST_NOTIFICATIONS))
                                }, 5000)
                            }
                        }else{
                            val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
                            val criteria = Criteria()
                            criteria.isAltitudeRequired = true
                            criteria.accuracy = Criteria.ACCURACY_FINE
                            criteria.powerRequirement = Criteria.NO_REQUIREMENT
                            val provider = locationManager.getBestProvider(criteria, false)
                            val location = locationManager.getLastKnownLocation(provider.toString())
                            val weatherApi = RetrofitHelper.getInstance().create(CurrentWeatherApi::class.java)
                            GlobalScope.launch {
                                val weatherResult = weatherApi.getWeather(location!!.latitude, location.longitude, resources.getString(R.string.apiKey), "metric")
                                val forecastResult = weatherApi.getForecast(location!!.latitude, location.longitude, resources.getString(R.string.apiKey), "metric")
                                val sharedPrefs = getSharedPreferences("prefs", MODE_PRIVATE)
                                val editor = sharedPrefs.edit()
                                editor.putString("City", getCityName(applicationContext, location.latitude, location.longitude))
                                editor.putString("iconCondition", weatherResult.body()!!.weather[0].main)
                                editor.putString("descriptionCondition", weatherResult.body()!!.weather[0].description)
                                editor.putString("temperature", weatherResult.body()!!.main.temp.toString())
                                editor.putString("temperatureFeel", weatherResult.body()!!.main.feelsLike.toString())
                                editor.putString("temperatureMin", weatherResult.body()!!.main.tempMin.toString())
                                editor.putString("temperatureMax", weatherResult.body()!!.main.tempMax.toString())
                                editor.putString("pressure", weatherResult.body()!!.main.pressure.toString())
                                editor.putString("humidity", weatherResult.body()!!.main.humidity.toString())
                                editor.putString("windSpeed", weatherResult.body()!!.wind.speed.toString())
                                editor.putString("cloudiness", weatherResult.body()!!.clouds.all.toString())
                                editor.putString("sunrise", weatherResult.body()!!.sys.sunrise.toString())
                                editor.putString("sunset", weatherResult.body()!!.sys.sunset.toString())
                                if(weatherResult.body()!!.rain != null)
                                    editor.putString("rain1h", weatherResult.body()!!.rain.h.toString())
                                getWeatherForNextTenHours(sharedPrefs, editor, forecastResult)
                                editor.apply()
                                val intent = Intent(applicationContext, MainActivity::class.java)
                                startActivity(intent)
                            }
                        }
                    }
                }
            }
        }
    }
}

fun getWeatherForNextTenHours(sharedPrefs: SharedPreferences, editor: SharedPreferences.Editor, response: Response<ForecastModel>)
{
    val body = response.body()
    editor.putString("forecast1temp", body!!.list.get(1).main.temp.toString())
    editor.putString("forecast2temp", body.list.get(2).main.temp.toString())
    editor.putString("forecast3temp", body.list.get(3).main.temp.toString())
    editor.putString("forecast4temp", body.list.get(4).main.temp.toString())
    editor.putString("forecast5temp", body.list.get(5).main.temp.toString())
    editor.putString("forecast6temp", body.list.get(6).main.temp.toString())
    editor.putString("forecast7temp", body.list.get(7).main.temp.toString())
    editor.putString("forecast8temp", body.list.get(8).main.temp.toString())
    editor.putString("forecast1desc", body.list.get(1).weather.get(0).description)
    editor.putString("forecast2desc", body.list.get(2).weather.get(0).description)
    editor.putString("forecast3desc", body.list.get(3).weather.get(0).description)
    editor.putString("forecast4desc", body.list.get(4).weather.get(0).description)
    editor.putString("forecast5desc", body.list.get(5).weather.get(0).description)
    editor.putString("forecast6desc", body.list.get(6).weather.get(0).description)
    editor.putString("forecast7desc", body.list.get(7).weather.get(0).description)
    editor.putString("forecast8desc", body.list.get(8).weather.get(0).description)
}

@Composable
fun Loader()
{
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.loading)
    )
    LottieAnimation(
        composition = composition, modifier = Modifier.size(300.dp, 300.dp), iterations = LottieConstants.IterateForever
    )
}

fun getCityName(context: Context, lat: Double, lng: Double) : String
{
    val geocoder = Geocoder(context, Locale.getDefault())
    val adresses = geocoder.getFromLocation(lat, lng, 1)
    return adresses!!.get(0).locality
}






