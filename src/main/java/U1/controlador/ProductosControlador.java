package U1.controlador;

import U1.modelo.Productos;
import U1.vista.VentanaEmpresa;
import U1.util.ConexionJPA;
import jakarta.persistence.EntityManager;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class ProductosControlador {
    private final VentanaEmpresa vista;

    public ProductosControlador(VentanaEmpresa vista) {
        this.vista = vista;
    }


    private DefaultTableModel crearModelo() {
        return new DefaultTableModel(new String[]{"ID", "Categoria ID", "Nombre", "Precio", "Cantidad"}, 0);
    }

    private void llenar(DefaultTableModel modelo, List<Productos> lista) {
        for (Productos p : lista) {
            Integer catId = p.getCategoriaRelacion() == null ? null : p.getCategoriaRelacion().getId();
            modelo.addRow(new Object[]{p.getId(), catId, p.getNombre(), p.getPrecio(), p.getCantidad()});
        }
    }

    public void Plistar() {
        DefaultTableModel modelo = crearModelo();
        vista.tbl_PProductos.setRowSorter(null);
        vista.tbl_PProductos.setAutoCreateColumnsFromModel(true);
        try (EntityManager em = ConexionJPA.crearEntityManager()) {
            llenar(modelo, em.createQuery("FROM Productos p ORDER BY p.id ASC", Productos.class).getResultList());
        }
        vista.tbl_PProductos.setModel(modelo);
    }



    public List<Productos> PbuscarPorNombre(String nombre) {
        try (EntityManager em = ConexionJPA.crearEntityManager()) {
            return em.createQuery("SELECT p FROM Productos p WHERE LOWER(p.nombre) LIKE LOWER(:nombre) ORDER BY p.id ASC", Productos.class)
                    .setParameter("nombre", "%" + nombre + "%")
                    .getResultList();
        }
    }

    public void PbuscarYMostrar(String texto) {
        DefaultTableModel modelo = crearModelo();
        vista.tbl_PProductos.setRowSorter(null);
        vista.tbl_PProductos.setAutoCreateColumnsFromModel(true);
        llenar(modelo, PbuscarPorNombre(texto));
        vista.tbl_PProductos.setModel(modelo);
    }

    private void mostrarResultado(String texto, String... etiquetas) {
        vista.mostrarResultado(vista.tbl_JPQLProductos, texto, etiquetas);
    }

    public void ProductosTotal() {
        try (EntityManager em = ConexionJPA.crearEntityManager()) {
            List<Object[]> productos = em.createQuery(
                    "SELECT p.id, p.nombre, p.precio, p.cantidad FROM Productos p ORDER BY p.id ASC",
                    Object[].class
            ).getResultList();
            StringBuilder resultado = new StringBuilder();
            for (Object[] p : productos) {
                resultado.append(" - ID: ")
                        .append(p[0])
                        .append(" - Nombre: ")
                        .append(p[1])
                        .append(" - Precio: ")
                        .append(p[2])
                        .append(" - Cantidad: ")
                        .append(p[3])
                        .append("\n");
            }
            mostrarResultado(resultado.toString(), "ID", "Nombre", "Precio", "Cantidad");
        }
    }

    public void MaximoYMinimoPrecio() {
        try (EntityManager em = ConexionJPA.crearEntityManager()) {
            Object[] precio = em.createQuery(
                    "SELECT MAX(p.precio), MIN(p.precio) FROM Productos p",
                    Object[].class
            ).getSingleResult();
            StringBuilder resultado = new StringBuilder();
            resultado.append("Máximo de precio: ")
                    .append(precio[0])
                    .append("\nMínimo de precio: ")
                    .append(precio[1]);
            mostrarResultado(resultado.toString(), "Máximo de precio:", "Mínimo de precio:");
        }
    }

    public void MaximoYMinimoCantidad() {
        try (EntityManager em = ConexionJPA.crearEntityManager()) {
            Object[] cantidad= em.createQuery(
                    "SELECT MAX(p.cantidad), MIN(p.cantidad) FROM Productos p",
                    Object[].class
            ).getSingleResult();
            StringBuilder resultado = new StringBuilder();
            resultado.append("Máxima cantidad: ")
                    .append(cantidad[0])
                    .append("\nMínima cantidad: ")
                    .append(cantidad[1]);
            mostrarResultado(resultado.toString(), "Máxima cantidad:", "Mínima cantidad:");
        }
    }

}