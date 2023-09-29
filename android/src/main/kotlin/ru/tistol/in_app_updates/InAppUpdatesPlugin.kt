package ru.tistol.in_app_updates

import android.app.Activity
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.android.play.core.install.model.ActivityResult
import com.google.android.play.core.install.model.AppUpdateType
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry

/** InAppUpdatesPlugin */
class InAppUpdatesPlugin : FlutterPlugin, MethodChannel.MethodCallHandler, ActivityAware,
    Application.ActivityLifecycleCallbacks, PluginRegistry.ActivityResultListener,
    EventChannel.StreamHandler {
    private lateinit var channel: MethodChannel
    private lateinit var eventChannel: EventChannel
    private lateinit var contextApp: Context
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
        contextApp = flutterPluginBinding.applicationContext
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
                    HuaweiInAppUpdate.getInstance().checkForUpdateHuawei(eventSink, activityApp)
                    result.success(null)
                }
                if (isHuawei == false) {
                    InAppUpdatesAndroid.getInstance().isUpdateAvailable(result, activityApp, this)
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
                        HuaweiInAppUpdate.getInstance()
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
    }

    override fun onDetachedFromActivityForConfigChanges() {
        activityApp = null
    }

    override fun onReattachedToActivityForConfigChanges(activityPluginBinding: ActivityPluginBinding) {
        activityApp = activityPluginBinding.activity
    }

    override fun onDetachedFromActivity() {
        activityApp = null
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
        activityApp = p0
    }

    override fun onActivityStarted(p0: Activity) {
        activityApp = p0
    }

    override fun onActivityResumed(p0: Activity) {
        activityApp = p0
    }

    override fun onActivityPaused(p0: Activity) {
        activityApp = p0
        if (isHuawei == false) {
            InAppUpdatesAndroid.getInstance().completeUpdate()
        }
    }

    override fun onActivityStopped(p0: Activity) {
        activityApp = p0
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
        activityApp = p0
    }

    override fun onActivityDestroyed(p0: Activity) {
        activityApp = p0
        if (isHuawei == false) {
            InAppUpdatesAndroid.getInstance().unregisterListeners()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        if (requestCode == 200) {
            if (InAppUpdatesAndroid.getInstance().appUpdateType == AppUpdateType.IMMEDIATE) {
                when (resultCode) {
                    RESULT_CANCELED -> {
//                        updateResult?.error("USER_DENIED_UPDATE", resultCode.toString(), null)
                    }
                    RESULT_OK -> {
//                        updateResult?.success(null)
                    }
                    ActivityResult.RESULT_IN_APP_UPDATE_FAILED -> {
//                        updateResult?.error("IN_APP_UPDATE_FAILED", "Some other error prevented either the user from providing consent or the update to proceed.", null)
                    }
                }
//                updateResult = null
                return true
            } else if (InAppUpdatesAndroid.getInstance().appUpdateType == AppUpdateType.FLEXIBLE) {
                when (resultCode) {
                    RESULT_CANCELED -> {
//                        updateResult?.error("USER_DENIED_UPDATE", resultCode.toString(), null)
//                        updateResult = null
                    }
                    ActivityResult.RESULT_IN_APP_UPDATE_FAILED -> {
//                        updateResult?.error("IN_APP_UPDATE_FAILED", resultCode.toString(), null)
//                        updateResult = null
                    }
                }
                return true
            }
        }
        return false
    }
}

