package AbstrHeap;

import abstrTable.AbstrLIFO;
import enumTypy.eTypProhl;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class AbstrHeap<K> implements IAbstrHeap<K> {

    private static class Uzel<K> {
        K prvek;

        public Uzel(K prvek) {
            this.prvek = prvek;
        }
    }

    private Uzel<K>[] pole;
    private int kapacita;
    private int aktualniVelikost;
    private Comparator<K> komparator;

    private void vymen(Uzel<K>[] pole, int a, int b) {
        Uzel<K> temp = pole[a];
        pole[a] = pole[b];
        pole[b] = temp;
    }

    public AbstrHeap(int kapacita, Comparator<K> komparator) {
        this.pole = new Uzel[kapacita];
        this.kapacita = kapacita;
        this.komparator = komparator;
        this.aktualniVelikost = 0;
    }

    @Override
    public void vybuduj(K[] pole) {
        for (K prvek : pole) {
            vloz(prvek);
        }
    }
    @Override
    public void reorganizace() {
        for (int i = aktualniVelikost / 2; i >= 0; i--) {
            sestup(i);
        }
    }

    private void sestup(int index) {
        int levy = levyPotomek(index);
        int pravy = pravyPotomek(index);
        int nejvetsi = index;

        if (levy < aktualniVelikost && komparator.compare
                (pole[levy].prvek, pole[nejvetsi].prvek) > 0) {
            nejvetsi = levy;
        }

        if (pravy < aktualniVelikost && komparator.compare
                (pole[pravy].prvek, pole[nejvetsi].prvek) > 0) {
            nejvetsi = pravy;
        }

        if (nejvetsi != index) {
            vymen(pole, index, nejvetsi);
            sestup(nejvetsi);
        }
    }

    @Override
    public void zrus() {
        aktualniVelikost = 0;
    }


    @Override
    public boolean jePrazdny() {
        return aktualniVelikost == 0;
    }

    @Override
    public void vloz(K prvek) {
        if (aktualniVelikost == kapacita) {
            rozsir();
        }
        pole[aktualniVelikost] = new Uzel<>(prvek);
        aktualniVelikost++;
        vystup(aktualniVelikost - 1);
    }

    private void vystup(int i) {
        while (i > 0 && komparator.compare(pole[i].prvek, pole[rodic(i)].prvek) > 0) {
            vymen(pole, i, rodic(i));
            i = rodic(i);
        }
    }

    private void rozsir() {
        Uzel<K>[] novePole = new Uzel[kapacita * 2];
        System.arraycopy(pole, 0, novePole, 0, aktualniVelikost);
        pole = novePole;
        kapacita = novePole.length;
    }
    @Override
    public K odeberMax() {
        if (jePrazdny()) {
            throw new NoSuchElementException("Heap je prazdny!!!!");
        }
        K maxPrvek = pole[0].prvek;
        pole[0] = pole[aktualniVelikost - 1];
        aktualniVelikost--;
        sestup(0);
        return maxPrvek;
    }

    @Override
    public K zpristupniMax() {
        if (jePrazdny()) {
            throw new NoSuchElementException("Heap je prazdny");
        }
        return pole[0].prvek;
    }


    @Override
    public Iterator<K> vypis(eTypProhl typ) {
        switch (typ) {
            case SIRKA -> {
                return new Iterator<>() {
                    int i = 0;

                    @Override
                    public boolean hasNext() {
                        return i < aktualniVelikost;
                    }

                    @Override
                    public K next() {
                        if (!hasNext()) throw new NoSuchElementException();
                        return pole[i++].prvek;
                    }
                };
            }


            case HLOUBKA -> {
                return new Iterator<>() {
                    final AbstrLIFO<Integer> lifo = new AbstrLIFO<>();
                    boolean initialized = false;

                    @Override
                    public boolean hasNext() {
                        if (!initialized) {
                            initialize();
                            initialized = true;
                        }
                        return !lifo.jePrazdny();
                    }

                    private void initialize() {
                        int index = 0;
                        while (index < aktualniVelikost) {
                            lifo.vloz(index);
                            index = levyPotomek(index);
                        }
                    }

                    @Override
                    public K next() {
                        if (!hasNext()) {
                            throw new NoSuchElementException();
                        }

                        int index = lifo.odeber();
                        K result = pole[index].prvek;

                        int rightIndex = pravyPotomek(index);
                        if (rightIndex < aktualniVelikost) {
                            int nextIndex = rightIndex;
                            while (nextIndex < aktualniVelikost) {
                                lifo.vloz(nextIndex);
                                nextIndex = levyPotomek(nextIndex);
                            }
                        }

                        return result;
                    }
                };
            }

        }
        return null;
    }

    private int levyPotomek(int i) {
        return 2 * i + 1;
    }

    private int pravyPotomek(int i) {
        return 2 * i + 2;
    }

    private int rodic(int i) {
        return (i - 1) / 2;
    }
}
