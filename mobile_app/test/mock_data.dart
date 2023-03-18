import 'dart:convert';

import 'package:energy_drink/domain/models/shop_model/shop_model.dart';

const mockShopsNames = ["Магазин 1", "Магазин 2", "Магазин 3"];
const mockBrandsNames = ["Бренд 1", "Бренд 2", "Бренд 3"];
const mockEnergyDrinkString1 =
    '{"fullName":"Энергетик 1","brand":"Бренд 1","image":"","volume":499,"priceWithDiscount":50.00,"priceWithOutDiscount":100.00,"discount":0.75}';
const mockEnergyDrinkString2 =
    '{"fullName":"Энергетик 2","brand":"Бренд 2","image":"","volume":499,"priceWithDiscount":75.00,"priceWithOutDiscount":100.00,"discount":0.50}';
const mockEnergyDrinkString3 =
    '{"fullName":"Энергетик 3","brand":"Бренд 3","image":"","volume":499,"priceWithDiscount":100.00,"priceWithOutDiscount":100.00,"discount":0.25}';
const mockShopString1 =
    '{"name":"Магазин 1","image":"","energyDrinks":[$mockEnergyDrinkString1]}';
const mockShopString2 =
    '{"name":"Магазин 2","image":"","energyDrinks":[$mockEnergyDrinkString2]}';
const mockShopString3 =
    '{"name":"Магазин 3","image":"","energyDrinks":[$mockEnergyDrinkString3]}';
const mockShopsString =
    '{"shops":[$mockShopString1, $mockShopString2, $mockShopString3]}';
const mockBrandsString = '{"brands":["Бренд 1","Бренд 2","Бренд 3"]}';

List<Shop> getMockShops() {
  final json = jsonDecode(mockShopsString);
  final shops = (json['shops'] as List<dynamic>)
      .map((e) => Shop.fromJson(e as Map<String, dynamic>))
      .toList();
  return shops;
}

List<String> getMockBrands() {
  final json = jsonDecode(mockBrandsString);
  final brands =
      (json['brands'] as List<dynamic>).map((e) => e as String).toList();
  return brands;
}
