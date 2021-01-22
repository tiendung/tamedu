https://docs.hivedb.dev/

```Dart
var filteredUsers = userBox.values.where((user) => user.name.startsWith('s'));

```

https://docs.hivedb.dev/#/basics/auto_increment

Datetime.millisecondsSinceEpoch

```Dart
  var friends = await Hive.openBox('friends');
  friends.clear();

  friends.add('Lisa');            // index 0, key 0
  friends.add('Dave');            // index 1, key 1
  friends.put(123, 'Marco');      // index 2, key 123
  friends.add('Paul');            // index 3, key 12
 
  print(friends.getAt(0));
  print(friends.get(0));
  
  print(friends.getAt(1));
  print(friends.get(1));
  
  print(friends.getAt(2));
  print(friends.get(123));
  
  print(friends.getAt(3));
  print(friends.get(124));
 ```