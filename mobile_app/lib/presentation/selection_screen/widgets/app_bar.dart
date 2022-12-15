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
              Navigator.pop(context);
            },
            icon: const Icon(Icons.arrow_back),
          ),
          const Spacer(
            flex: 7,
          ),
          Text(
            isShops ? 'Магазины' : 'Бренды',
            style: const TextStyle(fontSize: 20),
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
