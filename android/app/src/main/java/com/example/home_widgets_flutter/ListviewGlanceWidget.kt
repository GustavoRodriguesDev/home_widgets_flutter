package com.example.home_widgets_flutter

import HomeWidgetGlanceState
import HomeWidgetGlanceStateDefinition
import android.content.Context
import android.graphics.drawable.Icon
import android.view.RoundedCorner
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.IconImageProvider
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.appwidget.CheckBox
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.ColorProvider

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

class ListviewGlanceWidget : GlanceAppWidget() {
    override val stateDefinition = HomeWidgetGlanceStateDefinition()

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlaceListview()
        }
    }

    @Composable
    private fun GlaceListview() {
        val items = listOf("Item 1", "Item 2", "Item 3", "Item 4")

        Box(
            modifier = GlanceModifier.background(Color(0xFFFFFFFF)).fillMaxWidth()
        )
        {
            Column {
                Row(
                    modifier = GlanceModifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally

                ) {
                    items.forEachIndexed { index, item ->
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
                        if (index != items.size - 1) {
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
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items.forEachIndexed { index, item ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = GlanceModifier
                                .height(44.dp)
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),

                        ) {

                            CheckBox(checked = true, onCheckedChange = {})
                            Text("task de teste")
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

                        if (index != items.size - 1) {
                            Box(
                                modifier = GlanceModifier
                                    .height(1.dp)
                                    .fillMaxWidth()
                                    .background(Color(0xFFF2F4F7))
                            ) {
                            }
                        }
                    }
                }
            }
        }
    }
}




