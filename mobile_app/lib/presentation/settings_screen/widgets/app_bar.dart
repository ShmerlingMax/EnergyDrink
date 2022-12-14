import 'package:energy_drink/domain/models/aggregation_model/aggregation_model.dart';
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
                context.read<Aggregation>().reset();
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
