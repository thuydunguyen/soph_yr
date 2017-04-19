import org.junit.Test;

import java.util.Map;

public class AGRastererTest extends AGMapTest {
    /**
     * Test the rastering functionality of the student code, by calling their getMapRaster method
     * and ensuring that the resulting map is correct. All of the test data is stored in a
     * TestParameters object that is loaded by the AGMapTest constructor. Note that this test file
     * extends AGMapTest, where most of the interesting stuff happens.
     *
     * @throws Exception
     */
    @Test
    public void testGetMapRaster() throws Exception {
        for (TestParameters p : params) {
            System.out.println(p.rasterParams);
            Map<String, Object> studentRasterResult = rasterer.getMapRaster(p.rasterParams);
            String[][] imgs = (String[][]) studentRasterResult.get("render_grid");
            System.out.println("Actual: ");
            for (int x = 0; x < imgs.length; x++) {
                for (int y = 0; y < imgs[0].length; y++) {
                    System.out.print(imgs[x][y] + "  ");
                }
                System.out.println("");
            }
            System.out.println("Expected: ");
            String[][] r_imgs = (String[][]) p.rasterResult.get("render_grid");
            for (int x = 0; x < r_imgs.length; x++) {
                for (int y = 0; y < r_imgs[0].length; y++) {
                    System.out.print(r_imgs[x][y] + "  ");
                }
                System.out.println("");
            }

            checkParamsMap("Returned result differed for input: " + p.rasterParams + ".\n",
                    p.rasterResult, studentRasterResult);
        }
    }
}
