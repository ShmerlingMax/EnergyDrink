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

  List<EnergyDrink> sort(List<Shop> shops) {
    List<EnergyDrink> drinks = [];
    for (int i = 0; i < shops.length; i++) {
      if (!this.shops.contains(shops[i].name)) continue;
      for (int j = 0; j < shops[i].energyDrinks.length; j++) {
        if (brands.contains(shops[i].energyDrinks[j].brand)) {
          drinks.add(shops[i].energyDrinks[j]);
        }
      }
    }
    if (sortingParameter == SortingParameter.price) {
      if (sortingDirection == SortingDirection.ascending) {
        drinks.sort(
          (drink1, drink2) =>
              drink1.priceWithDiscount.compareTo(drink2.priceWithDiscount),
        );
      } else {
        drinks.sort(
          (drink1, drink2) =>
              drink1.priceWithDiscount.compareTo(drink2.priceWithDiscount),
        );
      }
    } else {
      if (sortingDirection == SortingDirection.ascending) {
        drinks.sort(
          (drink1, drink2) => drink1.discount.compareTo(drink2.discount),
        );
      } else {
        drinks.sort(
          (drink1, drink2) => drink1.discount.compareTo(drink2.discount),
        );
      }
    }
    return drinks;
  }
}
