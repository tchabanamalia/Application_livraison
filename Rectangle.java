public class Rectangle {
    int longueur;
    int largeur;
    int origine_x;
    int origine_y;
        
    Rectangle (int lon, int lar) {
        this.longueur = lon; 
        this.largeur = lar;
        this.origine_x = 0;
        this.origine_y = 0;   
    }
    
    void deplace(int x, int y) {
        this.origine_x = this.origine_x + x;
        this.origine_y = this.origine_y + y;
    } 
    
    int surface() {
        int surface = this.longueur * this.largeur;
        System.out.println(surface);
        return surface;
    }
          
    public static void main(String [] args) {
        Rectangle mon_rectangle = new Rectangle(15,5);
    }
        
}
