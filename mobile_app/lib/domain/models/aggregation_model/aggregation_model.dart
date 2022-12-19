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

class Aggregation with ChangeNotifier {
  List<String> _shops = [];
  List<String> _brands = [];
  SortingParameter _sortingParameter = SortingParameter.discount;
  SortingDirection _sortingDirection = SortingDirection.descending;
  String _search = '';
  List<String> _currentShops = [];
  List<String> _currentBrands = [];

  List<String> get shops => _currentShops;
  List<String> get brands => _currentBrands;
  SortingParameter get sortingParameter => _sortingParameter;
  SortingDirection get sortingDirection => _sortingDirection;
  String get search => _search;
  bool get isNotInit => _shops.isEmpty;
  List<String> get allShops => _shops;
  List<String> get allBrands => _brands;

  void init(List<String> shops, List<String> brands) {
    _shops = shops;
    _brands = brands;
    _currentShops = shops;
    _currentBrands = brands;
    WidgetsBinding.instance.addPostFrameCallback((_) {
      notifyListeners();
    });
  }

  set shops(List<String> value) {
    _currentShops = value;
    notifyListeners();
  }

  set brands(List<String> value) {
    _currentBrands = value;
    notifyListeners();
  }

  set sortingParameter(SortingParameter value) {
    _sortingParameter = value;
    notifyListeners();
  }

  set sortingDirection(SortingDirection value) {
    _sortingDirection = value;
    notifyListeners();
  }

  set search(String value) {
    _search = value;
    notifyListeners();
  }

  List<MapEntry<EnergyDrink, Image>> sort(List<Shop> shops) {
    List<MapEntry<EnergyDrink, Image>> drinks = [];
    for (int i = 0; i < shops.length; i++) {
      if (!_currentShops.contains(shops[i].name)) continue;
      for (int j = 0; j < shops[i].energyDrinks.length; j++) {
        if (_currentBrands.contains(shops[i].energyDrinks[j].brand) &&
            shops[i]
                .energyDrinks[j]
                .fullName
                .toLowerCase()
                .contains(search.toLowerCase())) {
          drinks.add(MapEntry(shops[i].energyDrinks[j], shops[i].image));
        }
      }
    }
    if (_sortingParameter == SortingParameter.price) {
      if (_sortingDirection == SortingDirection.ascending) {
        drinks.sort(
          (drink2, drink1) => drink2.key.priceWithDiscount
              .compareTo(drink1.key.priceWithDiscount),
        );
      } else {
        drinks.sort(
          (drink1, drink2) => drink2.key.priceWithDiscount
              .compareTo(drink1.key.priceWithDiscount),
        );
      }
    } else {
      if (_sortingDirection == SortingDirection.ascending) {
        drinks.sort(
          (drink2, drink1) =>
              drink2.key.discount.compareTo(drink1.key.discount),
        );
      } else {
        drinks.sort(
          (drink1, drink2) =>
              drink2.key.discount.compareTo(drink1.key.discount),
        );
      }
    }
    return drinks;
  }
}
