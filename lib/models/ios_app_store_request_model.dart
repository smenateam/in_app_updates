class IosAppStoreRequestModel {
  final String id;
  final String country;

  IosAppStoreRequestModel({required this.id, required this.country});

  Map<String, dynamic> toJson() => {
        "id": id,
        "country": country,
      };
}
