package com.example.home_widgets_flutter

import HomeWidgetGlanceState
import HomeWidgetGlanceStateDefinition
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
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
import org.json.JSONArray
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.compose.runtime.SideEffect
import androidx.core.net.toUri
import androidx.glance.LocalGlanceId
import androidx.glance.appwidget.ImageProvider




data class ItemTask(val name: String, val isChecked: Boolean, val index: Int) {}

class ListviewGlanceWidget : GlanceAppWidget() {
    override val stateDefinition = HomeWidgetGlanceStateDefinition()

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlaceListview(currentState(), id)
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
                index = i,
            )

            if (!mapItem.isChecked) {
                itemTaskList.add(mapItem)
            }
        }
        return itemTaskList
    }



    companion object {
        val sourceKey = stringPreferencesKey("image_source")
        val sourceUrlKey = stringPreferencesKey("https://developer.android.com/static/develop/ui/compose/images/glance-widget.png")

        fun getImageKey(size: DpSize) = getImageKey(size.width.value, size.height.value)

        fun getImageKey(width: Float, height: Float) = stringPreferencesKey(
            "uri-$width-$height"
        )
    }
    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    private fun GlaceListview(currentState: HomeWidgetGlanceState, id: GlanceId) {
        val data = currentState.preferences
        val itemsTasks = getTodos(data)

        val context = LocalContext.current
        val size = LocalSize.current
        val imagePath = currentState(getImageKey(size))

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
                            .background(Color.Blue)
                            .padding(4.dp)
                    ) {
                        if (imagePath != null) {
                            Image(
                                provider = getImageProvider(imagePath),
                                contentDescription = null,
                                contentScale = ContentScale.FillBounds,

                            )

                        } else {
                            CircularProgressIndicator()

                            // Enqueue the worker after the composition is completed using the glanceId as
                            // tag so we can cancel all jobs in case the widget instance is deleted
                            val glanceId = LocalGlanceId.current
                            SideEffect {
                                ImageWorker.enqueue(context, size, glanceId)
                            }
                        }
                        Box(
                            modifier = GlanceModifier
                                .background(Color.White)
                                .cornerRadius(50.dp)
                                .height(16.dp)
                                .width(16.dp),
                            contentAlignment = Alignment.Center
                        ) {

                            Image(
                                provider = ImageProvider(R.drawable.clock),
                                contentDescription = null,
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


    private fun getImageProvider(path: String): ImageProvider {
        if (path.startsWith("content://")) {
            return ImageProvider(path.toUri())
        }
        val bitmap = BitmapFactory.decodeFile(path)
        return ImageProvider(bitmap)
    }
}