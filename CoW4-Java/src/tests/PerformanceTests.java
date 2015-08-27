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

    private static final long MAX_ACCEPTABLE_TIME = 200L; // 200 ms
    private static GameWorld world = new StaticGameWorld();

    public static void main(String[] args) {
        printTime("IA top-left => potion", calculateAverageTime(0, 0, 4, 21));
        printTime("IA bottom-right => potion", calculateAverageTime(24, 24, 20, 3));

        printTime("IA top-left => milieu", calculateAverageTime(0, 0, 12, 12));
        printTime("IA bottom-right => milieu", calculateAverageTime(24, 24, 12, 12));

        printTime("IA top-left => bottom-right", calculateAverageTime(0, 0, 24, 24));
        printTime("IA bottom-right => top-left", calculateAverageTime(24, 24, 0, 0));

        printTime("IA top-left => milieu", calculateAverageTime(0, 0, 12, 12));
        printTime("IA bottom-right => milieu", calculateAverageTime(24, 24, 12, 12));

        printTime("IA top-left => milieu", calculateAverageTime(0, 0, 12, 12));
        printTime("IA bottom-right => milieu", calculateAverageTime(24, 24, 12, 12));
    }

    private static void printTime(String prefix, long time) {
        double timeMs = time / 1000000.;
        if (timeMs > MAX_ACCEPTABLE_TIME) {
            System.err.println("WARNING " + prefix + ": " + timeMs + " ms");
        } else {
            System.out.println(prefix + ": " + timeMs + " ms");
        }
    }

    private static long calculateAverageTime(int fromLine, int fromCol, int toLine, int toCol) {
        Cell from = world.getCell(fromLine, fromCol);
        Cell to = world.getCell(toLine, toCol);

        boolean stop = false;
        long durations = 0L;
        long i;
        for (i = 0; i < 3 && !stop; i++) {
            Instant start = Instant.now();
            List<Cell> path = world.getShortestPath(from, to);
            Instant end = Instant.now();

            printPath(path);

            long duration = Duration.between(start, end).toNanos();
            durations += duration;
        }

        return durations / i;
    }

    private static void printPath(List<Cell> path) {
        path.stream().forEachOrdered(cell -> System.out.print(cell.getId() + "->"));
        System.out.print("\n");
    }
}