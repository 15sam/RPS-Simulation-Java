public class secondlargest2ndsmallest {
    public static void main(String[] args) {
        int[] array={21,123,22,14,54,64,2,53,3};
        int min1 =array[0];
        int min2 =array[0];
        int max1 =array[0];
        int max2 =array[0];
        for (int num : array) {
            if (num < min1) {
                min2 = min1;  // Update min2 before changing min1
                min1 = num;
            } else if (num < min2 && num != min1) {
                min2 = num;  // Ensures min2 updates correctly when min1 doesn’t change
            }
        }

        System.out.println("2nd smallest "+min2);
    }
}
