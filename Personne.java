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








malia@BO-22-2016:~/Documents/Application_livraison_git$ git push 
Username for 'https://github.com': tchabanamalia
Password for 'https://tchabanamalia@github.com': 
remote: Support for password authentication was removed on August 13, 2021. Please use a personal access token instead.
remote: Please see https://github.blog/2020-12-15-token-authentication-requirements-for-git-operations/ for more information.
fatal: Authentication failed for 'https://github.com/tchabanamalia/Application_livraison.git/'
malia@BO-22-2016:~/Documents/Application_livraison_git$ git push 
Username for 'https://github.com': tchabanamalia
Password for 'https://tchabanamalia@github.com': 
Décompte des objets: 5, fait.
Delta compression using up to 4 threads.
Compression des objets: 100% (5/5), fait.
Écriture des objets: 100% (5/5), 20.74 KiB | 1.88 MiB/s, fait.
Total 5 (delta 0), reused 0 (delta 0)
To https://github.com/tchabanamalia/Application_livraison.git
   977e260..2877c96  main -> main
malia@BO-22-2016:~/Documents/Application_livraison_git$ git pools
git : 'pools' n'est pas une commande git. Voir 'git --help'.
malia@BO-22-2016:~/Documents/Application_livraison_git$ git pull
Déjà à jour.
malia@BO-22-2016:~/Documents/Application_livraison_git$ git branch malia
malia@BO-22-2016:~/Documents/Application_livraison_git$ git branch 
* main
  malia
malia@BO-22-2016:~/Documents/Application_livraison_git$ git checkout malia 
Basculement sur la branche 'malia'
malia@BO-22-2016:~/Documents/Application_livraison_git$ git branch 
  main
* malia
malia@BO-22-2016:~/Documents/Application_livraison_git$ 


























