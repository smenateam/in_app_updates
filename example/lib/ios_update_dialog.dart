import 'package:flutter/cupertino.dart';
import 'package:in_app_updates/in_app_updates.dart';
import 'package:in_app_updates/models/models.dart';

class IosUpdateDialog extends StatelessWidget {
  final IosAppStoreModel iosAppStoreModel;
  final String title;
  final String updateButtonText;
  final String remindMeLater;

  const IosUpdateDialog({
    required this.title,
    required this.iosAppStoreModel,
    required this.updateButtonText,
    required this.remindMeLater,
    Key? key,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return CupertinoAlertDialog(
      title: Text(title),
      content: Column(
        children: [
          Text(iosAppStoreModel.results.first.version ?? ""),
          Text(iosAppStoreModel.results.first.sellerName ?? ""),
          Text(iosAppStoreModel.results.first.releaseNotes ?? ""),
        ],
      ),
      actions: [
        GestureDetector(
          onTap: () => InAppUpdates().updateApp(),
          child: CupertinoDialogAction(
            child: Text(updateButtonText),
          ),
        ),
        GestureDetector(
          onTap: () => Navigator.pop(context),
          child: CupertinoDialogAction(
            child: Text(remindMeLater),
          ),
        ),
      ],
    );
  }
}
