import 'package:flutter_riverpod/legacy.dart';

enum AppScreen { home, speedTest, share, settings }

final currentScreenProvider = StateProvider<AppScreen>((ref) => AppScreen.home);
