import 'package:in_app_updates/models/models.dart';

import 'in_app_updates_platform_interface.dart';

class InAppUpdates {
  /// get info about app
  /// return type can be IosAppStoreModel if i Platform.isIOS
  /// or AndroidInAppUpdateInfoModel if i Platform.isAndroid(not Huawei)
  Future<dynamic> isUpdateAvailable({
    IosAppStoreRequestModel? iosAppStoreRequestModel,
    bool? isHuawei,
  }) =>
      InAppUpdatesPlatform.instance.isUpdateAvailable(
        iosAppStoreRequestModel: iosAppStoreRequestModel,
        isHuawei: isHuawei,
      );

  /// get current status and bytesDownloaded android
  /// use like this first InAppUpdates().getAndroidUpdateInfoStream().listen((event) {}
  /// after that use InAppUpdates().updateApp(
  ///       androidInAppUpdateRequestModel: AndroidInAppUpdateRequestModel(type: AppUpdateType.flexible/immediate),
  ///     );
  Stream<AndroidInAppUpdateEventChannelModel> getAndroidUpdateInfoStream() =>
      InAppUpdatesPlatform.instance.getAndroidStream().map(
            (json) => AndroidInAppUpdateEventChannelModel.fromJson(json.cast<String, dynamic>()),
          );

  /// get update data or failure from huawei
  /// use like this first InAppUpdates().getHuaweiUpdateInfoStream().listen((event) {
  /// (data) {
  ///                 if (currentVersion) < event.versionName) {
  ///                   updateApp(huaweiMustBtnOne: false),
  ///                 }
  ///               },}
  Stream<HuaweiInAppUpdateInfoModel> getHuaweiUpdateInfoStream() =>
      InAppUpdatesPlatform.instance.getAndroidStream().map(
            (json) => HuaweiInAppUpdateInfoModel.fromJson(json.cast<String, dynamic>()),
          );

  /// use only for android(not huawei) and when we want force update after status InstallStatus.downloaded
  /// you can use it with custom dialog or snackbar
  Future<bool?> completeUpdate() async => await InAppUpdatesPlatform.instance.completeUpdate();

  /// update app
  /// pass IosAppStoreRequestModel for open app store for update
  /// pass AndroidInAppUpdateRequestModel and choose type for update
  /// pass huaweiMustBtnOne for showDialog
  /// JosApps.getAppUpdateClient(activityApp).showUpdateDialog(activityApp, upgradeInfo, mustBtnOne)
  Future<void> updateApp({
    AndroidInAppUpdateRequestConfig? androidInAppUpdateRequestModel,
    bool? huaweiMustBtnOne,
  }) =>
      InAppUpdatesPlatform.instance.updateApp(
        androidInAppUpdateRequestModel: androidInAppUpdateRequestModel,
        mustBtnOne: huaweiMustBtnOne,
      );
}
