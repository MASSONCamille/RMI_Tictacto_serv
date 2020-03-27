package RMI;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class TictactoServ extends UnicastRemoteObject implements RMIInterface {
    private String test;

    private Grille grille;
    private int joueur;

    protected TictactoServ() throws RemoteException {
        super();
    }

    public static void main(String[] args){
        try {

            int port = 8000;
            LocateRegistry.createRegistry(port);
            Naming.rebind("rmi://localhost:" + port +"/tictacto", new TictactoServ());
            System.out.println("Server ready");
        }catch (Exception e) {
            System.err.println("Server exception : " + e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public int getPlayer() throws RemoteException {
        return this.joueur;
    }

    @Override
    public String ReStart() throws RemoteException {
        this.grille = new Grille();
        this.joueur = 1;
        return this.grille.toString();
    }

    @Override
    public boolean Play(int x, int y) throws RemoteException {
        int pos = this.grille.search(x,y);
        if (this.grille.getTable().get(pos).isEmpty()){
            if (joueur==1) {
                this.grille.getTable().get(pos).setVal(1);
                joueur = 2;
            }else{
                this.grille.getTable().get(pos).setVal(2);
                joueur = 1;
            }
            return true;
        }else return false;
    }

    @Override
    public boolean Play(int pos) throws RemoteException {
        pos--;
        if (this.grille.getTable().get(pos).isEmpty()){

            if (joueur==1) this.grille.getTable().get(pos).setVal(1);
            else this.grille.getTable().get(pos).setVal(2);

            return true;
        }else return false;
    }

    @Override
    public boolean TestGagne(int pos) throws RemoteException {
        pos--;
        // variable de resultat: 0 = pas de gagnant; 1 = joueur actif gagne

        ArrayList<Case> colone = this.grille.getColone(pos%3);
        System.out.println("Colone: " + colone.toString()); // test en console

        ArrayList<Case> ligne = this.grille.getLigne(pos/3);
        System.out.println("Ligne: " + ligne.toString()); // test en console

        ArrayList<ArrayList<Case>> diagos = this.grille.getDiago(pos);
        if (diagos != null) {
            System.out.println("Diago 1: " + diagos.get(0)); // test en console
            if (diagos.size() > 1) System.out.println("Diago 2: " + diagos.get(1)); // test en console
        }

        boolean gagne = true;
        for (Case c: colone) {
            gagne = gagne && (c.getVal() == joueur);
        }
        if (gagne) return true;

        gagne = true;
        for (Case c: ligne) {
            gagne = gagne && (c.getVal() == joueur);
        }
        if (gagne) return true;

        if (diagos != null){
            for (ArrayList<Case> diago: diagos) {
                gagne = true;
                for (Case c: diago) {
                    gagne = gagne && (c.getVal() == joueur);
                }
                if (gagne) return true;
            }
        }
        return false;
    }

    @Override
    public boolean TestEgal() throws RemoteException {
        boolean res = true;
        for (Case c: grille.getTable()) res = res && !c.isEmpty();
        return res;
    }

    @Override
    public void NextPlayer() throws RemoteException {
        if (joueur == 1) joueur = 2;
        else joueur = 1;
    }

    @Override
    public String ImprGrille() throws RemoteException {
        return grille.toString();
    }
}
