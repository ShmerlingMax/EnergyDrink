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
    //TODO
    return [];
  }
}
