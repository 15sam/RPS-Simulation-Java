class keyboard{
    private int keys=100;
    private String color="blue";

    public void pvttonormal(int keys, String color){
        this.keys= keys;
        this.color =color;
    }

    public void pressed(){
        System.out.println("key pressed "+ color);
    }
    public void throwit(){
        System.out.println("throwing"+keys);
    }
    public int getkeys(){
        return keys;
    }
    public String getcolor(){
        return color;
    }
}


class advkeyboard extends keyboard{


    public void push(){
        System.out.println("key pussed" );
    }
}

class demo {

    public static void main(String[] args) {

        advkeyboard obj = new advkeyboard();  //construstor
        obj.pressed();
        obj.throwit();
        obj.push();
        System.out.println(obj.getkeys()+obj.getcolor());

    }
}
