package U1.controlador;

import U1.modelo.Categoria;
import U1.vista.VentanaEmpresa;
import U1.util.ConexionJPA;
import jakarta.persistence.EntityManager;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class CategoriaControlador {
    private final VentanaEmpresa vista;

    public CategoriaControlador(VentanaEmpresa vista) {
        this.vista = vista;
    }

    private void mostrarResultado(String texto, String... etiquetas) {
        vista.mostrarResultado(vista.tbl_JPQLCategoria, texto, etiquetas);
    }
    public void Clistar() {
        DefaultTableModel modelo = new DefaultTableModel(
                new String[]{"ID", "Nombre", "Descripción", "Estado"}, 0);
        vista.tbl_CCategorias.setModel(modelo);

        try (EntityManager em = ConexionJPA.crearEntityManager()) {
            List<Categoria> lista = em.createQuery("SELECT c FROM Categoria c ORDER BY c.id", Categoria.class).getResultList();
            for (Categoria c : lista) {
                modelo.addRow(new Object[]{c.getId(), c.getNombre(), c.getDescripcion(), c.isEstado()});
            }
        }
    }

    public void CategoriasTotal() {
        vista.tbl_JPQLCategoria.setEnabled(true);
        try (EntityManager em = ConexionJPA.crearEntityManager()) {
            List<Categoria> categorias = em.createQuery(
                    "SELECT c FROM Categoria c ORDER BY c.id ASC", Categoria.class
            ).getResultList();
            StringBuilder resultado = new StringBuilder();
            for (Categoria c : categorias) {
                resultado.append(" - ID: ")
                        .append(c.getId())
                        .append(" - Nombre: ")
                        .append(c.getNombre())
                        .append(" - Descripción: ")
                        .append(c.getDescripcion())
                        .append(" - Estado: ")
                        .append(c.isEstado())
                        .append("\n");
            }
            mostrarResultado(resultado.toString(), "ID", "Nombre", "Descripción", "Estado");
        }


    }

    public void CategoriasActivas() {
        vista.tbl_JPQLCategoria.setEnabled(true);
        try (EntityManager em = ConexionJPA.crearEntityManager()) {
            List<Categoria> activas = em.createQuery(
                    "SELECT c FROM Categoria c WHERE c.estado = true ORDER BY c.id ASC", Categoria.class
            ).getResultList();

            StringBuilder resultado = new StringBuilder();
            for (Categoria c : activas) {
                resultado.append(" - ID: ")
                        .append(c.getId())
                        .append(" - Nombre: ")
                        .append(c.getNombre())
                        .append("\n");
            }
            mostrarResultado(resultado.toString(), "ID", "Nombre");
        }
    }

    public void CategoriasConProductos() {
        vista.tbl_JPQLCategoria.setEnabled(true);
        try (EntityManager em = ConexionJPA.crearEntityManager()) {
            List<Object[]> categorias = em.createQuery(
                    "SELECT DISTINCT c.id, p.nombre, c.nombre, p.nombre FROM Categoria c " +
                            "JOIN c.productos p ORDER BY c.id ASC",
                    Object[].class
            ).getResultList();
            StringBuilder resultado = new StringBuilder();
            for (Object[] p : categorias) {
                resultado.append(" - ID: ")
                        .append(p[0])
                        .append(" - Producto: ")
                        .append(p[1])
                        .append(" - Categoría: ")
                        .append(p[2])
                        .append(" - Producto: ")
                        .append(p[3])
                        .append("\n");
            }

            mostrarResultado(resultado.toString(), "ID", "Producto:", "Categoría:", "Producto:");
        }
    }
}