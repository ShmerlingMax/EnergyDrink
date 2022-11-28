import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:json_annotation/json_annotation.dart';
import 'package:energy_drink/domain/models/energy_drink_model/energy_drink_model.dart';

part 'shop_model.g.dart';

@JsonSerializable()
class Shop {
  Shop(
    this.name,
    this.image,
    this.energyDrinks,
  );
  String name;
  @JsonKey(fromJson: _convertStringToImage, toJson: _convertImageToString)
  Image image;
  List<EnergyDrink> energyDrinks;

  factory Shop.fromJson(Map<String, dynamic> json) => _$ShopFromJson(json);

  Map<String, dynamic> toJson() => _$ShopToJson(this);
}

Image _convertStringToImage(String json) => Image.memory(base64Decode(json));

String _convertImageToString(Image image) => image.toString();
