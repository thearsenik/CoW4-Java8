package tests;

import fr.ttfx.cow4.world.Cell;
import fr.ttfx.cow4.world.GameWorld;
import fr.ttfx.cow4.world.StaticGameWorld;
import org.junit.Assert;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.function.Function;

import static org.junit.Assert.*;

/**
 * Created by Arsenik on 27/08/15.
 */
public class PerformanceTests {

    private static final long MAX_ACCEPTABLE_TIME = 100L; // 200 ms
    private static GameWorld world = new StaticGameWorld();

    public static void main(String[] args) {
        printTime("AI top-left => potion", calculateAverageTime(0, 0, 4, 21));
        printTime("AI bottom-right => potion", calculateAverageTime(24, 24, 20, 3));

        printTime("AI top-left => milieu", calculateAverageTime(0, 0, 12, 12));
        printTime("AI bottom-right => milieu", calculateAverageTime(24, 24, 12, 12));

        printTime("AI top-left => bottom-right", calculateAverageTime(0, 0, 24, 24));
        printTime("AI bottom-right => top-left", calculateAverageTime(24, 24, 0, 0));

        printTime("AI top-left => milieu", calculateAverageTime(0, 0, 12, 12));
        printTime("AI bottom-right => milieu", calculateAverageTime(24, 24, 12, 12));

        printTime("AI top-left => milieu", calculateAverageTime(0, 0, 12, 12));
        printTime("AI bottom-right => milieu", calculateAverageTime(24, 24, 12, 12));


        long averageTime = 0;
        long min = 0;
        long max = 0;
        long nbProcess = 0;
        for (int i = 0; i < 25; i++) {
            for (int j = 0; j < 25; j++) {
                for (int k = 0; k < 25; k++) {
                    for (int l = 0; l < 25; l++) {
                        if ((i == k && j == l)
                                || (i == 0 && j == 14)
                                || (k == 0 && l == 14)
                                || (i == 24 && j == 10)
                                || (k == 24 && l == 10)) {
                            continue;
                        }
                        long time = calculateAverageTime(i, j, k, l);
                        nbProcess++;
                        averageTime += time;
                        printTime("(" + i + ", " + j + ") => (" + k + ", " + l + ")", time);
                        if (time < min) {
                            min = time;
                        }
                        if (time > max) {
                            max = time;
                        }
                    }
                }
            }
        }
        System.out.println("Min: " + (min / 1000000.) + "ms, Max: " + (max / 1000000.) + "ms " + (averageTime / nbProcess) / 1000000. + " ms");
    }

    private static void printTime(String prefix, long time) {
        double timeMs = time / 1000000.;
        if (timeMs > MAX_ACCEPTABLE_TIME) {
            System.err.println("WARNING " + prefix + ": " + timeMs + " ms");
        } else {
//            System.out.println(prefix + ": " + timeMs + " ms");
        }
    }

    private static long calculateAverageTime(int fromLine, int fromCol, int toLine, int toCol) {
        Cell from = world.getCell(fromLine, fromCol);
        Cell to = world.getCell(toLine, toCol);

//        long durations = 0L;
//        long i;
//        for (i = 0; i < 3; i++) {
            Instant start = Instant.now();
            List<Cell> path = world.getShortestPath(from, to);
            Instant end = Instant.now();

            printPath(path);

            long duration = Duration.between(start, end).toNanos();
//            durations += duration;
//        }
        return duration;
//        return durations / i;
    }

    private static void printPath(List<Cell> path) {
//        path.stream().forEachOrdered(cell -> System.out.print(cell.getId() + "->"));
//        System.out.print("\n");
    }
}