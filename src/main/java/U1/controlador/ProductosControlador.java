package U1.controlador;

import U1.modelo.Productos;
import U1.modelo.Categoria;
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
        try (EntityManager em = ConexionJPA.crearEntityManager()) {
            llenar(modelo, em.createQuery("FROM Productos", Productos.class).getResultList());
        }
        vista.tbl_PProductos.setModel(modelo);
    }



    public List<Productos> PbuscarPorNombre(String nombre) {
        try (EntityManager em = ConexionJPA.crearEntityManager()) {
            return em.createQuery("SELECT p FROM Productos p WHERE LOWER(p.nombre) LIKE LOWER(:nombre) ORDER BY p.nombre", Productos.class)
                    .setParameter("nombre", "%" + nombre + "%")
                    .getResultList();
        }
    }

    public void PbuscarYMostrar(String texto) {
        DefaultTableModel modelo = crearModelo();
        llenar(modelo, PbuscarPorNombre(texto));
        vista.tbl_PProductos.setModel(modelo);
    }

}