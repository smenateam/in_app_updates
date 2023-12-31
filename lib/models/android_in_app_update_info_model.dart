class AndroidInAppUpdateInfoModel {
  int? updateAvailability;
  bool? immediateAllowed;
  bool? isUpdateAvailable;
  bool? flexibleAllowed;
  int? availableVersionCode;
  int? installStatus;
  String? packageName;
  int? clientVersionStalenessDays;
  int? updatePriority;
  int? totalBytesToDownload;

  AndroidInAppUpdateInfoModel.fromJson(Map<String, dynamic> json) {
    updateAvailability = json['updateAvailability'];
    immediateAllowed = json['immediateAllowed'];
    isUpdateAvailable = json['isUpdateAvailable'];
    flexibleAllowed = json['flexibleAllowed'];
    availableVersionCode = json['availableVersionCode'];
    installStatus = json['installStatus'];
    packageName = json['packageName'];
    clientVersionStalenessDays = json['clientVersionStalenessDays'];
    updatePriority = json['updatePriority'];
    totalBytesToDownload = json['totalBytesToDownload'];
  }
}
