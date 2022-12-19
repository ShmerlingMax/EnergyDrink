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
      padding: const EdgeInsets.all(8),
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
          const SizedBox(
            height: 8,
          ),
          Align(
            alignment: Alignment.topLeft,
            child: Text(
              energyDrink.key.fullName,
              style: const TextStyle(
                fontSize: 16,
                fontWeight: FontWeight.w500,
              ),
            ),
          ),
          Row(
            mainAxisAlignment: MainAxisAlignment.end,
            children: [
              Text(
                '${energyDrink.key.volume} мл',
                style: const TextStyle(
                  fontSize: 10,
                  fontWeight: FontWeight.w500,
                ),
              ),
            ],
          ),
          const Spacer(),
          Row(
            mainAxisAlignment: MainAxisAlignment.start,
            children: [
              Text(
                '${energyDrink.key.priceWithDiscount} ₽',
                style: const TextStyle(
                  fontSize: 14,
                  fontWeight: FontWeight.w500,
                ),
              ),
            ],
          ),
          Row(
            children: [
              Text(
                '${energyDrink.key.priceWithOutDiscount} ₽',
                style: const TextStyle(
                  decoration: TextDecoration.lineThrough,
                  fontSize: 12,
                  fontWeight: FontWeight.w500,
                ),
              ),
              const SizedBox(
                width: 5,
              ),
              Text(
                '${(energyDrink.key.discount * 100).round()}% OFF',
                style: const TextStyle(
                  color: Colors.red,
                  fontSize: 12,
                  fontWeight: FontWeight.w500,
                ),
              ),
              const Spacer(),
              SizedBox(
                width: 50,
                height: 30,
                child: energyDrink.value,
              ),
            ],
          ),
          const Spacer(),
        ],
      ),
    );
  }
}
