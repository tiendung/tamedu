import 'package:hive/hive.dart';
part 'habit.g.dart';

@HiveType(typeId: 1)
class Habit extends HiveObject {
  @HiveField(0)
  String name;
  @HiveField(1)
  int repeat;
  @HiveField(2)
  String unit;

  Habit(this.name, this.repeat, this.unit);

  String toString() {
    return "$name ";
  }

  static const BOX_NAME = "habits";
  static Box x;
}
