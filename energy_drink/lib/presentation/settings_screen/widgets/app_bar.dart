import 'package:energy_drink/domain/models/aggregation_model/aggregation_model.dart';
import 'package:energy_drink/domain/models/settings_model/settings_model.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

class SettingsAppBar extends StatelessWidget with PreferredSizeWidget {
  const SettingsAppBar({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return SafeArea(
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          IconButton(
            onPressed: () {
              Navigator.pop(context);
              final aggregation = context.read<Aggregation>();
              context.read<Settings>().reset(
                    aggregation.shops.toList(),
                    aggregation.brands.toList(),
                    aggregation.sortingParameter,
                    aggregation.sortingDirection,
                  );
            },
            icon: const Icon(Icons.arrow_back),
          ),
          const Text(
            'Настройки',
            style: TextStyle(fontSize: 20),
          ),
          Container(
            height: 50,
            width: 110,
            color: const Color(0xFFB2C2D7),
            child: TextButton(
              onPressed: () {
                final aggregation = context.read<Aggregation>();
                context.read<Settings>().reset(
                      aggregation.allShops.toList(),
                      aggregation.allBrands.toList(),
                      SortingParameter.discount,
                      SortingDirection.descending,
                    );
              },
              child: const Text(
                'Сбросить',
                style: TextStyle(
                  fontSize: 15,
                  color: Colors.black,
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }

  @override
  Size get preferredSize => const Size.fromHeight(kToolbarHeight);
}
