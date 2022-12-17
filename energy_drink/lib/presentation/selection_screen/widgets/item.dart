import 'package:energy_drink/domain/models/selection_model/selection_model.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

class SelectionItem extends StatelessWidget {
  final bool isShops;
  final String text;
  const SelectionItem(this.isShops, this.text, {Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: () {
        final value = isShops
            ? context.read<SelectionModel>().shops.contains(text)
            : context.read<SelectionModel>().brands.contains(text);
        if (!value) {
          if (isShops) {
            final newShops = context.read<SelectionModel>().shops;
            newShops.add(text);
            context.read<SelectionModel>().shops = newShops;
          } else {
            final newBrands = context.read<SelectionModel>().brands;
            newBrands.add(text);
            context.read<SelectionModel>().brands = newBrands;
          }
        } else {
          if (isShops) {
            final newShops = context.read<SelectionModel>().shops;
            newShops.remove(text);
            context.read<SelectionModel>().shops = newShops;
          } else {
            final newBrands = context.read<SelectionModel>().brands;
            newBrands.remove(text);
            context.read<SelectionModel>().brands = newBrands;
          }
        }
      },
      child: Material(
        color: Colors.transparent,
        child: Column(
          children: [
            const Divider(
              thickness: 1,
              color: Colors.black,
            ),
            Row(
              children: [
                const SizedBox(
                  width: 15,
                ),
                Text(
                  text,
                  style: const TextStyle(
                    fontSize: 15,
                    fontWeight: FontWeight.w500,
                  ),
                ),
                const Spacer(),
                Checkbox(
                  value: isShops
                      ? context.watch<SelectionModel>().shops.contains(text)
                      : context.watch<SelectionModel>().brands.contains(text),
                  onChanged: (value) {
                    if (value!) {
                      if (isShops) {
                        final newShops = context.read<SelectionModel>().shops;
                        newShops.add(text);
                        context.read<SelectionModel>().shops = newShops;
                      } else {
                        final newBrands = context.read<SelectionModel>().brands;
                        newBrands.add(text);
                        context.read<SelectionModel>().brands = newBrands;
                      }
                    } else {
                      if (isShops) {
                        final newShops = context.read<SelectionModel>().shops;
                        newShops.remove(text);
                        context.read<SelectionModel>().shops = newShops;
                      } else {
                        final newBrands = context.read<SelectionModel>().brands;
                        newBrands.remove(text);
                        context.read<SelectionModel>().brands = newBrands;
                      }
                    }
                  },
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
