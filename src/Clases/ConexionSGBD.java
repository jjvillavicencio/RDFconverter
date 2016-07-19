/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Clases;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author jjvillavicencio
 */
public class ConexionSGBD {
    private int SGBD;
    private Connection con;
    private ArrayList tablas;
    private TablaAlias tablaAlias;
    private ArrayList descripcionTablas;
    private String Script = "";
    private String tabla;
    private String baseDatos;
    private String usuario;
    private String tablaRdf;

    public void setTablaAlias(TablaAlias tablaAlias) {
        this.tablaAlias = tablaAlias;
    }

    public String getTablaRdf() {
        return tablaRdf;
    }

    public ConexionSGBD() {
        this.descripcionTablas = new ArrayList();
        this.tablaAlias = new TablaAlias();
        this.tablas = new ArrayList();
    }

    public boolean crearConexionPostgreSql(String UsuarioDB, String HOST, String PUERTO, String NombreDB, String PASS) throws ClassNotFoundException {
        try {
            this.usuario = UsuarioDB;
            //System.out.println(connection);
            
            Class.forName("org.postgresql.Driver");
            if (con == null || con.isClosed() == true) {
                String cadenaConexion = "jdbc:postgresql://" + HOST + ":" + PUERTO + "/" + NombreDB;
                con = DriverManager.getConnection(cadenaConexion, UsuarioDB, PASS);
            }
            JOptionPane.showMessageDialog(null, "Conexión a POSTGRESQL exitosa");
            return true;
        } catch (SQLException ex) {
            System.out.println(ex);
            JOptionPane.showMessageDialog(null, "No se pudo conectar a POSTGRESQL");
            return false;
        }
    }

    public Connection getConexionPostgreSql() {
        return con;
    }
    /*
     * Con este metodo cerramos la conexion una vez hayamos terminado de usar la
     * base de datos
     */

    public void cerrarPosgreSql() throws SQLException {
        if (con != null && con.isClosed() == false) {
            con.close();
        }
    }


    //Obtener todas las tablas de la Base de Datos seleccionada
    public Boolean todasTablas() {

        try {
            String _selectDB = "";
            String _showTablas = "";
            Statement stmt = con.createStatement();
            ResultSet rs;

            if (SGBD == 0) {
                //Crear sentencia para MySql "SHOW TABLES"
                _showTablas = "SELECT tablename as TABLE_NAME FROM pg_tables WHERE schemaname = 'public' ";
            } else {
                //PARA MYSQL
            }

            //Ejecutar sentencia
            System.out.println(_showTablas);
            rs = stmt.executeQuery(_showTablas);
            //Recorrer resultado de la consulta asignar en un ArrayList
            while (rs.next()) {
                String r = rs.getString("TABLE_NAME");
                System.out.println(r);
                tablas.add(r);
            }

            return true;
        } catch (SQLException ex) {
            System.out.println(ex);
            JOptionPane.showMessageDialog(null, "No se pudo cargar las tablas");
            return false;
        }
    }

//Retorna ArrayList con todas las tablas
    public ArrayList getTodasTablas() {
        return tablas;
    }

    //Obtener descripción de la tabla (nombre de columnas y clave primaria)
    public Boolean describirTabla(Connection con, String tabla) {
        String _desTabla;
        String _primaria = "";
        String pri = "no hay";
        String columna = "";
        try {
            Statement stmt = con.createStatement();
            ResultSet rs;
            ResultSet rs2;

            if (SGBD == 0) {
                _desTabla = "SELECT `COLUMN_NAME`, `COLUMN_KEY` FROM `COLUMNS` WHERE `TABLE_NAME`= '" + tabla + "' AND `TABLE_SCHEMA` = '" + this.baseDatos + "'";
                pri = "PRI";
                columna = "COLUMN_KEY";
            } else {
                _desTabla = "select COLUMN_NAME from ALL_TAB_COLUMNS where OWNER = '" + usuario.toUpperCase() + "' AND TABLE_NAME = '" + tabla.toUpperCase() + "'";
                _primaria = "SELECT COLUMN_NAME, TABLE_NAME, CONSTRAINT_NAME\n"
                        + "FROM ALL_CONS_COLUMNS\n"
                        + "WHERE TABLE_NAME LIKE '" + tabla + "'\n"
                        + "AND OWNER LIKE '" + this.usuario.toUpperCase() + "'\n"
                        + "AND CONSTRAINT_NAME IN\n"
                        + "(SELECT CONSTRAINT_NAME\n"
                        + "FROM ALL_CONSTRAINTS\n"
                        + "WHERE CONSTRAINT_TYPE = 'P'\n"
                        + ")\n"
                        + "ORDER BY POSITION";
                rs2 = stmt.executeQuery(_primaria);
                while (rs2.next()) {
                    pri = rs2.getString("COLUMN_NAME");
                    columna = "COLUMN_NAME";
                }

            }

            //Nuevo objeto Alias (Original,Alias,Key)
            Alias nuevo = new Alias();
            //Crear consulta "DESCRIBE"
            System.out.println(_desTabla);
            //Ejecutar consulta
            rs = stmt.executeQuery(_desTabla);
            //Asignar valores al objeto Alias
            nuevo.setOriginal("Prefijo Recurso");
            nuevo.setAlias(" ");
            nuevo.setKey(" ");
            //Asignar objeto Alias a un ArrayList de objetos tipo Alias
            tablaAlias.setTablaAlias(nuevo);
            //Asignar valores al objeto Alias
            nuevo = new Alias();
            nuevo.setOriginal(tabla);
            nuevo.setAlias(tabla);
            nuevo.setKey(" ");
            //Asignar objeto Alias a un ArrayList de objetos tipo Alias
            tablaAlias.setTablaAlias(nuevo);
            //Recorrer resultado obtenido en la consulta "DESCRIBE..."
            try {
                while (rs.next()) {
                    //Inicializar variable de tipo Alias
                    nuevo = new Alias();
                    //Obtener valor de la columna FIELD en la consulta
                    String r = rs.getString("COLUMN_NAME");
                    System.out.println("-->" + r);
                    //Verificar si es Clave Primaria
                    if (pri.equals(rs.getString(columna))) {
                        //Asignar valor en campo Key en objeto Alias
                        nuevo.setKey("PRI");
                        //Asignar valor en campo Alias en objeto Alias
                        nuevo.setAlias(r);
                        //Asignar valor en campo Original en el objeto Alias
                        nuevo.setOriginal(r);
                    } else {
                        //Asignar valor en campo Key en objeto Alias
                        nuevo.setKey(" ");
                        //Asignar valor en campo Original en el objeto Alias
                        nuevo.setOriginal(r);
                        //Asignar valor en campo Alias en objeto Alias
                        nuevo.setAlias(r);
                    }

                    //Asignar objeto Alias a un ArrayList de objetos tipo Alias
                    tablaAlias.setTablaAlias(nuevo);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "La tabla seleccionada no tiene PRIMARY KEY");
                return false;
            }
            this.tabla = tabla;
            return true;
        } catch (SQLException ex) {
            System.out.println(ex);
            JOptionPane.showMessageDialog(null, "Error al describir tabla");
            return false;
        }
    }

