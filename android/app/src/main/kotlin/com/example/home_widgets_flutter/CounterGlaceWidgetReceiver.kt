package com.example.home_widgets_flutter.glance

import HomeWidgetGlanceWidgetReceiver
import com.example.home_widgets_flutter.CounterGlanceWidget


class CounterGlaceWidgetReceiver : HomeWidgetGlanceWidgetReceiver<CounterGlanceWidget>() {
    override val glanceAppWidget = CounterGlanceWidget()
}