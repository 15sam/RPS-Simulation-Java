import java.util.HashSet;
import java.util.Iterator;

public class hashSet {
    public static void main(String args[]){
        HashSet<String> car = new HashSet<>();
        car.add("Porche");
        car.add("urus");
        car.add("bmw");
        car.add("maruti");
        System.out.println("Size of the set is : "+car.size());

        // search
        if (car.contains("bmw")){
            System.out.println("bmw is there!");
        }if (!car.contains("cadillac")){
            System.out.println("cadillac isnt there!");
        }

        //delete
        car.remove("bmw");
        if (!car.contains("bmw")){
            System.out.println("we deleted bmw");
        }


        //print all elements
        System.out.println(car);

        //ITERATION
        Iterator it = car.iterator();
        while(it.hasNext()){
            System.out.println(it.next());
        }



    }


}
