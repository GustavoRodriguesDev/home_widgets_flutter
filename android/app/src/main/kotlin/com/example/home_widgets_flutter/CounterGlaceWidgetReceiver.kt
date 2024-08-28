package com.example.home_widgets_flutter.glance

import CounterGlanceWidget
import HomeWidgetGlanceWidgetReceiver


class CounterGlaceWidgetReceiver : HomeWidgetGlanceWidgetReceiver<CounterGlanceWidget>() {
    override val glanceAppWidget = CounterGlanceWidget()
}