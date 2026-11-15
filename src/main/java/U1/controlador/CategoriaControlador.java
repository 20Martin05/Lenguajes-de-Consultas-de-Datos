package U1.controlador;

import U1.modelo.Categoria;
import U1.vista.VentanaEmpresa;
import U1.util.ConexionJPA;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import javax.swing.*;
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

    private boolean nombreValido() {
        if (vista.cmb_CNombre.getSelectedItem() == null
                || vista.cmb_CNombre.getSelectedItem().toString().isBlank()) {
            JOptionPane.showMessageDialog(vista, "Selecciona un nombre de categoría.");
            return false;
        }
        return true;
    }

    public void Cguardar() {
        if (!nombreValido()) return;

        EntityManager em = ConexionJPA.crearEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            Categoria nuevo = new Categoria();
            nuevo.setNombre(vista.cmb_CNombre.getSelectedItem().toString());
            nuevo.setDescripcion(vista.txt_CDescripcion.getText().trim());
            nuevo.setEstado(vista.cmb_CEstado.getSelectedIndex() == 0);
            tx.begin();
            em.persist(nuevo);
            tx.commit();
            JOptionPane.showMessageDialog(vista, "Categoría guardada con éxito");
            Clistar();
        } catch (Exception ex) {
            if (tx.isActive()) tx.rollback();
            JOptionPane.showMessageDialog(vista, "Error al guardar categoría: " + ex.getMessage());
        } finally {
            if (em.isOpen()) em.close();
        }
    }

    public void Cactualizar() {
        int id = idSeleccionado();
        if (id == -1) return;
        if (!nombreValido()) return;

        EntityManager em = ConexionJPA.crearEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Categoria categoria = em.find(Categoria.class, id);
            if (categoria == null) throw new IllegalStateException("La categoría ya no existe");
            categoria.setNombre(vista.cmb_CNombre.getSelectedItem().toString());
            categoria.setDescripcion(vista.txt_CDescripcion.getText().trim());
            categoria.setEstado(vista.cmb_CEstado.getSelectedIndex() == 0);
            tx.commit();
            JOptionPane.showMessageDialog(vista, "Categoría actualizada con éxito");
            Clistar();
        } catch (Exception ex) {
            if (tx.isActive()) tx.rollback();
            JOptionPane.showMessageDialog(vista, "Error al actualizar categoría: " + ex.getMessage());
        } finally {
            if (em.isOpen()) em.close();
        }
    }

    public void Celiminar() {
        int id = idSeleccionado();
        if (id == -1) return;

        if (JOptionPane.showConfirmDialog(vista, "¿Estás seguro de eliminar esta categoría?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;

        try (EntityManager em = ConexionJPA.crearEntityManager()) {
            long productos = em.createQuery(
                            "SELECT COUNT(p) FROM Productos p WHERE p.categoriaRelacion.id = :id", Long.class)
                    .setParameter("id", id)
                    .getSingleResult();
            if (productos > 0) {
                JOptionPane.showMessageDialog(vista, "No se puede eliminar: la categoría tiene " + productos + " producto(s).");
                return;
            }

            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                em.createQuery("DELETE FROM Categoria c WHERE c.id = :id").setParameter("id", id).executeUpdate();
                tx.commit();
                JOptionPane.showMessageDialog(vista, "Categoría eliminada con éxito");
                Clistar();
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                JOptionPane.showMessageDialog(vista, "No se pudo eliminar la categoría: " + ex.getMessage());
            }
        }
    }

    public void Climpiar() {
        vista.cmb_CNombre.setSelectedIndex(0);
        vista.txt_CDescripcion.setText("");
        vista.cmb_CEstado.setSelectedIndex(0);
    }

    private int idSeleccionado() {
        int filaVista = vista.tbl_CCategorias.getSelectedRow();
        if (filaVista < 0) {
            JOptionPane.showMessageDialog(vista, "Selecciona una categoría de la tabla primero");
            return -1;
        }
        return ((Number) vista.tbl_CCategorias.getValueAt(vista.tbl_CCategorias.convertRowIndexToModel(filaVista), 0)).intValue();
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