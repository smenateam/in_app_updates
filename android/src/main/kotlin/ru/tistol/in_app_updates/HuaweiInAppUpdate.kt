package ru.tistol.in_app_updates

import android.app.Activity
import android.content.Intent

import com.huawei.hms.jos.AppUpdateClient
import com.huawei.hms.jos.JosApps
import com.huawei.updatesdk.service.appmgr.bean.ApkUpgradeInfo
import com.huawei.updatesdk.service.otaupdate.CheckUpdateCallBack
import com.huawei.updatesdk.service.otaupdate.UpdateKey
import com.huawei.updatesdk.service.otaupdate.UpdateStatusCode
import io.flutter.plugin.common.EventChannel

import io.flutter.plugin.common.MethodChannel

class HuaweiInAppUpdate private constructor() {
    companion object {

        @Volatile
        private var instance: HuaweiInAppUpdate? = null

        fun getInstance() = instance ?: synchronized(this) {
            instance ?: HuaweiInAppUpdate().also { instance = it }
        }
    }

    private var client: AppUpdateClient? = null
    private var upgradeInfo: ApkUpgradeInfo? = null
    private var status: Int? = null
    private var eventSink: EventChannel.EventSink? = null

    fun checkForUpdateHuawei(
        eventS: EventChannel.EventSink?=null,
        activityApp: Activity?,
    ) {
        eventSink = eventS
        client = JosApps.getAppUpdateClient(activityApp)
        client?.checkAppUpdate(
            activityApp,
            object : CheckUpdateCallBack {
                override fun onUpdateInfo(intent: Intent?) {
                    status = intent?.getIntExtra(UpdateKey.STATUS, -99)
                    val errorDetails = mapOf<String, Any?>(
                        "code" to intent?.getIntExtra(UpdateKey.FAIL_CODE, -99),
                        "reason" to intent?.getStringExtra(UpdateKey.FAIL_REASON),
                    )
                    if (status == UpdateStatusCode.HAS_UPGRADE_INFO) {
                        val info = intent?.getSerializableExtra(UpdateKey.INFO)
                        if (info is ApkUpgradeInfo) {
                            upgradeInfo = info
                            val successResult = hashMapOf(
                                "appId" to info.id_,
                                "appName" to info.name_,
                                "packageName" to info.package_,
                                "versionName" to info.version_,
                                "diffSize" to info.diffSize_,
                                "diffDownUrl" to info.diffDownUrl_,
                                "diffSha2" to info.diffSha2_,
                                "sameS" to info.sameS_,
                                "size" to info.longSize_,
                                "releaseDate" to info.releaseDate_,
                                "icon" to info.icon_,
                                "oldVersionCode" to info.oldVersionCode_,
                                "versionCode" to info.versionCode_,
                                "downUrl" to info.downurl_,
                                "newFeatures" to info.newFeatures_,
                                "releaseDateDesc" to info.releaseDateDesc_,
                                "detailId" to info.detailId_,
                                "fullDownUrl" to info.fullDownUrl_,
                                "bundleSize" to info.bundleSize_,
                                "devType" to info.devType_,
                                "isAutoUpdate" to info.isAutoUpdate_,
                                "oldVersionName" to info.oldVersionName_,
                                "isCompulsoryUpdate" to info.isCompulsoryUpdate_,
                                "notRcmReason" to info.notRcmReason_
                            )
                            eventSink?.success(successResult)
                        } else {
                            eventSink?.error(
                                "NO_UPGRADE_INFO",
                                "No update is available",
                                errorDetails
                            )
                        }
                    } else if (status == UpdateStatusCode.PARAMER_ERROR) {
                        eventSink?.error("PARAMETER_ERROR", "Parameter is incorrect", errorDetails)
                    } else if (status == UpdateStatusCode.CONNECT_ERROR) {
                        eventSink?.error(
                            "CONNECT_ERROR", "Network connection is incorrect", errorDetails
                        )
                    } else if (status == UpdateStatusCode.NO_UPGRADE_INFO) {
                        eventSink?.error("NO_UPGRADE_INFO", "No update is available", errorDetails)
                    } else if (status == UpdateStatusCode.CANCEL) {
                        eventSink?.error("CANCEL", "User cancels the update", errorDetails)
                    } else if (status == UpdateStatusCode.INSTALL_FAILED) {
                        eventSink?.error("INSTALL_FAILED", "App update fails", errorDetails)
                    } else if (status == UpdateStatusCode.CHECK_FAILED) {
                        eventSink?.error(
                            "CHECK_FAILED", "Update information fails to be queried", errorDetails
                        )
                    } else if (status == UpdateStatusCode.MARKET_FORBID) {
                        eventSink?.error(
                            "MARKET_FORBID",
                            "HUAWEI AppGallery is disabled",
                            errorDetails
                        )
                    } else if (status == UpdateStatusCode.IN_MARKET_UPDATING) {
                        eventSink?.error("IN_MARKET_UPDATING", "App is being updated", errorDetails)
                    } else {
                        eventSink?.error("UNKNOWN_STATUS", "Status unknown", errorDetails)
                    }
                }

                override fun onMarketInstallInfo(intent: Intent?) {
                }

                override fun onMarketStoreError(responseCode: Int) {
                }

                override fun onUpdateStoreError(responseCode: Int) {
                }

            },
        )
    }

    fun updateHuawei(
        result: MethodChannel.Result,
        activityApp: Activity?,
        mustBtnOne: Boolean,
    ) {
        if (upgradeInfo == null) {
            result.error(
                "IN_APP_UPDATES_HUAWEI", "Use isUpdateAvailable first then updateApp", null
            )
            return
        }
        if (status != UpdateStatusCode.HAS_UPGRADE_INFO) {
            result.error(
                "ANDROID_APP_UPDATED",
                "The app already updated",
                null,
            )
            return
        }
        JosApps.getAppUpdateClient(activityApp)
            .showUpdateDialog(activityApp, upgradeInfo, mustBtnOne)
    }
}
