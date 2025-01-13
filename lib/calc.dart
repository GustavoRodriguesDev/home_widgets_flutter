class Math {
  void dda(num x1, num x2, num y1, num y2) {
    num deltaY = y2 - y1;
    num deltaX = x2 - x1;
    num m = deltaY / deltaX;
    num n = y1 - m * x1;

    print(m);
    print(n);
    logCartesianPlane(x1: x1, y1: y1, x2: x2, y2: y2, m: m, n: n);
  }

  void logCartesianPlane(
      {required num x1,
      required num y1,
      required num x2,
      required num y2,
      required num m,
      required num n}) {
    int width = 100;
    int height = 100;
    List<List<String>> grid = List.generate(
      height,
      (y) => List.generate(width, (x) => '.', growable: false),
      growable: false,
    );

    for (num x = x1; x <= x2; x++) {
      num y = (m * x + n).round();
      if (x >= 0 && x < width && y >= 0 && y < height) {
        grid[height - 1 - y.toInt()][x.toInt()] = 'x';
      }
    }

    for (var row in grid) {
      print(row.join(' '));
    }
  }
}

void main(List<String> args) {
  Math math = Math();
  math.dda(3, 7, 3, 8);
}
