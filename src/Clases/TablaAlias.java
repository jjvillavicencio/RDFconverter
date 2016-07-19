/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Clases;

import java.util.ArrayList;

/**
 *
 * @author jjvillavicencio
 */
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
