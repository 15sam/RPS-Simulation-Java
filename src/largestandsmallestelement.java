import java.util.LinkedHashMap;
import java.util.Map;


public class largestandsmallestelement {

    public static void main (String []args) {
        int[] array = {2, 12, 42, 23, 12, 44, 53};
        int min = array[0];
        int max = array[0];
        for (int num : array) {
            if (num < min) {
                min = num;
            }
            if (num>max) {
                max = num;
            }
            }
        System.out.println("the min number is: " + min);
        System.out.println("the max number is: " + max);
        }


    }
