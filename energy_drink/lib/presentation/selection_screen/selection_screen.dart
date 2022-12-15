import 'package:energy_drink/presentation/selection_screen/widgets/app_bar.dart';
import 'package:flutter/material.dart';

class SelectionScreen extends StatelessWidget {
  final bool isShops;
  const SelectionScreen(this.isShops, {Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: SelectionAppBar(isShops),
      body: Container(),
    );
  }
}
