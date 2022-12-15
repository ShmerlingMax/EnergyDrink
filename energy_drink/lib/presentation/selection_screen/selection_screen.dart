import 'package:energy_drink/domain/models/aggregation_model/aggregation_model.dart';
import 'package:energy_drink/domain/models/selection_model/selection_model.dart';
import 'package:energy_drink/presentation/selection_screen/widgets/app_bar.dart';
import 'package:energy_drink/presentation/selection_screen/widgets/item.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

class SelectionScreen extends StatelessWidget {
  final bool isShops;
  const SelectionScreen(this.isShops, {Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: SelectionAppBar(isShops),
      body: Stack(
        children: [
          Center(
            child: Padding(
              padding: const EdgeInsets.symmetric(horizontal: 34),
              child: ListView.builder(
                itemCount: (isShops
                        ? context.read<Aggregation>().allShops.length
                        : context.read<Aggregation>().allBrands.length) +
                    1,
                itemBuilder: (context, index) {
                  if (index == 0) {
                    return SelectAll(isShops);
                  }
                  return SelectionItem(
                    isShops,
                    isShops
                        ? context.read<Aggregation>().allShops[index - 1]
                        : context.read<Aggregation>().allBrands[index - 1],
                  );
                },
              ),
            ),
          )
        ],
      ),
    );
  }
}

class SelectAll extends StatelessWidget {
  final bool isShops;
  const SelectAll(this.isShops, {Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: () {
        final value = isShops
            ? context.read<SelectionModel>().shops.length ==
                context.read<Aggregation>().allShops.length
            : context.read<SelectionModel>().brands.length ==
                context.read<Aggregation>().allBrands.length;
        if (!value) {
          if (isShops) {
            final newShops = context.read<Aggregation>().allShops.toList();
            context.read<SelectionModel>().shops = newShops;
          } else {
            final newBrands = context.read<Aggregation>().allBrands.toList();
            context.read<SelectionModel>().brands = newBrands;
          }
        } else {
          if (isShops) {
            context.read<SelectionModel>().shops = [];
          } else {
            context.read<SelectionModel>().brands = [];
          }
        }
      },
      child: Material(
        color: Colors.transparent,
        child: Row(
          children: [
            const SizedBox(
              width: 15,
            ),
            const Text(
              'Выбрать всё',
              style: TextStyle(fontSize: 15),
            ),
            const Spacer(),
            Checkbox(
              value: isShops
                  ? context.watch<SelectionModel>().shops.length ==
                      context.watch<Aggregation>().allShops.length
                  : context.watch<SelectionModel>().brands.length ==
                      context.watch<Aggregation>().allBrands.length,
              onChanged: (value) {
                if (value!) {
                  if (isShops) {
                    final newShops =
                        context.read<Aggregation>().allShops.toList();
                    context.read<SelectionModel>().shops = newShops;
                  } else {
                    final newBrands =
                        context.read<Aggregation>().allBrands.toList();
                    context.read<SelectionModel>().brands = newBrands;
                  }
                } else {
                  if (isShops) {
                    context.read<SelectionModel>().shops = [];
                  } else {
                    context.read<SelectionModel>().brands = [];
                  }
                }
              },
            ),
          ],
        ),
      ),
    );
  }
}