import 'dart:convert';

import 'package:equatable/equatable.dart';
import 'package:flutter/material.dart';
import 'package:json_annotation/json_annotation.dart';
import '../energy_drink_model/energy_drink_model.dart';

part 'shop_model.g.dart';

@JsonSerializable()
class Shop extends Equatable {
  Shop(
    this.name,
    this.image,
    this.energyDrinks,
  );
  String name;
  @JsonKey(fromJson: _convertStringToImage, toJson: _convertImageToString)
  Image image;
  List<EnergyDrink> energyDrinks;

  @override
  List<Object> get props => [name, energyDrinks];

  factory Shop.fromJson(Map<String, dynamic> json) => _$ShopFromJson(json);

  Map<String, dynamic> toJson() => _$ShopToJson(this);
}

Image _convertStringToImage(String json) => Image.memory(base64Decode(json));

String _convertImageToString(Image image) => image.toString();
