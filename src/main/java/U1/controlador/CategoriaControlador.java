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

}