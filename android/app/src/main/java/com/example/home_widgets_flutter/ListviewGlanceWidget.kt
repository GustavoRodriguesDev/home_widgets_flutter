package com.example.home_widgets_flutter

import HomeWidgetGlanceState
import HomeWidgetGlanceStateDefinition
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.appwidget.CheckBox
import androidx.glance.appwidget.GlanceAppWidget
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
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import org.json.JSONArray

data class ItemTask(val name: String, val isChecked: Boolean) {
}
class ListviewGlanceWidget : GlanceAppWidget() {
    override val stateDefinition = HomeWidgetGlanceStateDefinition()

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlaceListview(context, currentState())
        }
    }
    private fun getTodos(data: SharedPreferences):List<ItemTask>{
        val jsonData = data.getString("task", "[]")
        val jsonArray = JSONArray(jsonData)
        val itemTaskList = mutableListOf<ItemTask>()
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val mapItem = ItemTask(
                name = jsonObject.getString("name"),
                isChecked = jsonObject . getBoolean ("checked")
            )
            itemTaskList.add(mapItem)
        }
        print("passou aqui")
        print(itemTaskList.size)
        return itemTaskList
    }

    @Composable
    private fun GlaceListview(context: Context, currentState: HomeWidgetGlanceState) {
        val data = currentState.preferences
        val itemsTasks  =   getTodos(data)
        Box(
            modifier = GlanceModifier.background(Color(0xFFFFFFFF)).fillMaxWidth()
        )
        {
            Column {
                Row(
                    modifier = GlanceModifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally

                ) {
                    itemsTasks.forEachIndexed { index, item ->
                        Box(
                            modifier = GlanceModifier
                                .height(69.dp)
                                .width(62.dp)
                                .cornerRadius(8.dp)
                                .background(Color.Blue)
                                .padding(4.dp)
                        ) {
                            Image(
                                provider = ImageProvider(R.drawable.sun),
                                contentDescription = null,
                                modifier = GlanceModifier
                                    .fillMaxSize()
                            )
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
                                    modifier = GlanceModifier
                                        .size(12.dp)
                                )
                            }
                        }
                        if (index != itemsTasks.size - 1) {
                            Spacer(modifier = GlanceModifier.padding(8.dp))
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
                        modifier = GlanceModifier
                            .padding(horizontal = 16.dp, vertical = 12.dp)

                    ) {
                        Text("Hoje", style = TextStyle(fontSize = 18.sp))
                    }
                }
                LazyColumn(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  items(itemsTasks.size){index->

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = GlanceModifier
                                .height(44.dp)
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),

                            ) {

                            CheckBox(checked = itemsTasks[index].isChecked, onCheckedChange = {})
                            Text(itemsTasks[index].name)
                            Spacer(GlanceModifier.defaultWeight())
                            Image(
                                provider = ImageProvider(R.drawable.sun),
                                contentDescription = null,
                                modifier = GlanceModifier
                                    .size(12.dp)
                            )

                            Text("as 08:00")

                            Image(
                                provider = ImageProvider(R.drawable.clock),
                                contentDescription = null,
                                modifier = GlanceModifier
                                    .size(12.dp)
                            )

                        }

//                        if (index != itemsTasks.size - 1) {
//                            Box(
//                                modifier = GlanceModifier
//                                    .height(1.dp)
//                                    .fillMaxWidth()
//                                    .background(Color(0xFFF2F4F7))
//                            ) {
//                            }
//                        }
                    }
                    }
                }
            }
        }
    }









