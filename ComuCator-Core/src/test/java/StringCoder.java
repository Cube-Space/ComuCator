import net.cubespace.ComuCator.Util.StringCode;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.LinkedHashSet;
import java.util.UUID;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class StringCoder {
    @Test
    public void collisionTest() {
        LinkedHashSet<Long> known = new LinkedHashSet<>();
        LinkedHashSet<String> knownStrings = new LinkedHashSet<>();

        for(int i = 0; i < 1000000; i++) {
            String string;

            do {
                string = UUID.randomUUID().toString();
            } while(knownStrings.contains(string));

            knownStrings.add(string);

            Long code = StringCode.getStringCode(string);

            Assert.assertTrue(!known.contains(code), "Collision after Iteration: " + i + " String: " + string);
            known.add(code);
        }
    }
}
