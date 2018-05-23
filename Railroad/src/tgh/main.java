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
    private final ArrayList<Edge> hrany;
    // DisjointSet struktura
    private final DisjointSet dis;
    private static final Scanner sc = new Scanner(System.in);

    private void setridlist() {
        hrany.sort(
                Comparator.comparingInt(Edge::getMirazateze).thenComparingInt(Edge::getCenauseku));
    }

    private int[] kruskaluj(int pocet) {
        // setřídí nejprve list dle ohodnocení hran (nejprve zátěž a poté cena) od nejmenší po největší
        setridlist();
        //pocet pridanych hran
        int count = 0;
        for (int i = 0; i < hrany.size(); i++) {

            Edge h = hrany.get(i);
            // pokud je nalezeno n-1 spojení (minimální kostra grafu)
            if (count != pocet - 1) {
                // pokud nepatří ke stejné komponentě souvislosti, tak se přidá hrana
                // do výsledného grafu
                if (dis.findparent(h.getPrvni()) != dis.findparent(h.getDruhy())) {
                    dis.union(h.getPrvni(), h.getDruhy());
                    count++;
                    g.pridejVysledek(h);
                }
            } else {
                // nalezeno n-1 vrcholu, je třeba přerušit
                break;
            }

        }
        g.kostramake(pocet - 1);
        return g.vyslednakostra(pocet - 1);

    }

    /**
     * @param pocet - total count of the cities
     */
    public main(int pocet) {
        hrany = new ArrayList<>();
        dis = new DisjointSet(pocet);
        g = new Graf(pocet - 1);
    }

    private void pridejHrany(int od, int to, int cena, int mirazateze, int poradi) {
        hrany.add(new Edge(od, to, cena, mirazateze, poradi));
    }

    public static void main(String[] args) {

        if (sc.hasNextInt()) {
            int pocet = sc.nextInt();
            int pocetvariant = sc.nextInt();
            main krus = new main(pocet);
            for (int i = 0; i < pocetvariant; i++) {
                krus.pridejHrany(sc.nextInt(), sc.nextInt(), sc.nextInt(), sc.nextInt(), i);
            }
            //krus.kruskaluj(pocet);
            int[] j = krus.kruskaluj(pocet);
            for (int i = 0; i < j.length; i++) {
                System.out.println(j[i]);
            }
        }
    }
}

// třída DisjointSet udržující informace o příslušnosti ke komponentě
// souvislosti
class DisjointSet {

    ArrayList<Node> vrcholy;

    public Node getVrcholonIndex(int index) {
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
        Node v = new Node(index);
        this.vrcholy.add(v);
    }
    // najde rodiče pro zadaný vrchol a vrátí vrchol

    public Node findparent(int parent) {

        Node v = vrcholy.get(parent);
        if (v.getParent() == v.getHodnota()) {
            return v;
        } else {

            return findparent(v.getParent());

        }

    }
    // spojí dva nodes, pokud nejsou ve stejné skupině

    public void union(int prvni, int druhy) {
        Node par1 = findparent(vrcholy.get(prvni).getHodnota());
        Node par2 = findparent(vrcholy.get(druhy).getHodnota());
        if (par1 != par2) {
            if (par1.getRank() >= par2.getRank()) {
                if (par1.getRank() == par2.getRank()) {
                    par1.setRank(par1.getRank() + 1);
                }
                par2.setParent(par1.getHodnota());
            } else {
                par1.setParent(par2.getHodnota());
            }
        }
    }

}
// třída reprezentující vrchol

class Node {

    private int rank;
    private int parent;
    private int hodnota;

    public Node(int index) {
        this.rank = 0;
        this.parent = index;
        this.hodnota = index;

    }

    public int getRank() {
        return rank;
    }

    public int getParent() {
        return parent;
    }

    public int getHodnota() {
        return hodnota;
    }

    public void setforGraph(int parent, int rank) {
        this.parent = parent;
        this.rank = rank;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public void setHodnota(int hodnota) {
        this.hodnota = hodnota;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

}

class Edge {

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

    public Edge(int od, int to, int cenauseku, int mirazateze, int poradi) {

        this.prvni = od;
        this.druhy = to;
        this.cenauseku = cenauseku;
        this.mirazateze = mirazateze;
        this.poradi = poradi;
    }
}

class Graf {

    // vrcholy se seznamem předků
    ArrayList<Node> nodelist;
    ArrayList<Edge> edges;

    //ArrayList<Node> nodes;
    public Graf(int pocet) {
        // uložení hran pro práci s výsledkem
        edges = new ArrayList();
        nodelist = new ArrayList();
        // přidá se kořen hned na začátku
        nodelist.add(new Node(0));

    }

    private void removeedge(Edge e) {
        edges.remove(e);
    }

    private void mergealg(int hodnota, int parent, int rank) {
        Node k = new Node(hodnota);
        k.setforGraph(parent, rank);
        nodelist.add(k);
    }

    // vytvoření kostry - parent = rodic, rank = poradi, hodnota = cislo vrcholu
    public void kostramake(int pocet) {
        int akt = 0;
        for (int i = 0; i < pocet;) {
            final int uzelhodnota = nodelist.get(akt).getHodnota();
            Edge hranka = null;
            if (edges.stream().filter(o -> o.getPrvni() == uzelhodnota || o.getDruhy() == uzelhodnota).findFirst().isPresent()) {
                if (edges.stream().filter(o -> o.getPrvni() == uzelhodnota).findFirst().isPresent()) {
                    hranka = edges.stream().filter(o -> o.getPrvni() == uzelhodnota).findFirst().get();
                    mergealg(hranka.getDruhy(), hranka.getPrvni(), hranka.getPoradi());
                } else {
                    hranka = edges.stream().filter(o -> o.getDruhy() == uzelhodnota).findFirst().get();
                    mergealg(hranka.getPrvni(), hranka.getDruhy(), hranka.getPoradi());
                }
                i++;
                edges.remove(hranka);
            } else {
                akt++;
            }

        }
    }

    // vypíše výsledek výsledné kostry od dětí k předkům
    public int[] vyslednakostra(int nula) {
        int[] vysledek = new int[nula];
        for (int i = nula; i > 0; i--) {
            final int tento = i;
            vysledek[i - 1] = nodelist.stream().filter(o -> o.getHodnota() == tento).findFirst().get().getRank();
                    
        }

        return vysledek;
    }

    void pridejVysledek(Edge h) {
        edges.add(h);
    }

}
