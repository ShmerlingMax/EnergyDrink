import 'package:energy_drink/domain/models/aggregation_model/aggregation_model.dart';
import 'package:energy_drink/domain/models/settings_model/settings_model.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

class SortingDirectionSelector extends StatelessWidget {
  const SortingDirectionSelector({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Expanded(
      child: DropdownButton<SortingDirection>(
        isExpanded: true,
        value: context.watch<Settings>().sortingDirection,
        dropdownColor: const Color(0xFFB7D6FF),
        items: const [
          DropdownMenuItem(
            value: SortingDirection.ascending,
            child: Text('по возрастанию'),
          ),
          DropdownMenuItem(
            value: SortingDirection.descending,
            child: Text('по убыванию'),
          ),
        ],
        onChanged: (value) {
          context.read<Settings>().sortingDirection = value!;
        },
      ),
    );
  }
}
