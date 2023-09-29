import 'dart:async';
import 'dart:io';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:in_app_updates/in_app_updates.dart';
import 'package:in_app_updates/models/models.dart';
import 'package:in_app_updates_example/ios_update_dialog.dart';
import 'package:in_app_updates_example/version.dart';

void main() {
  runApp(const MyApp(false));
}

class MyApp extends StatefulWidget {
  final bool isHuawei;

  const MyApp(this.isHuawei, {super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  IosAppStoreModel? iosAppStoreModel;
  AndroidInAppUpdateInfoModel? androidInAppUpdateInfoModel;
  StreamSubscription<AndroidInAppUpdateEventChannelModel>? androidStreamSubscription;
  StreamSubscription<HuaweiInAppUpdateInfoModel>? huaweiStreamSubscription;

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    if (Platform.isAndroid) {
      if (widget.isHuawei) {
        InAppUpdates().getHuaweiUpdateInfoStream().listen(
          (event) {
            if (Version.parse('0.0.0') < Version.parse(event.versionName)) {
              InAppUpdates().updateApp(huaweiMustBtnOne: false);
            }
          },
          onError: (error) {},
          cancelOnError: false,
        );
      } else {
        InAppUpdates().getAndroidUpdateInfoStream().listen(
          (event) {
            if (event.installStatus == InstallStatus.downloaded) {
              ScaffoldMessenger.of(context).showSnackBar(
                SnackBar(
                  content: Row(
                    children: [
                      const Text("Update right now"),
                      TextButton(
                        onPressed: () => InAppUpdates().completeUpdate(),
                        child: const Text("Update"),
                      ),
                    ],
                  ),
                ),
              );
            }
          },
          onError: (error) {},
          cancelOnError: false,
        );
      }
    }
    try {
      final res = await InAppUpdates().isUpdateAvailable(
        isHuawei: false,
        iosAppStoreRequestModel: IosAppStoreRequestModel(id: '0000000000', country: 'ru'),
      );
      if (res is IosAppStoreModel) {
        iosAppStoreModel = res;
        Future.sync(
          () {
            if (Version.parse('0.0.0') < Version.parse(iosAppStoreModel!.results.first.version)) {
              showDialog(
                context: context,
                builder: (context) => IosUpdateDialog(
                  title: "New version available",
                  iosAppStoreModel: res,
                  updateButtonText: "Update now",
                  remindMeLater: "Remind me later",
                ),
              );
            }
          },
        );
      }
      if (res is AndroidInAppUpdateInfoModel) {
        androidInAppUpdateInfoModel = res;
        InAppUpdates().updateApp(
          androidInAppUpdateRequestModel: AndroidInAppUpdateRequestConfig(type: AppUpdateType.immediate),
        );
      }
      setState(() {});
    } catch (e) {
      ///
    }
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Builder(
          builder: (context) => GestureDetector(
            onTap: () => showCupertinoDialog(
              context: context,
              builder: (ctx) => IosUpdateDialog(
                title: "New version available",
                iosAppStoreModel: iosAppStoreModel!,
                updateButtonText: "Update now",
                remindMeLater: "Remind me later",
              ),
            ),
            child: const Center(
              child: Text('Running on:'),
            ),
          ),
        ),
      ),
    );
  }
}
