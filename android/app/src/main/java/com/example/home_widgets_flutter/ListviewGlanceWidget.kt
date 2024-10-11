package com.example.home_widgets_flutter

import HomeWidgetGlanceState
import HomeWidgetGlanceStateDefinition
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.ImageProvider
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import coil.annotation.ExperimentalCoilApi
import coil.imageLoader
import coil.request.ErrorResult
import coil.request.ImageRequest
import org.json.JSONArray
import java.io.File
import java.security.AccessController.getContext


data class ItemTask(val name: String, val isChecked: Boolean, val imageUrl: String, val index: Int) {

}

class ListviewGlanceWidget : GlanceAppWidget() {
    override val stateDefinition = HomeWidgetGlanceStateDefinition()

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlaceListview(currentState(), id, context)
        }
    }

    private fun getClickTypeActionParameterKey(): ActionParameters.Key<Int> {
        return ActionParameters.Key("index")
    }

    private fun getTodos(data: SharedPreferences): List<ItemTask> {
        val jsonData = data.getString("task", "[]")
        val jsonArray = JSONArray(jsonData)
        val itemTaskList = mutableListOf<ItemTask>()
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val mapItem = ItemTask(
                name = jsonObject.getString("name"),
                isChecked = jsonObject.getBoolean("checked"),
                imageUrl = jsonObject.getString("imageUrl"),
                index = i,
            )

            if (!mapItem.isChecked) {
                itemTaskList.add(mapItem)
            }
        }
        return itemTaskList
    }




    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    private fun GlaceListview(currentState: HomeWidgetGlanceState, id: GlanceId, context: Context) {
        val data = currentState.preferences
        val itemsTasks = getTodos(data)

        var imagePath by remember { mutableStateOf<String?>(null) }

        LaunchedEffect(itemsTasks[0].imageUrl) {
            imagePath = processImageUrl(itemsTasks[0].imageUrl, context)
        }





        Box(
            modifier = GlanceModifier
                .background(Color(0xFFFFFFFF))
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            Column {
                Row(
                    modifier = GlanceModifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = GlanceModifier
                            .height(69.dp)
                            .width(62.dp)
                            .cornerRadius(8.dp)
                            .padding(4.dp)
                    ) {


                        if(imagePath != null) {
                            Log.d("IMAGEM 1","IMAGEM 1: $imagePath")
                            Image(
                                provider = ImageProvider(imagePath!!.toUri()),
                                contentDescription = "Background Image",
                                modifier = GlanceModifier
                                    .fillMaxSize()
                                    .cornerRadius(8.dp)
                            )
                        } else {
                            Log.d("IMAGEM 2","IMAGEM 2: $imagePath")
                            Image(
                                provider = ImageProvider(R.drawable.goat_image),
                                contentDescription = "Background Image",
                                modifier = GlanceModifier
                                    .fillMaxSize()
                                    .cornerRadius(8.dp)
                            )
                        }

                        Box(
                            modifier = GlanceModifier
                                .background(Color.White)
                                .cornerRadius(50.dp)
                                .height(16.dp)
                                .width(16.dp)
                                .padding(2.dp)
                        ) {
                            Image(
                                provider = ImageProvider(R.drawable.clock),
                                contentDescription = "Clock Icon",
                                modifier = GlanceModifier.size(12.dp)
                            )
                        }
                    }
                }

                Box(
                    modifier = GlanceModifier
                        .background(Color.Gray)
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Row(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = GlanceModifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Text("Hoje", style = TextStyle(fontSize = 18.sp))
                    }
                }
                if (itemsTasks.isEmpty()) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = GlanceModifier.fillMaxSize()
                    ) {
                        Text("sem itens", style = TextStyle(fontSize = 18.sp))
                    }

                } else {
                    LazyColumn(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        items(itemsTasks.size) { index ->
                            if (!itemsTasks[index].isChecked) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = GlanceModifier
                                        .height(44.dp)
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = GlanceModifier
                                            .height(16.dp)
                                            .width(16.dp)
                                            .background(Color.Gray)
                                            .padding(0.5.dp)
                                            .cornerRadius(5.dp)
                                            .clickable(
                                                actionRunCallback<ListviewActionCallbacks>(
                                                    actionParametersOf(
                                                        getClickTypeActionParameterKey() to itemsTasks[index].index
                                                    )
                                                )
                                            ),

                                        ) {
                                        Box(
                                            GlanceModifier
                                                .background(Color.White)
                                                .height(15.3.dp)
                                                .width(15.3.dp)
                                                .cornerRadius(5.dp),
                                        ) {}
                                    }
                                    Spacer(modifier = GlanceModifier.padding(4.dp))
                                    Text(itemsTasks[index].name)
                                    Spacer(GlanceModifier.defaultWeight())
                                    Image(
                                        provider = ImageProvider(R.drawable.sun),
                                        contentDescription = null,
                                        modifier = GlanceModifier.size(12.dp)
                                    )
                                    Text("as 08:00")
                                    Image(
                                        provider = ImageProvider(R.drawable.clock),
                                        contentDescription = null,
                                        modifier = GlanceModifier.size(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
@OptIn(ExperimentalCoilApi::class)
private suspend fun processImageUrl(imageUrl: String, context: Context): String? {
    val request = ImageRequest.Builder(context)
        .data(imageUrl)
        .build()

    with(context.imageLoader) {
        val result = execute(request)
        if (result is ErrorResult) {
            throw result.throwable
        }
    }
    val path = context.imageLoader.diskCache?.get(imageUrl)?.use { snapshot ->
        val cacheDir: File = File(context.cacheDir, "image_cache")

        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }

        val newFile = File(cacheDir, snapshot.data.toFile().name);

        val contentUri = FileProvider.getUriForFile(
            context,
            "com.example.home_widgets_flutter.fileprovider",
            newFile
        )

        val resolveInfo = context.packageManager.resolveActivity(
            Intent(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_HOME) },
            PackageManager.MATCH_DEFAULT_ONLY
        )
        val launcherName = resolveInfo?.activityInfo?.packageName
        if (launcherName != null) {
            context.grantUriPermission(
                launcherName,
                contentUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
            )
        }

        contentUri.toString()
    }
   return  path
}


}