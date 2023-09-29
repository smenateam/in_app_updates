enum AppUpdateType {
  flexible,
  immediate,
}

class AndroidInAppUpdateRequestConfig {
  final AppUpdateType type;
  final bool allowAssetPackDeletion;

  AndroidInAppUpdateRequestConfig({required this.type, this.allowAssetPackDeletion = false});

  Map<String, dynamic> toJson() => {
        "immediate": type == AppUpdateType.immediate,
        "flexible": type == AppUpdateType.flexible,
        "allowAssetPackDeletion": allowAssetPackDeletion,
      };
}
