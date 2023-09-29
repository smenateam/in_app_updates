# in_app_updates

Flutter plugin, with which you can use in_app_update in Huawei, Android(google services) and iOS
systems
First of all app should be in App Store, Play market, App Gallery and the project must contain
keyStore files

## Adding dependencies

To use the plugin, add it to the pubspec.yaml file.

## Installing

### Android(Google services)

First you need to open a stream to receive installation data

```
 InAppUpdates().getAndroidUpdateInfoStream().listen(
          (event) {
          // Here we check our status, if app already downloaded we show snackbar with button
          // which force update our app
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
```

After that we should get data from stores and check with our local version(code before)
Also we should choose type AppUpdateType.immediate or AppUpdateType.flexible read about this
here[here](https://developer.android.com/guide/playcore/in-app-updates/kotlin-java). You can check
googleServices with this
plugin [google_api_availability](https://pub.dev/packages/google_api_availability)
and path bool to isHuawei:true/false

```
final res = await InAppUpdates().isUpdateAvailable(
        isHuawei: false,
      );
```

if our system is Android without google services then we check type is AndroidInAppUpdateInfoModel
and also we can check current version with AndroidInAppUpdateInfoModel(version)
Finally, we use updateApp function and pass our AppUpdateType.immediate or AppUpdateType.flexible
for open dialog or page

```
if (res is AndroidInAppUpdateInfoModel) {
    InAppUpdates().updateApp(
        androidInAppUpdateRequestModel: AndroidInAppUpdateRequestConfig(type: AppUpdateType.immediate),
    );
}
```

If you use AppUpdateType.flexible your app update in background state, or when app will be close

### Android(Without Google services) - Huawei

First you need to open a stream to receive installation data

```
 InAppUpdates().getHuaweiUpdateInfoStream().listen(
          (event) {
            if (Version.parse('0.0.0') < Version.parse(event.versionName)) {
              InAppUpdates().updateApp(huaweiMustBtnOne: false);
            }
          },
          onError: (error) {},
          cancelOnError: false,
        );
```

After that we should get data from stores and check with our local version(code before)
You can read about
in_app_updates [here](https://developer.huawei.com/consumer/en/doc/development/AppGallery-connect-Guides/appgallerykit-app-update-0000001055118286)
You can check googleServices with this
plugin [google_api_availability](https://pub.dev/packages/google_api_availability)
and path bool to isHuawei:true/false. res will be null, because for Huawei we can get current
installation data from InAppUpdates().getHuaweiUpdateInfoStream(), but we need trigger for check
update and use isUpdateAvailable

```
final res = await InAppUpdates().isUpdateAvailable(
        isHuawei: true,
      );
```

### IOS

iOS doesn't have this feature, but we can check current version in appStore with local and show
dialog which can redirect us in appStore on our app First of all we need get this data from appStore
call this function with id(id your app in appStore)
and country(country where our application is available)

```
 final res = await InAppUpdates().isUpdateAvailable(
        iosAppStoreRequestModel: IosAppStoreRequestModel(id: '0000000000', country: 'ru'),
      );
```

After that we check platform and check version for showDialog or page or something else with button
which call InAppUpdates().updateApp() for redirect us in appStore

```
if (Platform.isIOS) {
            final iosAppStoreModel = data as IosAppStoreModel;
            if (Version.parse(sl<AppCubit>().state.version) < Version.parse(iosAppStoreModel.results.first.version)) {
              showDialog(
                context: context,
                builder: (context) => IosUpdateDialog(
                  title: "New version available",
                  iosAppStoreModel: data,
                  updateButtonText: "Update now",
                  remindMeLater: "Remind me later",
                ),
              );
            }
          }
```