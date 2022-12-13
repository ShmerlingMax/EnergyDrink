import 'package:energy_drink/domain/models/energy_drink_model/energy_drink_model.dart';
import 'package:flutter/material.dart';

class Item extends StatelessWidget {
  final EnergyDrink energyDrink;
  final int index;

  const Item(this.energyDrink, this.index, {Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Container(
      height: 300,
      decoration: BoxDecoration(
        border: Border(
          bottom: const BorderSide(
            color: Colors.black,
            width: 1,
          ),
          right: (index.isEven)
              ? const BorderSide(color: Colors.black, width: 0.5)
              : const BorderSide(color: Colors.black, width: 1),
          left: (index.isEven)
              ? const BorderSide(color: Colors.black, width: 1)
              : const BorderSide(color: Colors.black, width: 0.5),
        ),
      ),
      child: Center(
        child: Text(energyDrink.fullName),
      ),
    );
  }
}
