import 'package:flutter/material.dart';

import '../energy_drink_model/energy_drink_model.dart';
import '../shop_model/shop_model.dart';

enum SortingParameter {
  price,
  discount,
}

enum SortingDirection {
  ascending,
  descending,
}

class Aggregation {
  Aggregation(
    this.shops,
    this.brands,
    this.sortingParameter,
    this.sortingDirection,
  );
  List<String> shops;
  List<String> brands;
  SortingParameter sortingParameter;
  SortingDirection sortingDirection;

  List<MapEntry<EnergyDrink, Image>> sort(List<Shop> shops) {
    List<MapEntry<EnergyDrink, Image>> drinks = [];
    for (int i = 0; i < shops.length; i++) {
      if (!this.shops.contains(shops[i].name)) continue;
      for (int j = 0; j < shops[i].energyDrinks.length; j++) {
        if (brands.contains(shops[i].energyDrinks[j].brand)) {
          drinks.add(MapEntry(shops[i].energyDrinks[j], shops[i].image));
        }
      }
    }
    if (sortingParameter == SortingParameter.price) {
      if (sortingDirection == SortingDirection.ascending) {
        drinks.sort(
          (drink1, drink2) => drink1.key.priceWithDiscount
              .compareTo(drink2.key.priceWithDiscount),
        );
      } else {
        drinks.sort(
          (drink1, drink2) => drink1.key.priceWithDiscount
              .compareTo(drink2.key.priceWithDiscount),
        );
      }
    } else {
      if (sortingDirection == SortingDirection.ascending) {
        drinks.sort(
          (drink1, drink2) =>
              drink1.key.discount.compareTo(drink2.key.discount),
        );
      } else {
        drinks.sort(
          (drink1, drink2) =>
              drink1.key.discount.compareTo(drink2.key.discount),
        );
      }
    }
    return drinks;
  }
}
