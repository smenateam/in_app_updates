enum InstallStatus {
  unknown(0),
  pending(1),
  downloading(2),
  downloaded(11),
  installing(3),
  installed(4),
  failed(5),
  canceled(6),
  requiresUiIntent(10);

  const InstallStatus(this.value);

  factory InstallStatus.fromCode(int code) {
    return values.firstWhere(
      (e) => e.value == code,
      orElse: () => InstallStatus.unknown,
    );
  }

  final num value;
}

class AndroidInAppUpdateEventChannelModel {
  InstallStatus? installStatus;
  int? bytesDownloaded;
  int? totalBytesToDownload;
  String? packageName;
  int? installErrorCode;

  AndroidInAppUpdateEventChannelModel.fromJson(Map<String, dynamic> json) {
    installStatus = InstallStatus.fromCode(json['installStatus'] ?? -1);
    bytesDownloaded = json['bytesDownloaded'];
    totalBytesToDownload = json['totalBytesToDownload'];
    packageName = json['packageName'];
    installErrorCode = json['installErrorCode'];
  }
}
