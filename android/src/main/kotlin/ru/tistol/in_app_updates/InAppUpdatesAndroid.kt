package ru.tistol.in_app_updates

import android.app.Activity
import android.app.Application
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.isFlexibleUpdateAllowed
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodChannel


@Suppress("DEPRECATION")
class InAppUpdatesAndroid private constructor() {
    companion object {

        @Volatile
        private var instance: InAppUpdatesAndroid? = null

        fun getInstance() = instance ?: synchronized(this) {
            instance ?: InAppUpdatesAndroid().also { instance = it }
        }
    }

    private lateinit var appUpdateManager: AppUpdateManager
    private var installStatus: Int? = null
    private var listener: InstallStateUpdatedListener? = null
    private var appUpdateType: Int? = null
    private var appUpdateInfo: AppUpdateInfo? = null

    fun isUpdateAvailable(
        result: MethodChannel.Result,
        activityApp: Activity?,
        activityLifecycleCallbacks: Application.ActivityLifecycleCallbacks,
    ) {
//        activityApp?.application?.registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
        appUpdateManager = AppUpdateManagerFactory.create(activityApp!!)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            this.installStatus = appUpdateInfo.installStatus()
            this.appUpdateInfo = appUpdateInfo
            result.success(
                mapOf(
                    "updateAvailability" to appUpdateInfo.updateAvailability(),
                    "isUpdateAvailable" to (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE),
                    "immediateAllowed" to appUpdateInfo.isImmediateUpdateAllowed,
                    "flexibleAllowed" to appUpdateInfo.isFlexibleUpdateAllowed,
                    "availableVersionCode" to appUpdateInfo.availableVersionCode(),
                    "installStatus" to appUpdateInfo.installStatus(),
                    "packageName" to appUpdateInfo.packageName(),
                    "clientVersionStalenessDays" to appUpdateInfo.clientVersionStalenessDays(),
                    "updatePriority" to appUpdateInfo.updatePriority(),
                    "totalBytesToDownload" to appUpdateInfo.totalBytesToDownload(),
                )
            )
            if (this.installStatus == InstallStatus.DOWNLOADED) {
                completeUpdate()
            }
        }
        appUpdateInfoTask.addOnFailureListener {
            result.error("IN_APP_UPDATES_TASK_FAILURE", it.message, null)
        }
    }

    fun updateApp(
        immediate: Boolean,
        flexible: Boolean,
        allowAssetPackDeletion: Boolean,
        activityApp: Activity,
        result: MethodChannel.Result,
        eventSink: EventChannel.EventSink?,
    ) {
        if (immediate == flexible) {
            result.error(
                "IN_APP_UPDATES_BOTH_METHOD",
                "Neither method is selected, or both are selected",
                null,
            )
            return
        }
        if (appUpdateInfo?.updateAvailability() == UpdateAvailability.UPDATE_NOT_AVAILABLE) {
            result.error(
                "ANDROID_APP_UPDATED",
                "The app already updated",
                null,
            )
            return
        }
        registerListeners(eventSink)
        if (appUpdateInfo == null) {
            val appUpdateInfoTask = appUpdateManager.appUpdateInfo
            appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
                if (immediate) {
                    appUpdateType = AppUpdateType.IMMEDIATE
                    immediateUpdate(allowAssetPackDeletion, activityApp, appUpdateInfo)
                }
                if (flexible) {
                    appUpdateType = AppUpdateType.FLEXIBLE
                    flexibleUpdate(allowAssetPackDeletion, activityApp, appUpdateInfo)
                }
                if (installStatus == InstallStatus.DOWNLOADED) {
                    completeUpdate()
                }
            }
        } else {
            if (immediate) {
                appUpdateType = AppUpdateType.IMMEDIATE
                immediateUpdate(allowAssetPackDeletion, activityApp, appUpdateInfo!!)
            }
            if (flexible) {
                appUpdateType = AppUpdateType.FLEXIBLE
                flexibleUpdate(allowAssetPackDeletion, activityApp, appUpdateInfo!!)
            }
            appUpdateInfo = null
        }
        result.success(null)
    }

    private fun immediateUpdate(
        allowAssetPackDeletion: Boolean,
        activityApp: Activity,
        appUpdateInfo: AppUpdateInfo,
    ) {
        appUpdateManager.startUpdateFlow(
            appUpdateInfo,
            activityApp,
            AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE)
                .setAllowAssetPackDeletion(allowAssetPackDeletion).build(),
        )
    }

    private fun flexibleUpdate(
        allowAssetPackDeletion: Boolean,
        activityApp: Activity,
        appUpdateInfo: AppUpdateInfo,
    ) {
        appUpdateManager.startUpdateFlow(
            appUpdateInfo,
            activityApp,
            AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE)
                .setAllowAssetPackDeletion(allowAssetPackDeletion).build(),
        )
    }

    fun completeUpdate(result: MethodChannel.Result? = null) {
        if (installStatus == InstallStatus.DOWNLOADED && appUpdateType == AppUpdateType.FLEXIBLE) {
            appUpdateManager.completeUpdate()
            installStatus = null
            result?.success(true)
        } else {
            result?.success(false)
        }
    }

    private fun registerListeners(
        eventSink: EventChannel.EventSink?,
    ) {
        unregisterListeners()
        listener = InstallStateUpdatedListener { state ->
            installStatus = state.installStatus()
            eventSink?.success(
                mapOf(
                    "installStatus" to installStatus,
                    "bytesDownloaded" to state.bytesDownloaded(),
                    "totalBytesToDownload" to state.totalBytesToDownload(),
                    "packageName" to state.packageName(),
                    "installErrorCode" to state.installErrorCode(),
                )
            )
            when (state.installStatus()) {
                InstallStatus.DOWNLOADED -> {
                    unregisterListeners()
                }
                InstallStatus.CANCELED -> {
                    eventSink?.error("CANCELED", "Canceled screen", null)
                    unregisterListeners()
                }
                InstallStatus.UNKNOWN -> {
                    eventSink?.error("UNKNOWN", "Unknown error", null)
                    unregisterListeners()
                }
                InstallStatus.FAILED -> {
                    eventSink?.error("FAILED", "FAILED error", null)
                    unregisterListeners()
                }
                InstallStatus.DOWNLOADING -> {
                }
                InstallStatus.INSTALLED -> {
                    unregisterListeners()
                }
                InstallStatus.INSTALLING -> {
                }
                InstallStatus.PENDING -> {
                }
                InstallStatus.REQUIRES_UI_INTENT -> {
                }
            }
        }
        appUpdateManager.registerListener(listener!!)
    }

    fun unregisterListeners() {
        if (listener != null) {
            appUpdateManager.unregisterListener(listener!!)
            listener = null
        }
    }

    fun destroy() {
        instance = null
    }
}
