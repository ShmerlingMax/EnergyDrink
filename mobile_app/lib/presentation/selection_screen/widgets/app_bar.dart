import 'package:energy_drink/domain/models/selection_model/selection_model.dart';
import 'package:energy_drink/domain/models/settings_model/settings_model.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

class SelectionAppBar extends StatelessWidget with PreferredSizeWidget {
  final bool isShops;
  const SelectionAppBar(this.isShops, {Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return SafeArea(
      child: Row(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          IconButton(
            onPressed: () {
              final settings = context.read<Settings>();
              if (isShops) {
                context.read<SelectionModel>().shops = settings.shops.toList();
              } else {
                context.read<SelectionModel>().brands =
                    settings.brands.toList();
              }
              Navigator.pop(context);
            },
            icon: const Icon(Icons.arrow_back),
          ),
          const Spacer(
            flex: 7,
          ),
          Text(
            isShops ? 'Магазины' : 'Бренды',
            style: const TextStyle(
              fontSize: 20,
              fontWeight: FontWeight.w500,
            ),
          ),
          const Spacer(
            flex: 9,
          ),
        ],
      ),
    );
  }

  @override
  Size get preferredSize => const Size.fromHeight(kToolbarHeight);
}
