import 'package:energy_drink/domain/models/energy_drink_model/energy_drink_model.dart';
import 'package:flutter/material.dart';

class Item extends StatelessWidget {
  final MapEntry<EnergyDrink, Image> energyDrink;
  final int index;

  const Item(this.energyDrink, this.index, {Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    final itemWidth = MediaQuery.of(context).size.width / 3;
    final itemHeight = MediaQuery.of(context).size.height / 5.2;
    return Container(
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
      child: Column(
        mainAxisAlignment: MainAxisAlignment.end,
        children: [
          SizedBox(
            width: itemWidth,
            height: itemHeight,
            child: FittedBox(
              fit: BoxFit.cover,
              child: energyDrink.key.image,
            ),
          ),
          Text(energyDrink.key.fullName),
          const SizedBox(
            height: 10,
          ),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceAround,
            children: [
              Text('${energyDrink.key.priceWithDiscount} ₽'),
              Text('${energyDrink.key.volume} мл'),
            ],
          ),
          const SizedBox(
            height: 10,
          ),
          Row(
            children: [
              const SizedBox(
                width: 10,
              ),
              Text(
                '${energyDrink.key.priceWithOutDiscount} ₽',
                style: const TextStyle(decoration: TextDecoration.lineThrough),
              ),
              const SizedBox(
                width: 5,
              ),
              Text(
                '${(energyDrink.key.discount * 100).round()}% OFF',
                style: const TextStyle(
                  color: Colors.red,
                ),
              ),
              const Spacer(),
              SizedBox(
                width: 50,
                height: 30,
                child: energyDrink.value,
              ),
              const SizedBox(
                width: 5,
              ),
            ],
          ),
          const SizedBox(
            height: 10,
          ),
        ],
      ),
    );
  }
}
