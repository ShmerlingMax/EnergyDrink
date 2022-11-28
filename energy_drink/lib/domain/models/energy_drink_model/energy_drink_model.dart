import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:json_annotation/json_annotation.dart';

part 'energy_drink_model.g.dart';

@JsonSerializable()
class EnergyDrink {
  EnergyDrink(
    this.fullName,
    this.brand,
    this.image,
    this.priceWithDiscount,
    this.priceWithOutDiscount,
    this.discount,
    this.volume,
  );

  String fullName;
  String brand;
  @JsonKey(fromJson: _convertStringToImage, toJson: _convertImageToString)
  Image image;
  double priceWithDiscount;
  double priceWithOutDiscount;
  double discount;
  int volume;

  factory EnergyDrink.fromJson(Map<String, dynamic> json) =>
      _$EnergyDrinkFromJson(json);

  Map<String, dynamic> toJson() => _$EnergyDrinkToJson(this);
}

Image _convertStringToImage(String json) => Image.memory(base64Decode(json));

String _convertImageToString(Image image) => image.toString();
