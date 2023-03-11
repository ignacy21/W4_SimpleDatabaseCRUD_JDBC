package projekt;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Miansda {

    public static void main(String[] args) {

        List<LocalDateTime> list = new ArrayList<>(List.of(
                LocalDateTime.of(2022, 2, 5, 7,1),
                LocalDateTime.of(2022, 12, 6, 7,1),
                LocalDateTime.of(2022, 5, 23, 7,1),
                LocalDateTime.of(2022, 5, 23, 6,1),
                LocalDateTime.of(2022, 5, 23, 6,10),
                LocalDateTime.of(2022, 7, 30, 6,10)
        ));
        Comparator<LocalDateTime> localDateTimeComparatorAsc = Comparator.<LocalDateTime>naturalOrder().reversed();
        list.sort(localDateTimeComparatorAsc);
        for (LocalDateTime localDateTime : list) {
            System.out.println(localDateTime);
        }


    }
}
