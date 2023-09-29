class HuaweiInAppUpdateInfoModel {
  String? appId;
  String? appName;
  String? packageName;
  String? versionName;
  int? diffSize;
  String? diffDownUrl;
  String? diffSha2;
  int? sameS;
  int? size;
  String? releaseDate;
  String? icon;
  int? oldVersionCode;
  int? versionCode;
  String? downUrl;
  String? newFeatures;
  String? releaseDateDesc;
  String? detailId;
  String? fullDownUrl;
  int? bundleSize;
  int? devType;
  int? isAutoUpdate;
  String? oldVersionName;
  int? isCompulsoryUpdate;
  String? notRcmReason;

  HuaweiInAppUpdateInfoModel.fromJson(Map<String, dynamic> json) {
    appId = json['appId'];
    appName = json['appName'];
    packageName = json['packageName'];
    versionName = json['versionName'];
    diffSize = json['diffSize'];
    diffDownUrl = json['diffDownUrl'];
    diffSha2 = json['diffSha2'];
    sameS = json['sameS'];
    size = json['size'];
    releaseDate = json['releaseDate'];
    icon = json['icon'];
    oldVersionCode = json['oldVersionCode'];
    versionCode = json['versionCode'];
    downUrl = json['downUrl'];
    newFeatures = json['newFeatures'];
    releaseDateDesc = json['releaseDateDesc'];
    detailId = json['detailId'];
    fullDownUrl = json['fullDownUrl'];
    bundleSize = json['bundleSize'];
    devType = json['devType'];
    isAutoUpdate = json['isAutoUpdate'];
    oldVersionName = json['oldVersionName'];
    isCompulsoryUpdate = json['isCompulsoryUpdate'];
    notRcmReason = json['notRcmReason'];
  }
}
