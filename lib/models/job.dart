// library jobs;

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

  // @HiveField(3)
  // DateTime dueDateTime;

  Job(this.name, this.repeat, this.unit);

  String toString() {
    return "$name $repeat $unit => key: $key";
  }

// Datetime.millisecondsSinceEpoch
  // String dueDate
}

// valuesBetween
