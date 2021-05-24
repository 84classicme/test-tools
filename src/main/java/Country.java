import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Country {
    String name;
    int population;
    String capital;
    String currency;
}
