import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by miniwolf on 15-01-2017.
 */
public class OptimizedForLoop {
    public static void main(String[] args) {
        List<Integer> strings = new ArrayList<>();
        for (int i = 0; i < 10000000; i++) {
            strings.add(i);
        }
        int count = 0;
        long init = System.nanoTime();
        for (int i = 0; i < strings.size(); i++) {
            count += strings.get(i);
        }
        System.out.println(System.nanoTime() - init);

        count = 0;
        long init2 = System.nanoTime();
        for (int string : strings) {
            count += string;
        }
        System.out.println(System.nanoTime() - init2);
    }
}
