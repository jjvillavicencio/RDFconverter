package Clases;

import java.util.ArrayList;

/**
 *
 * 
 * @author John Villavicencio
 * @author Vanessa Sotomayor
 * @author Jackson Masache
 * 
 * 
 **/

public class TablaAlias {

    private ArrayList<Alias> nuevo;
    private int tamaño;

    public TablaAlias() {
        this.nuevo = new ArrayList<Alias>();
    }

    public ArrayList<Alias> getTablaAlias() {
        return nuevo;
    }

    public void setTablaAlias(Alias columna) {
        this.nuevo.add(columna);
    }

    public int getTamaño() {
        return tamaño;
    }

    public void setTamaño() {
        this.tamaño = nuevo.size();
    }

}
