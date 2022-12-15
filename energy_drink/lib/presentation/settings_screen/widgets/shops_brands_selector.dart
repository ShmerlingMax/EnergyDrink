import 'package:energy_drink/domain/models/selection_model/selection_model.dart';
import 'package:energy_drink/domain/models/settings_model/settings_model.dart';
import 'package:energy_drink/presentation/selection_screen/selection_screen.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

class ShopsBrandsSelector extends StatelessWidget {
  final bool isShops;

  const ShopsBrandsSelector(this.isShops, {Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    List<String> elements = isShops
        ? context.watch<Settings>().shops
        : context.watch<Settings>().brands;
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Padding(
          padding: const EdgeInsets.only(left: 20),
          child: Text(
            isShops ? 'Магазины:' : 'Бренды: ',
            style: const TextStyle(fontSize: 15),
          ),
        ),
        const SizedBox(
          height: 6,
        ),
        Wrap(
          children: [
            ...elements.map((e) => _Item(isShops, e)),
            Container(
              color: const Color(0xFFB2C2D7),
              height: 32,
              child: GestureDetector(
                onTap: () {
                  final settings = context.read<Settings>();
                  context.read<SelectionModel>().init(
                        settings.shops.toList(),
                        settings.brands.toList(),
                      );
                  Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (context) => SelectionScreen(isShops),
                    ),
                  );
                },
                child: const Padding(
                  padding: EdgeInsets.only(
                    left: 10,
                    top: 8,
                    bottom: 8,
                    right: 8,
                  ),
                  child: Text(
                    'Добавить',
                    style: TextStyle(fontSize: 14),
                  ),
                ),
              ),
            ),
          ],
        ),
      ],
    );
  }
}

class _Item extends StatelessWidget {
  final bool isShops;
  final String text;

  const _Item(this.isShops, this.text, {Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.only(right: 20, bottom: 15),
      child: Container(
        color: const Color(0xFFB7D6FF),
        height: 32,
        child: Padding(
          padding: const EdgeInsets.only(
            left: 10,
            top: 8,
            bottom: 8,
            right: 5,
          ),
          child: Row(
            mainAxisSize: MainAxisSize.min,
            children: [
              Text(
                text,
                style: const TextStyle(fontSize: 14),
              ),
              GestureDetector(
                onTap: () {
                  if (isShops) {
                    final newShops = context.read<Settings>().shops;
                    newShops.remove(text);
                    context.read<Settings>().shops = newShops;
                  } else {
                    final newBrands = context.read<Settings>().brands;
                    newBrands.remove(text);
                    context.read<Settings>().brands = newBrands;
                  }
                },
                child: Image.asset(
                  'assets/close.png',
                  height: 24,
                  width: 24,
                ),
              )
            ],
          ),
        ),
      ),
    );
  }
}
