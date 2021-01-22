import 'package:hive/hive.dart';
part 'job.g.dart';

@HiveType(typeId: 0)
class Job extends HiveObject {
  @HiveField(0)
  String name;

  @HiveField(1)
  int repeat;

  @HiveField(2)
  String unit;

  @HiveField(3)
  DateTime dueDateTime;

  Job(this.name, this.repeat, this.unit, this.dueDateTime);
  String toString() {
    return "$name $repeat $unit, ${dueDateTime} => key: $key";
  }

  static const String BOX_NAME = "jobs";
  static Box x;

  static getAllForDate(DateTime date) {
    return x.values.where((j) =>
        j.dueDateTime.year == date.year &&
        j.dueDateTime.month == date.month &&
        j.dueDateTime.day == date.day);
  }
}
