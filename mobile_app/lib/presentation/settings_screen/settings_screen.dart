import 'package:energy_drink/presentation/settings_screen/widgets/app_bar.dart';
import 'package:energy_drink/presentation/settings_screen/widgets/save_button.dart';
import 'package:energy_drink/presentation/settings_screen/widgets/sorting_direction_selector.dart';
import 'package:energy_drink/presentation/settings_screen/widgets/sorting_parameter_selector.dart';
import 'package:flutter/material.dart';

class SettingsScreen extends StatelessWidget {
  const SettingsScreen({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: const SettingsAppBar(),
      bottomSheet: const Padding(padding: EdgeInsets.zero),
      body: Stack(
        children: [
          SingleChildScrollView(
            child: Padding(
              padding: const EdgeInsets.symmetric(vertical: 8),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Padding(
                    padding: EdgeInsets.only(left: 20),
                    child: Text(
                      'Параметры сортировки:',
                      style: TextStyle(fontSize: 15),
                    ),
                  ),
                  Row(
                    children: const [
                      SizedBox(
                        height: 100,
                        width: 200,
                        child: SortingParameterSelector(),
                      ),
                      SortingDirectionSelector(),
                    ],
                  ),
                ],
              ),
            ),
          ),
          const SaveButton(),
        ],
      ),
    );
  }
}