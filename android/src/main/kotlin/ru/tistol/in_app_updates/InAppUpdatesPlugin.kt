package ru.tistol.in_app_updates

import android.app.Activity
import android.app.Application
import android.os.Bundle
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.Result

/** InAppUpdatesPlugin */
class InAppUpdatesPlugin : FlutterPlugin, MethodChannel.MethodCallHandler, ActivityAware,
    Application.ActivityLifecycleCallbacks,
    EventChannel.StreamHandler {
    private lateinit var channel: MethodChannel
    private lateinit var eventChannel: EventChannel
    private var activityApp: Activity? = null
    private var eventSink: EventChannel.EventSink? = null
    private var isHuawei: Boolean? = null

    override fun onListen(
        arguments: Any?, eventSink: EventChannel.EventSink?
    ) {
        this.eventSink = eventSink
    }

    override fun onCancel(arguments: Any?) {
        eventSink = null
    }

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "in_app_updates")
        channel.setMethodCallHandler(this)

        eventChannel =
            EventChannel(flutterPluginBinding.binaryMessenger, "in_app_updates_event_channel")
        eventChannel.setStreamHandler(this)
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        when (call.method) {
            "isUpdateAvailable" -> {
                val args = call.arguments<Map<String, Boolean>>()
                isHuawei = args?.get("isHuawei")
                if (isHuawei == null) {
                    result.error(
                        "IN_APP_UPDATES_PLATFORM_NOT_CHOOSE",
                        "isHuawei should be true or false",
                        null,
                    )
                    return
                }
                if (isHuawei == true) {
                    InAppUpdatesHuawei.getInstance()
                        .checkForUpdateHuawei(result, eventSink, activityApp)
                }
                if (isHuawei == false) {
                    InAppUpdatesAndroid.getInstance().isUpdateAvailable(result, activityApp)
                }
            }
            "updateApp" -> {
                val args = call.arguments<Map<String, Boolean>>()
                val immediate = args?.get("immediate") ?: false
                val flexible = args?.get("flexible") ?: false
                val allowAssetPackDeletion = args?.get("allowAssetPackDeletion") ?: false

                if (isHuawei == null) {
                    result.error(
                        "IN_APP_UPDATES_PLATFORM_NOT_CHOOSE",
                        "isHuawei should be true or false\"",
                        ""
                    )
                }
                if (activityApp != null) {
                    if (isHuawei == true) {
                        val mustBtnOne = args?.get("mustBtnOne") ?: false
                        InAppUpdatesHuawei.getInstance()
                            .updateHuawei(result, activityApp, mustBtnOne)
                    }

                    if (isHuawei == false) {
                        InAppUpdatesAndroid.getInstance()
                            .updateApp(
                                immediate,
                                flexible,
                                allowAssetPackDeletion,
                                activityApp!!,
                                result, eventSink,
                            )
                    }
                } else {
                    result.error("ACTIVITY_NULL", "activityApp should be not null", null)
                }
            }
            "completeUpdate" -> {
                InAppUpdatesAndroid.getInstance().completeUpdate(result)
            }
            else -> {
                result.notImplemented()
            }
        }
    }

    override fun onAttachedToActivity(activityPluginBinding: ActivityPluginBinding) {
        activityApp = activityPluginBinding.activity
        if (isHuawei == false) {
            activityApp?.application?.registerActivityLifecycleCallbacks(this)
        }
    }

    override fun onDetachedFromActivityForConfigChanges() {}

    override fun onReattachedToActivityForConfigChanges(activityPluginBinding: ActivityPluginBinding) {
    }

    override fun onDetachedFromActivity() {
        if (isHuawei == false) {
            activityApp?.application?.unregisterActivityLifecycleCallbacks(this)
        }
        activityApp = null
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
        if (isHuawei == false) {
            InAppUpdatesAndroid.getInstance().destroy()
        }
        if (isHuawei == true) {
            InAppUpdatesHuawei.getInstance().destroy()
        }
    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
    }

    override fun onActivityStarted(p0: Activity) {
    }

    override fun onActivityResumed(p0: Activity) {
    }

    override fun onActivityPaused(p0: Activity) {
        if (isHuawei == false) {
            InAppUpdatesAndroid.getInstance().completeUpdate()
        }
        if (isHuawei == true) {
            InAppUpdatesHuawei.getInstance().releaseCallback()
        }
    }

    override fun onActivityStopped(p0: Activity) {
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
    }

    override fun onActivityDestroyed(p0: Activity) {
    }
}

