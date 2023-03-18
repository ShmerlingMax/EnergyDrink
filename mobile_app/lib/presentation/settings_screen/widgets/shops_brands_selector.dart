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
            style: const TextStyle(
              fontSize: 15,
              fontWeight: FontWeight.w500,
            ),
          ),
        ),
        const SizedBox(
          height: 6,
        ),
        Wrap(
          children: [
            ...elements.map((e) => ShopsOrBrandsItem(isShops, e)),
            Container(
              color: const Color(0xFFB2C2D7),
              height: 32,
              child: GestureDetector(
                key: isShops
                    ? const Key('shops_add_button')
                    : const Key('brands_add_button'),
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
                    style: TextStyle(
                      fontSize: 14,
                      fontWeight: FontWeight.w500,
                    ),
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

class ShopsOrBrandsItem extends StatelessWidget {
  final bool isShops;
  final String text;

  const ShopsOrBrandsItem(this.isShops, this.text, {Key? key})
      : super(key: key);

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
                style: const TextStyle(
                  fontSize: 14,
                  fontWeight: FontWeight.w500,
                ),
              ),
              GestureDetector(
                key: isShops
                    ? const Key('shop_delete_button')
                    : const Key('brand_delete_button'),
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
