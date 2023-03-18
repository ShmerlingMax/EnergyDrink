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
        key: const Key('sorting_direction_button'),
        isExpanded: true,
        value: context.watch<Settings>().sortingDirection,
        dropdownColor: const Color(0xFFB7D6FF),
        items: const [
          DropdownMenuItem(
            key: Key('ascending_button'),
            value: SortingDirection.ascending,
            child: Text(
              'по возрастанию',
              style: TextStyle(
                fontSize: 15,
                fontWeight: FontWeight.w500,
              ),
            ),
          ),
          DropdownMenuItem(
            key: Key('descending_button'),
            value: SortingDirection.descending,
            child: Text(
              'по убыванию',
              style: TextStyle(
                fontSize: 15,
                fontWeight: FontWeight.w500,
              ),
            ),
          ),
        ],
        onChanged: (value) {
          context.read<Settings>().sortingDirection = value!;
        },
      ),
    );
  }
}
