import 'package:energy_drink/domain/models/settings_model/settings_model.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:energy_drink/domain/models/aggregation_model/aggregation_model.dart';

class SortingParameterSelector extends StatelessWidget {
  const SortingParameterSelector({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Flexible(
          child: GestureDetector(
            onTap: () => context.read<Settings>().sortingParameter =
                SortingParameter.price,
            child: ListTile(
              contentPadding: EdgeInsets.zero,
              title: const Text('цена'),
              leading: Radio<SortingParameter>(
                value: SortingParameter.price,
                groupValue: context.watch<Settings>().sortingParameter,
                onChanged: (SortingParameter? value) {
                  context.read<Settings>().sortingParameter = value!;
                },
              ),
            ),
          ),
        ),
        Flexible(
          child: GestureDetector(
            onTap: () => context.read<Settings>().sortingParameter =
                SortingParameter.discount,
            child: ListTile(
              contentPadding: EdgeInsets.zero,
              title: const Text('скидка'),
              leading: Radio<SortingParameter>(
                value: SortingParameter.discount,
                groupValue: context.watch<Settings>().sortingParameter,
                onChanged: (SortingParameter? value) {
                  context.read<Settings>().sortingParameter = value!;
                },
              ),
            ),
          ),
        ),
      ],
    );
  }
}
