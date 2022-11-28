// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'shop_model.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

Shop _$ShopFromJson(Map<String, dynamic> json) => Shop(
      json['name'] as String,
      _convertStringToImage(json['image'] as String),
      (json['energyDrinks'] as List<dynamic>)
          .map((e) => EnergyDrink.fromJson(e as Map<String, dynamic>))
          .toList(),
    );

Map<String, dynamic> _$ShopToJson(Shop instance) => <String, dynamic>{
      'name': instance.name,
      'image': _convertImageToString(instance.image),
      'energyDrinks': instance.energyDrinks,
    };
