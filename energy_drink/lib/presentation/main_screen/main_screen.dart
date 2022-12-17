import 'package:energy_drink/data/api/api.dart';
import 'package:energy_drink/domain/models/aggregation_model/aggregation_model.dart';
import 'package:energy_drink/domain/models/settings_model/settings_model.dart';
import 'package:energy_drink/domain/models/shop_model/shop_model.dart';
import 'package:energy_drink/presentation/main_screen/widgets/item.dart';
import 'package:flutter/material.dart';
import 'package:flutter_layout_grid/flutter_layout_grid.dart';
import 'package:provider/provider.dart';

import 'widgets/app_bar.dart';

class MainScreen extends StatefulWidget {
  const MainScreen({Key? key}) : super(key: key);

  @override
  State<MainScreen> createState() => _MainScreenState();
}

class _MainScreenState extends State<MainScreen> {
  late Future<List<Shop>> shops;
  late Future<List<String>> brands;
  @override
  void initState() {
    shops = Api().getMockShops();
    brands = Api().getMockBrands();
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: () => FocusManager.instance.primaryFocus?.unfocus(),
      child: Scaffold(
        appBar: const MainAppBar(),
        body: SafeArea(
          child: FutureBuilder(
            future: Future.wait([shops, brands]),
            builder: (BuildContext context, AsyncSnapshot<dynamic> snapshot) {
              if (snapshot.hasData) {
                if (context.read<Aggregation>().isNotInit) {
                  final List<String> shopsStr = [];
                  for (int i = 0; i < snapshot.data[0].length; i++) {
                    shopsStr.add(snapshot.data[0][i].name);
                  }
                  context
                      .read<Aggregation>()
                      .init(shopsStr.toList(), snapshot.data[1].toList());
                  context
                      .read<Settings>()
                      .init(shopsStr.toList(), snapshot.data[1].toList());
                }
                final drinks =
                    context.watch<Aggregation>().sort(snapshot.data[0]);
                if (drinks.isEmpty) {
                  return const Center(
                    child: Text('По вашему запросу ничего не найдено'),
                  );
                }
                return SingleChildScrollView(
                  child: LayoutGrid(
                    columnSizes: [1.fr, 1.fr],
                    rowSizes: [
                      for (var i = 0; i < drinks.length / 2; i++) auto
                    ],
                    children: [
                      for (var i = 0; i < drinks.length; i++)
                        Item(drinks[i], i),
                    ],
                  ),
                );
                // final itemWidth = MediaQuery.of(context).size.width / 3;
                // final itemHeight = MediaQuery.of(context).size.height / 4;
                // return GridView.builder(
                //   shrinkWrap: true,
                //   itemCount: drinks.length,
                //   gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
                //     crossAxisCount: 2,
                //     childAspectRatio: itemWidth / itemHeight,
                //   ),
                //   itemBuilder: (context, index) => Item(
                //     drinks[index],
                //     index,
                //   ),
                // );
              } else {
                return const Center(
                  child: CircularProgressIndicator(),
                );
              }
            },
          ),
        ),
      ),
    );
  }
}
