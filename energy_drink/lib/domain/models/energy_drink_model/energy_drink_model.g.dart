// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'energy_drink_model.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

EnergyDrink _$EnergyDrinkFromJson(Map<String, dynamic> json) => EnergyDrink(
      json['fullName'] as String,
      json['brand'] as String,
      _convertStringToImage(json['image'] as String),
      (json['priceWithDiscount'] as num).toDouble(),
      (json['priceWithOutDiscount'] as num).toDouble(),
      (json['discount'] as num).toDouble(),
      json['volume'] as int,
    );

Map<String, dynamic> _$EnergyDrinkToJson(EnergyDrink instance) =>
    <String, dynamic>{
      'fullName': instance.fullName,
      'brand': instance.brand,
      'image': _convertImageToString(instance.image),
      'priceWithDiscount': instance.priceWithDiscount,
      'priceWithOutDiscount': instance.priceWithOutDiscount,
      'discount': instance.discount,
      'volume': instance.volume,
    };
