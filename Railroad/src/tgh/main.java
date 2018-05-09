/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tgh;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

/**
 *
 * @author SULO
 */
public class main {

    //strom zakořeněný ve městě 0 (rekurze)
    private final Graf g;
    // seznam spojení od města k městu
    private final ArrayList<Hrana> hrany;
    // DisjointSet struktura
    private final DisjointSet dis;
    private static final Scanner sc = new Scanner(System.in);

    private void setridlist() {
        hrany.sort(
                Comparator.comparingInt(Hrana::getMirazateze).thenComparingInt(Hrana::getCenauseku));
    }

    private int [] kruskaluj(int pocet) {
        // setřídí nejprve list dle ohodnocení hran (nejprve zátěž a poté cena) od nejmenší po největší
        setridlist();
        for (int i = 0; i < hrany.size(); i++) {
            Hrana h = hrany.get(i);
            // pokud je nalezeno n-1 spojení (minimální kostra grafu)
            if (pocet - 1 != g.hrany.size()) {
                // pokud nepatří ke stejné komponentě souvislosti, tak se přidá hrana
                // do výsledného grafu
                if (dis.findparent(h.getPrvni()) != dis.findparent(h.getDruhy())) {
                    dis.union(h.getPrvni(), h.getDruhy());
                    g.pridejVysledek(h);
                }
            }
        }
        // vrací výslednou kostru od města n-1
        return (g.vyslednakostra(pocet-1));
    }

    /**
     * @param pocet - total count of the cities
     */
    public main(int pocet) {
        hrany = new ArrayList<>();
        dis = new DisjointSet(pocet);
        g = new Graf();
    }

    private void pridejHrany(int od, int to, int cena, int mirazateze, int poradi) {
        hrany.add(new Hrana(od, to, cena, mirazateze, poradi));
    }

    public static void main(String[] args){
        
        if (sc.hasNextInt()) {
            int pocet = sc.nextInt();
            int pocetvariant = sc.nextInt();
            main krus = new main(pocet);
            for (int i = 0; i < pocetvariant; i++) {
                krus.pridejHrany(sc.nextInt(), sc.nextInt(), sc.nextInt(), sc.nextInt(), i);
            }
            krus.kruskaluj(pocet);
            int [] j =krus.kruskaluj(pocet);
        for(int i=0;i<j.length;i++)
        {
            System.out.println(j[i]);
        }
        }
    }
    // třída DisjointSet udržující informace o příslušnosti ke komponentě
    // souvislosti
    class DisjointSet {

        ArrayList<Vrchol> vrcholy;

        public Vrchol getVrcholonIndex(int index) {
            return vrcholy.get(index);
        }

        public DisjointSet(int pocet) {
            vrcholy = new ArrayList();

            for (int i = 0; i < pocet; i++) {

                makeset(i);

            }
        }
        // vytvoří set pro každý uzel, který je sám sobě rodičem

        private void makeset(int index) {
            Vrchol v = new Vrchol(index);
            this.vrcholy.add(v);
        }
        // najde rodiče pro zadaný vrchol a vrátí vrchol

        public Vrchol findparent(int parent) {

            Vrchol v = vrcholy.get(parent);
            if (v.parent == v.hodnota) {
                return v;
            } else {

                return findparent(v.parent);

            }

        }
        // spojí dva vrcholy, pokud nejsou ve stejné skupině

        public void union(int prvni, int druhy) {
            Vrchol par1 = findparent(vrcholy.get(prvni).hodnota);
            Vrchol par2 = findparent(vrcholy.get(druhy).hodnota);
            if (par1 != par2) {
                if (par1.rank >= par2.rank) {
                    if (par1.rank == par2.rank) {
                        par1.rank++;
                    }
                    par2.parent = par1.hodnota;
                } else {
                    par1.parent = par2.hodnota;
                }
            }
        }

    }
// třída reprezentující vrchol

    class Vrchol {

        private int rank;
        private int parent;
        private int hodnota;

        public Vrchol(int index) {
            this.rank = 0;
            this.parent = index;
            this.hodnota = index;
        }

    }

    class Hrana {

        public int getCenauseku() {
            return cenauseku;
        }

        public int getMirazateze() {
            return mirazateze;
        }

        public int getPrvni() {
            return prvni;
        }

        public int getDruhy() {
            return druhy;
        }

        public int getPoradi() {
            return poradi;
        }

        private final int prvni;
        private final int druhy;
        private final int cenauseku;
        private final int mirazateze;
        private final int poradi;

        public Hrana(int od, int to, int cenauseku, int mirazateze, int poradi) {

            this.prvni = od;
            this.druhy = to;
            this.cenauseku = cenauseku;
            this.mirazateze = mirazateze;
            this.poradi = poradi;
        }
    }

    class Graf {

        ArrayList<Hrana> hrany;
        public Graf() {
            hrany = new ArrayList();
            //poledeti = new ArrayList();
        }

        public void pridejVysledek(Hrana h) {
            hrany.add(h);
        }
        // vypíše výsledek výsledné kostry od dětí k předkům
        public int [] vyslednakostra(int nula) {
             int [] vysledek = new int[nula];
              for(int i=nula;i>0;i--)
              {
            final int j=i;
            Hrana h = hrany.stream().filter(o -> o.getDruhy() == j).findFirst().get();
           vysledek[i-1] =(h.getPoradi());
            nula--;
              }                                              
           return vysledek;     
            }
            
           

        }

    }

