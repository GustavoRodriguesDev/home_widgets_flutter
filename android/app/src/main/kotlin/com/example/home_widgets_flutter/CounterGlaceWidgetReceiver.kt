package com.example.home_widgets_flutter

import HomeWidgetGlanceWidgetReceiver
import com.example.home_widgets_flutter.CounterGlanceWidget


class CounterGlaceWidgetReceiver : HomeWidgetGlanceWidgetReceiver<CounterGlanceWidget>() {
    override val glanceAppWidget = CounterGlanceWidget()
}