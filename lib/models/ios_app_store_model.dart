class IosAppStoreModel {
  int? resultCount;
  List<Result> results = [];

  IosAppStoreModel.fromJson(Map<String, dynamic> json) {
    resultCount = json['resultCount'];
    results = (json['results'] as List).map((result) => Result.fromJson(result.cast<String, dynamic>())).toList();
  }
}

class Result {
  List<String> advisories = [];
  List<String> supportedDevices = [];
  List<String> ipadScreenshotUrls = [];
  List<String> appleTvScreenshotUrls = [];
  String? artworkUrl60;
  String? artworkUrl512;
  String? artworkUrl100;
  String? artistViewUrl;
  String? kind;
  String? releaseNotes;
  String? minimumOsVersion;
  String? description;
  List<String>? genreIds = [];
  String? primaryGenreName;
  int? primaryGenreId;
  String? sellerName;
  bool? isVppDeviceBasedLicensingEnabled;
  String? releaseDate;
  String? bundleId;
  int? trackId;
  String? trackName;
  String? currency;
  String? currentVersionReleaseDate;
  String? trackCensoredName;
  List<String>? languageCodesISO2A;
  String? fileSizeBytes;
  String? formattedPrice;
  double? averageUserRatingForCurrentVersion;
  int? userRatingCountForCurrentVersion;
  double? averageUserRating;
  String? trackViewUrl;
  String? trackContentRating;
  int? artistId;
  String? artistName;
  List<String>? genres = [];
  double? price;
  String? version;
  String? wrapperType;
  int? userRatingCount;

  Result.fromJson(Map<String, dynamic> json) {
    advisories = (json['advisories'] as List).map((e) => e.toString()).toList();
    supportedDevices = (json['supportedDevices'] as List).map((e) => e.toString()).toList();
    ipadScreenshotUrls = (json['ipadScreenshotUrls'] as List).map((e) => e.toString()).toList();
    appleTvScreenshotUrls = (json['appletvScreenshotUrls'] as List).map((e) => e.toString()).toList();
    artworkUrl60 = json['artworkUrl60'];
    artworkUrl512 = json['artworkUrl512'];
    artworkUrl100 = json['artworkUrl100'];
    kind = json['kind'];
    releaseNotes = json['releaseNotes'];
    minimumOsVersion = json['minimumOsVersion'];
    description = json['description'];
    genreIds = (json['genreIds'] as List).map((e) => e.toString()).toList();
    sellerName = json['sellerName'];
    isVppDeviceBasedLicensingEnabled = json['isVppDeviceBasedLicensingEnabled'];
    releaseDate = json['releaseDate'];
    bundleId = json['bundleId'];
    trackId = json['trackId'];
    trackName = json['trackName'];
    currency = json['currency'];
    currentVersionReleaseDate = json['currentVersionReleaseDate'];
    trackCensoredName = json['trackCensoredName'];
    languageCodesISO2A = (json['languageCodesISO2A'] as List).map((e) => e.toString()).toList();
    fileSizeBytes = json['fileSizeBytes'];
    formattedPrice = json['formattedPrice'];
    averageUserRatingForCurrentVersion = json['averageUserRatingForCurrentVersion'];
    userRatingCountForCurrentVersion = json['userRatingCountForCurrentVersion'];
    averageUserRating = json['averageUserRating'];
    trackViewUrl = json['trackViewUrl'];
    trackContentRating = json['trackContentRating'];
    artistId = json['artistId'];
    artistName = json['artistName'];
    genres = (json['genres'] as List).map((e) => e.toString()).toList();
    price = json['price']?.toDouble();
    version = json['version'];
    wrapperType = json['wrapperType'];
    userRatingCount = json['userRatingCount'];
  }
}
