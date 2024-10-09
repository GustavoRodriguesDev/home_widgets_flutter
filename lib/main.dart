import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:home_widget/home_widget.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await HomeWidget.registerInteractivityCallback(interactiveCallback);
  runApp(const MyApp());
}

@pragma('vm:entry-point')
Future<void> interactiveCallback(Uri? uri) async {
  // We check the host of the uri to determine which action should be triggered.
  if (uri?.host == 'checkitem') {
    final index = int.tryParse(
          uri!.pathSegments.first.replaceAll('[', '').replaceAll(']', ''),
        ) ??
        -1;

    await checkItem(index, true);
  }
}

// Future<int> get _value async {
//   final value = await HomeWidget.getWidgetData<int>(_countKey, defaultValue: 0);
//   return value!;
// }

// Future<int> _increment() async {
//   final oldValue = await _value;
//   final newValue = oldValue + 1;
//   await _sendAndUpdate(newValue);
//   return newValue;
// }

const _countKey = 'task';

Future<List<ItemTask>> fetchItensTasks() async {
  try {
    final value = await HomeWidget.getWidgetData<String>(
      _countKey,
    );

    if (value == null) return [];
    final newValue = jsonDecode(value);
    final itens = newValue
        .map<ItemTask>(
          (e) => ItemTask(
            isChecked: e['checked'],
            name: e['name'],
          ),
        )
        .toList();
    return itens;
  } catch (e) {
    rethrow;
  }
}

Future<void> checkItem(int index, bool isChecked) async {
  List<ItemTask> itemList = await fetchItensTasks();
  ItemTask item = itemList[index];
  item = ItemTask(name: item.name, isChecked: isChecked);
  itemList.removeAt(index);
  itemList.insert(index, item);
  itemList = [...itemList];
  sendItensTasks(itemList);
}

Future<void> sendItensTasks(List<ItemTask> itens) async {
  await _sendAndUpdate(
    itens
        .map(
          (e) => {
            'checked': e.isChecked,
            'name': e.name,
          },
        )
        .toList(),
  );
}

/// Saves that new value

/// Stores [value] in the Widget Configuration
Future<void> _sendAndUpdate(List<Map<String, dynamic>> item) async {
  await HomeWidget.saveWidgetData(_countKey, jsonEncode(item));
  await TaskController.instance.getItens();

  await HomeWidget.updateWidget(
    androidName: 'ListviewGlanceWidgetReceiver',
  );
}

class TaskController extends ValueNotifier<List<ItemTask>> {
  TaskController._() : super([]);
  static final TaskController instance = TaskController._();

  Future<void> addItem(ItemTask itemTask) async {
    final itemList = await fetchItensTasks();

    itemList.add(itemTask);
    value = [...value, itemTask];

    sendItensTasks(value);
  }

  Future<void> getItens() async {
    final itemList = await fetchItensTasks();
    value = itemList;
  }

  Future<void> checkItems(int index, bool chcked) async {
    await checkItem(index, chcked);
    await getItens();
  }
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
        useMaterial3: true,
      ),
      home: const MyHomePage(title: 'Flutter Demo Home Page'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key, required this.title});

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> with WidgetsBindingObserver {
  final controller = TaskController.instance;
  @override
  void initState() {
    super.initState();
    controller.getItens();
    WidgetsBinding.instance.addObserver(this);
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text(widget.title)),
      body: Center(
        child: ValueListenableBuilder(
            valueListenable: controller,
            builder: (context, value, _) {
              return ListView.builder(
                itemCount: value.length,
                itemBuilder: (context, index) {
                  final item = value[index];
                  return Row(
                    children: [
                      Checkbox(
                        value: item.isChecked,
                        onChanged: (value) {
                          if (value != null) {
                            setState(() {
                              controller.checkItems(index, value);
                            });
                          }
                        },
                      ),
                      const SizedBox(width: 4),
                      Text(item.name)
                    ],
                  );
                },
              );
            }),
      ),
      floatingActionButton: Align(
        alignment: Alignment.bottomRight,
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            FloatingActionButton(
              onPressed: () async {
                controller.getItens();
              },
              tooltip: 'Increment',
              child: const Text("Clear"),
            ),
            const SizedBox(height: 8),
            FloatingActionButton(
              onPressed: () async {
                showModalBottomSheet(
                  context: context,
                  constraints: const BoxConstraints(minHeight: 300),
                  builder: (context) {
                    final TextEditingController textEditingController =
                        TextEditingController();
                    return Column(
                      children: [
                        const Text('Add Item'),
                        TextField(controller: textEditingController),
                        ElevatedButton(
                          onPressed: () {
                            controller.addItem(
                              ItemTask(
                                isChecked: false,
                                name: textEditingController.text,
                              ),
                            );
                            Navigator.of(context).pop();
                            textEditingController.clear();
                          },
                          child: const Text(
                            'Add',
                          ),
                        )
                      ],
                    );
                  },
                );
              },
              tooltip: 'Increment',
              child: const Icon(Icons.add),
            ),
          ],
        ),
      ),
    );
  }
}

class ItemTask {
  final bool isChecked;
  final String name;

  ItemTask({
    required this.isChecked,
    required this.name,
  });
}
