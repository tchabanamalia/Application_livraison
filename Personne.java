public class Personne {
    String nom;
    String prenom;
    int anneeNaissance;
    String etat;
    
    public Personne(String _nom, String _prenom, int _anneeNaissance) {
        nom = _nom;
        prenom = _prenom;
        anneeNaissance = _anneeNaissance;        
    }
    
    public int getAge() {
        int age = 2021 - this.anneeNaissance;
        return age;
    }   
    
}