    //Retorna ArrayList de tipo Alias con la descripción de la tabla
    public TablaAlias getDesTablaALias() {
        return this.tablaAlias;
    }

    public boolean crearTablaRdf(TablaAlias tabla) {
        Boolean bandera = false;
        String nombreTabla = " ";
        String clavePrimaria = " ";
        String _createTable = "";
        try {
            nombreTabla = JOptionPane.showInputDialog(null, "Ingrese el nombre de la Tabla en RDF");
            System.out.println("nomb" + nombreTabla);

            if (null == nombreTabla) {
                bandera = false;
            } else {
                this.tablaRdf = nombreTabla;
                try {
                    Statement stmt = con.createStatement();
                    Statement stmt2 = con.createStatement();
                    ResultSet rs;
                    if (SGBD == 0) {
                        String _selectDB = "USE " + this.baseDatos;
                        //Ejecutar sentencia
                        stmt.executeQuery(_selectDB);
                        _createTable = "CREATE TABLE IF NOT EXISTS " + nombreTabla + " (sujeto varchar(250), predicado varchar(250), objeto varchar(250))";

                    } else {
                        _createTable = "CREATE TABLE " + nombreTabla + " (sujeto varchar(250), predicado varchar(250), objeto varchar(250))";

                    }
                    System.out.println(_createTable);
                    stmt.execute(_createTable);
                    String _leerValores = "select * from " + tabla.getTablaAlias().get(1).getOriginal();
                    System.out.println(_leerValores);
                    rs = stmt.executeQuery(_leerValores);
                    for (int i = 2; i < tabla.getTamaño(); i++) {
                        if ("PRI".equals(tabla.getTablaAlias().get(i).getKey())) {
                            clavePrimaria = tabla.getTablaAlias().get(i).getOriginal();
                        }
                    }
                    int cont = 2;

                    Alias nuevo = new Alias();
                    nuevo.setOriginal("tipo de recurso");
                    nuevo.setAlias("tipo_recurso");
                    tabla.getTablaAlias().add(2, nuevo);

                    while (rs.next()) {
                        tabla.setTamaño();
                        if (cont < tabla.getTamaño()) {
                            //for (int i = 2; i < tabla.getTamaño(); i++) {
                            for (int j = 2; j < tabla.getTamaño(); j++) {
                                String Objeto;
                                String TipoRecurso = tabla.getTablaAlias().get(0).getAlias() + "" + tabla.getTablaAlias().get(1).getAlias();
                                String Sujeto = tabla.getTablaAlias().get(0).getAlias() + "" + tabla.getTablaAlias().get(1).getAlias() + "/" + rs.getString(clavePrimaria);
                                //System.out.print(Sujeto);
                                String Predicado = tabla.getTablaAlias().get(j).getAlias();
                                //System.out.print("\t" + Predicado);
                                if (j == 2) {
                                    Objeto = TipoRecurso;
                                } else {
                                    Objeto = rs.getString(tabla.getTablaAlias().get(j).getOriginal());
                                }
                                //System.out.println("\t" + Objeto);

                                String _insert = "INSERT INTO " + nombreTabla + " VALUES ('" + Sujeto + "','" + Predicado + "','" + Objeto + "')";
                                //Script = Script + _insert+"\n";
                                System.out.println(_insert);
                                stmt2.execute(_insert);
                            }
                            cont = cont + 1;
                        }
                    }
                    JOptionPane.showMessageDialog(null, "Tabla convertida exitosamente.");

                } catch (SQLException ex) {
                    System.out.println(ex);
                }
                bandera = true;
            }
        } catch (IndexOutOfBoundsException ex) {
            System.out.println(ex);
        }
        return bandera;
    }

    public String getScript() {
        return Script;
    }

    public String getTabla() {
        return tabla;
    }

    public String getBaseDatos() {
        return baseDatos;
    }

    public void setBaseDatos(String baseDatos) {
        this.baseDatos = baseDatos;
    }

    public int getSGBD() {
        return SGBD;
    }

    public void setSGBD(int SGBD) {
        this.SGBD = SGBD;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Connection getCon() {
        return con;
    }
}
